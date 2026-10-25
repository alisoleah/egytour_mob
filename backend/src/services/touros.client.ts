/**
 * Server-to-server client for the Tour_os Hotel PMS (pass validation).
 *
 * Outbound policy (hard requirements), enforced in guardedPmsFetch where the
 * fetch happens:
 * - Only http/https; redirects rejected (no redirect hops to internal hosts).
 * - The PMS origin is pinned from TOUROS_API_URL; every request URL must match
 *   that exact origin.
 * - The hostname — and every address it actually resolves to — must never be
 *   localhost, loopback, private, link-local or otherwise reserved
 *   (DNS-rebinding safe). Developing against a locally running Tour_os
 *   instance requires a tunnel/host.
 *
 * When TOUROS_API_URL is not configured the client falls back to dev mode:
 * codes listed in DEV_PASS_CODES activate the VIP tier locally, so the whole
 * flow is demoable without a deployed PMS.
 */
import { lookup } from 'dns/promises';

const ALLOWED_PROTOCOLS = new Set(['http:', 'https:']);

function isIPv4(hostname: string): boolean {
    return /^\d{1,3}(\.\d{1,3}){3}$/.test(hostname);
}

function isBlockedIPv4(hostname: string): boolean {
    const [a, b] = hostname.split('.').map(Number);
    if (a === 127 || a === 0 || a === 10) return true; // loopback, "this" net, private
    if (a === 172 && b >= 16 && b <= 31) return true; // private
    if (a === 192 && b === 168) return true; // private
    if (a === 169 && b === 254) return true; // link-local (cloud metadata)
    if (a === 100 && b >= 64 && b <= 127) return true; // CGNAT
    if (a >= 224) return true; // multicast / reserved
    return false;
}

function isBlockedAddress(address: string): boolean {
    const host = address.toLowerCase().replace(/^\[|\]$/g, '');
    if (host === 'localhost' || host.endsWith('.localhost')) return true;
    if (host === '::1' || host === '::' || host === '0.0.0.0') return true;
    if (isIPv4(host)) return isBlockedIPv4(host);
    if (/^f[cd][0-9a-f]{2}:/.test(host)) return true; // IPv6 unique-local
    if (/^fe[89ab][0-9a-f]:/.test(host)) return true; // IPv6 link-local
    if (host.startsWith('::ffff:')) return isBlockedAddress(host.slice(7)); // IPv4-mapped
    return false;
}

export interface TourosPassInfo {
    valid: boolean;
    experienceId?: string;
    experienceName?: string;
    description?: string;
    guestName?: string;
    redeemedAt?: string | null;
}

export class TourosError extends Error {
    constructor(message: string) {
        super(message);
        this.name = 'TourosError';
    }
}

/** The single place the PMS origin is parsed and pinned. */
function pmsOrigin(): URL {
    const raw = process.env.TOUROS_API_URL;
    if (!raw) {
        throw new TourosError('TOUROS_API_URL is not configured');
    }
    const origin = new URL(raw);
    if (!ALLOWED_PROTOCOLS.has(origin.protocol)) {
        throw new TourosError(`Blocked Tour_os URL with disallowed scheme: ${origin.protocol}`);
    }
    if (origin.username || origin.password) {
        throw new TourosError('Blocked Tour_os URL containing embedded credentials');
    }
    if (isBlockedAddress(origin.hostname)) {
        throw new TourosError(
            `Blocked Tour_os request to internal host: ${origin.hostname}. ` +
                'The PMS must be reached at a public URL (tunnel local instances for development).',
        );
    }
    return origin;
}

const dnsCache = new Map<string, { checkedAt: number }>();
const DNS_TTL_MS = 30_000;

/** Resolve the hostname now and reject private/resolved-internal answers. */
async function assertHostResolvesPublic(hostname: string): Promise<void> {
    const cached = dnsCache.get(hostname);
    if (cached && Date.now() - cached.checkedAt < DNS_TTL_MS) {
        return;
    }
    let addresses: { address: string }[];
    try {
        addresses = await lookup(hostname, { all: true });
    } catch {
        throw new TourosError(`Could not resolve Tour_os host: ${hostname}`);
    }
    for (const { address } of addresses) {
        if (isBlockedAddress(address)) {
            throw new TourosError(
                `Tour_os host ${hostname} resolves to internal address ${address} — blocked (DNS rebinding protection)`,
            );
        }
    }
    dnsCache.set(hostname, { checkedAt: Date.now() });
}

/**
 * The ONLY function that talks to the PMS. Validates the URL (scheme, no
 * embedded credentials, exact origin match with the pinned PMS origin, host
 * not internal), verifies DNS resolves publicly, disables redirects, and
 * enforces a timeout — all in this scope, immediately before the fetch.
 */
