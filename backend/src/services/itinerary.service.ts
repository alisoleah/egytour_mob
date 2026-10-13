import { GoogleGenerativeAI } from '@google/generative-ai';
import { db } from '../db';
import { itineraries, itineraryStops, pointsOfInterest, sites } from '../db/schema';
import { eq, inArray } from 'drizzle-orm';

export interface ItineraryStopData {
    poiId?: string;
    title: string;
    region: string;
    orderIndex: number;
    recommendedDurationMinutes: number;
    notes: string;
}

export interface ItineraryDayData {
    dayNumber: number;
    theme: string;
    region: string;
    stops: ItineraryStopData[];
}

export interface GeneratedItinerary {
    id?: string;
    title: string;
    durationDays: number;
    regions: string[];
    days: ItineraryDayData[];
    createdAt: string;
}

export interface GenerateItineraryRequest {
    durationDays: number;
    regions: string[];
    interests?: string[];
    expertiseLevel?: string;
    startDate?: string;
    userId?: string;
}

export class ItineraryGenerator {
    private genAI: GoogleGenerativeAI | null = null;

    constructor() {
        const apiKey = process.env.GEMINI_API_KEY;
        if (apiKey && apiKey !== 'your-gemini-api-key') {
            this.genAI = new GoogleGenerativeAI(apiKey);
        }
    }

    async generateItinerary(request: GenerateItineraryRequest): Promise<GeneratedItinerary> {
        // 1. If Gemini is available, attempt AI generation
        if (this.genAI) {
            try {
                return await this.generateWithGemini(request);
            } catch (err) {
                console.warn('⚠️ Gemini itinerary generation failed, using curated plan generator:', err);
            }
        }

        // 2. Fallback to curated intelligent itinerary synthesis
        return this.generateCuratedPlan(request);
    }

    private async generateWithGemini(request: GenerateItineraryRequest): Promise<GeneratedItinerary> {
        const model = this.genAI!.getGenerativeModel({ model: 'gemini-1.5-flash' });

        const prompt = `You are an expert Egyptologist and travel planning director for EgyTour.
Create an optimized ${request.durationDays}-day Egypt itinerary based on these traveler preferences:
- Duration: ${request.durationDays} days
- Target Regions: ${(request.regions || ['cairo', 'luxor']).join(', ')}
- Interests: ${(request.interests || ['ancient_history', 'architecture']).join(', ')}
- Expertise Level: ${request.expertiseLevel || 'casual'}

REQUIREMENTS:
1. Group sites geographically to minimize transit friction.
2. Provide 2 to 4 high-value stops per day.
3. Include realistic visit durations (60-180 minutes).
4. Return ONLY valid JSON matching this exact structure:
{
  "title": "Grand 3-Day Pharaohs & Pyramids Journey",
  "days": [
    {
      "dayNumber": 1,
      "theme": "The Wonders of Giza & Memphis",
      "region": "cairo",
      "stops": [
        {
          "title": "Great Pyramid of Khufu",
          "region": "cairo",
          "orderIndex": 0,
          "recommendedDurationMinutes": 150,
          "notes": "Explore the plateau and the monumental King's Chamber."
        },
        {
          "title": "The Great Sphinx & Valley Temple",
          "region": "cairo",
          "orderIndex": 1,
          "recommendedDurationMinutes": 90,
          "notes": "Marvel at Khafre's leonine portrait and the Dream Stele."
        }
      ]
    }
  ]
}`;

        const result = await model.generateContent(prompt);
        const text = result.response.text();
        const jsonMatch = text.match(/\{[\s\S]*\}/);
        if (!jsonMatch) {
            throw new Error('No valid JSON generated');
        }

        const parsed = JSON.parse(jsonMatch[0]);
        return {
            id: 'itin-' + Date.now(),
            title: parsed.title || `${request.durationDays}-Day Egyptian Discovery`,
            durationDays: request.durationDays,
            regions: request.regions,
            days: parsed.days || [],
            createdAt: new Date().toISOString()
        };
    }