async function guardedPmsFetch(
    rawUrl: string,
    init: RequestInit,
    token: string | null,
): Promise<Response> {
    const origin = pmsOrigin();
    const url = new URL(rawUrl);
    if (!ALLOWED_PROTOCOLS.has(url.protocol)) {
        throw new TourosError(`Blocked Tour_os URL with disallowed scheme: ${url.protocol}`);
    }
    if (url.username || url.password) {
        throw new TourosError('Blocked Tour_os URL containing embedded credentials');
    }
    if (url.origin !== origin.origin) {
        throw new TourosError(
            `Blocked Tour_os request outside the pinned origin (${origin.origin}): ${url.origin}`,
        );
    }
    if (isBlockedAddress(url.hostname)) {
        throw new TourosError(`Blocked Tour_os request to internal host: ${url.hostname}`);
    }
    await assertHostResolvesPublic(url.hostname);

    const controller = new AbortController();
    const timer = setTimeout(() => controller.abort(), 15000);
    try {
        return await fetch(url, {
            ...init,
            redirect: 'error', // no redirect hops to unvalidated hosts
            signal: controller.signal,
            headers: {
                ...(init.headers ?? {}),
                ...(token ? { Authorization: `Bearer ${token}` } : {}),
            },
        });
    } finally {
        clearTimeout(timer);
    }
}

export class TourosClient {
    private token: string | null = null;

    isLive(): boolean {
        return Boolean(process.env.TOUROS_API_URL);
    }

    /**
     * Validate a hotel-issued pass code. Live mode authenticates as a PMS
     * service account and calls GET /experiences/access/:code.
     */
    async validateAccess(code: string): Promise<TourosPassInfo> {
        const base = this.pmsBase();
        if (!base) {
            return this.devValidate(code);
        }

        const response = await this.authedFetch(
            `${base}/experiences/access/${encodeURIComponent(code)}`,
            { headers: { Accept: 'application/json' } },
        );
        if (response.status === 404) {
            return { valid: false };
        }
        if (!response.ok) {
            throw new TourosError(`Tour_os validation failed with ${response.status}`);
        }
        return (await response.json()) as TourosPassInfo;
    }

    /** Mark the code redeemed in the PMS (single use). Best-effort: returns success. */
    async redeem(code: string): Promise<boolean> {
        const base = this.pmsBase();
        if (!base) {
            return true; // dev mode: nothing to redeem server-side
        }

        const response = await this.authedFetch(
            `${base}/experiences/redeem/${encodeURIComponent(code)}`,
            { method: 'POST' },
        );
        return response.ok;
    }

    /** Dev fallback: DEV_PASS_CODES (comma-separated) activates the VIP pass. */
    private devValidate(code: string): TourosPassInfo {
        const devCodes = (process.env.DEV_PASS_CODES ?? '')
            .split(',')
            .map((c) => c.trim().toUpperCase())
            .filter((c) => c.length > 0);

        if (devCodes.includes(code.toUpperCase())) {
            return {
                valid: true,
                experienceId: 'egytour-vip-all-access',
                experienceName: 'EgyTour VIP All-Access Pass',
                description: 'Development pass — all content tiers unlocked.',
            };
        }
        return { valid: false };
    }

    private pmsBase(): string | null {
        const raw = process.env.TOUROS_API_URL;
        return raw && raw.length > 0 ? raw.replace(/\/$/, '') : null;
    }

    /** Authenticated fetch with one re-login retry on token expiry. */
    private async authedFetch(rawUrl: string, init: RequestInit): Promise<Response> {
        if (!this.token) {
            await this.login();
        }
        let response = await guardedPmsFetch(rawUrl, init, this.token);
        if (response.status === 401) {
            await this.login();
            response = await guardedPmsFetch(rawUrl, init, this.token);
        }
        return response;
    }

    /** Service-account login against the PMS (email + password env pair). */
    private async login(): Promise<void> {
        const base = this.pmsBase();
        if (!base) {
            throw new TourosError('TOUROS_API_URL is not configured');
        }
        const email = process.env.TOUROS_SERVICE_EMAIL;
        const password = process.env.TOUROS_SERVICE_PASSWORD;
        if (!email || !password) {
            throw new TourosError(
                'TOUROS_SERVICE_EMAIL / TOUROS_SERVICE_PASSWORD are required for live pass validation',
            );
        }

        const response = await guardedPmsFetch(`${base}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password }),
        }, null);

        if (!response.ok) {
            throw new TourosError(`Tour_os service-account login failed (${response.status})`);
        }
        const payload = (await response.json()) as { data?: { accessToken?: string } };
        const token = payload.data?.accessToken;
        if (!token) {
            throw new TourosError('Tour_os login response contained no access token');
        }
        this.token = token;
    }
}