    private generateCuratedPlan(request: GenerateItineraryRequest): GeneratedItinerary {
        const daysCount = Math.max(1, Math.min(request.durationDays, 7));
        const regions = request.regions.length > 0 ? request.regions : ['cairo', 'luxor'];

        const days: ItineraryDayData[] = [];

        // Day 1: Giza Plateau
        days.push({
            dayNumber: 1,
            theme: 'Giza Plateau: Pyramids & The Sphinx',
            region: 'cairo',
            stops: [
                {
                    poiId: 'giza-1',
                    title: 'Great Pyramid of Khufu',
                    region: 'cairo',
                    orderIndex: 0,
                    recommendedDurationMinutes: 150,
                    notes: 'Morning exploration of the sole surviving Ancient Wonder of the World.'
                },
                {
                    poiId: 'giza-2',
                    title: 'The Great Sphinx & Valley Temple',
                    region: 'cairo',
                    orderIndex: 1,
                    recommendedDurationMinutes: 90,
                    notes: 'View the 73-meter limestone guardian and Thutmose IV\'s Dream Stele.'
                },
                {
                    title: 'Grand Egyptian Museum (GEM)',
                    region: 'cairo',
                    orderIndex: 2,
                    recommendedDurationMinutes: 180,
                    notes: 'Marvel at the full Tutankhamun collection and the Grand Staircase.'
                }
            ]
        });

        // Day 2: Historic Cairo & Saqqara
        if (daysCount >= 2) {
            days.push({
                dayNumber: 2,
                theme: 'Saqqara Step Pyramid & Historic Islamic Cairo',
                region: 'cairo',
                stops: [
                    {
                        title: 'Djoser Step Pyramid at Saqqara',
                        region: 'cairo',
                        orderIndex: 0,
                        recommendedDurationMinutes: 120,
                        notes: 'Visit the world\'s oldest monumental stone building designed by Imhotep.'
                    },
                    {
                        title: 'Khan el-Khalili Bazaar & Al-Mu\'izz Street',
                        region: 'cairo',
                        orderIndex: 1,
                        recommendedDurationMinutes: 120,
                        notes: 'Wander the 14th-century souk and medieval Mamluk architecture.'
                    }
                ]
            });
        }

        // Day 3: Luxor East Bank
        if (daysCount >= 3) {
            days.push({
                dayNumber: 3,
                theme: 'Thebes East Bank: Temples of Karnak & Luxor',
                region: 'luxor',
                stops: [
                    {
                        title: 'Karnak Temple Complex & Hypostyle Hall',
                        region: 'luxor',
                        orderIndex: 0,
                        recommendedDurationMinutes: 180,
                        notes: 'Walk among 134 towering papyrus columns in the largest religious complex ever built.'
                    },
                    {
                        poiId: 'luxor-1',
                        title: 'Luxor Temple by Sunset',
                        region: 'luxor',
                        orderIndex: 1,
                        recommendedDurationMinutes: 120,
                        notes: 'Witness illuminated statues of Ramesses II and the Avenue of Sphinxes.'
                    }
                ]
            });
        }

        // Day 4: Luxor West Bank
        if (daysCount >= 4) {
            days.push({
                dayNumber: 4,
                theme: 'Thebes West Bank: Valley of the Kings & Hatshepsut',
                region: 'luxor',
                stops: [
                    {
                        title: 'Valley of the Kings (KV62 & Royal Tombs)',
                        region: 'luxor',
                        orderIndex: 0,
                        recommendedDurationMinutes: 180,
                        notes: 'Descend into brightly painted underground tombs of the New Kingdom pharaohs.'
                    },
                    {
                        title: 'Mortuary Temple of Queen Hatshepsut',
                        region: 'luxor',
                        orderIndex: 1,
                        recommendedDurationMinutes: 120,
                        notes: 'Admire the terraced cliffs of Deir el-Bahari honouring Egypt\'s female pharaoh.'
                    },
                    {
                        title: 'Colossi of Memnon',
                        region: 'luxor',
                        orderIndex: 2,
                        recommendedDurationMinutes: 45,
                        notes: 'Photograph the towering 18-meter stone guardians of Amenhotep III.'
                    }
                ]
            });
        }

        // Day 5: Aswan
        if (daysCount >= 5) {
            days.push({
                dayNumber: 5,
                theme: 'Aswan: Nile Cataracts & Philae Island',
                region: 'aswan',
                stops: [
                    {
                        title: 'Temple of Isis at Philae Island',
                        region: 'aswan',
                        orderIndex: 0,
                        recommendedDurationMinutes: 150,
                        notes: 'Take a boat to the island sanctuary of Isis rescued by UNESCO.'
                    },
                    {
                        title: 'Unfinished Obelisk & Granite Quarries',
                        region: 'aswan',
                        orderIndex: 1,
                        recommendedDurationMinutes: 60,
                        notes: 'Discover how ancient stonemasons carved 1,000-ton obelisks from bedrock.'
                    }
                ]
            });
        }

        return {
            id: 'itin-' + Date.now(),
            title: `${daysCount}-Day Classical Egyptian Odyssey`,
            durationDays: daysCount,
            regions: regions,
            days: days,
            createdAt: new Date().toISOString()
        };
    }
}
