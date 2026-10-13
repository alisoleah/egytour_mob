# EgyTour - Sprint Execution Plan Part 3 (Sprints 5-6)

## 📅 Sprint 5: Trip Planning (Weeks 9-10)

### Sprint Goal
Implement AI-powered itinerary generation and trip planning features.

### Tasks

#### 5.1 Itinerary Generation Backend (Backend - Day 1-3)

```typescript
// services/trip-planner/src/generator.ts
import { GoogleGenerativeAI } from '@google/generative-ai';
import { db } from '../../packages/db';
import { pointsOfInterest, sites } from '../../packages/db/schema';
import { sql } from 'drizzle-orm';

interface ItineraryRequest {
    durationDays: number;
    regions: string[];
    interests: string[];
    expertiseLevel: string;
    startDate: string;
}

export class ItineraryGenerator {
    private genAI: GoogleGenerativeAI;
    
    constructor() {
        this.genAI = new GoogleGenerativeAI(process.env.GEMINI_API_KEY!);
    }
    
    async generateItinerary(request: ItineraryRequest) {
        // 1. Fetch relevant POIs
        const pois = await this.fetchRelevantPOIs(request);
        
        // 2. Generate optimized itinerary with AI
        const itinerary = await this.optimizeWithAI(pois, request);
        
        // 3. Save to database
        const savedItinerary = await this.saveItinerary(itinerary, request);
        
        return savedItinerary;
    }
    
    private async fetchRelevantPOIs(request: ItineraryRequest) {
        return await db.execute(sql`
            SELECT poi.*, s.name_en as site_name, s.region
            FROM points_of_interest poi
            JOIN sites s ON poi.site_id = s.id
            WHERE s.region = ANY(${request.regions})
            AND poi.importance_rank >= 5
            ORDER BY poi.importance_rank DESC
            LIMIT 50
        `);
    }
    
    private async optimizeWithAI(pois: any[], request: ItineraryRequest) {
        const model = this.genAI.getGenerativeModel({ model: 'gemini-pro' });
        
        const poisData = pois.rows.map(poi => ({
            id: poi.id,
            name: poi.name_en,
            site: poi.site_name,
            region: poi.region,
            importance: poi.importance_rank,
            type: poi.poi_type,
        }));
        
        const prompt = `You are an expert Egypt travel planner. Create an optimized ${request.durationDays}-day itinerary.

Available POIs:
${JSON.stringify(poisData, null, 2)}

Requirements:
- Duration: ${request.durationDays} days
- Regions: ${request.regions.join(', ')}
- Interests: ${request.interests.join(', ')}
- Expertise level: ${request.expertiseLevel}

Create a day-by-day itinerary that:
1. Groups POIs by geographic proximity
2. Balances travel time and sightseeing
3. Prioritizes high-importance sites
4. Matches the user's expertise level
5. Includes realistic time allocations (2-3 hours per major site)
6. Suggests 3-5 POIs per day maximum

Return ONLY valid JSON in this format:
{
  "title": "Suggested itinerary title",
  "days": [
    {
      "dayNumber": 1,
      "theme": "Day theme",
      "stops": [
        {
          "poiId": "uuid",
          "orderIndex": 0,
          "recommendedDurationMinutes": 120,
          "notes": "Why visit and what to see"
        }
      ]
    }
  ]
}`;
        
        const result = await model.generateContent(prompt);
        const responseText = result.response.text();
        
        // Extract JSON from response
        const jsonMatch = responseText.match(/\{[\s\S]*\}/);
        if (!jsonMatch) {
            throw new Error('Failed to generate valid itinerary');
        }
        
        return JSON.parse(jsonMatch[0]);
    }
    
    private async saveItinerary(itinerary: any, request: ItineraryRequest) {
        const [savedItinerary] = await db.insert(itineraries).values({
            userId: request.userId,
            title: itinerary.title,
            startDate: request.startDate,
            endDate: new Date(new Date(request.startDate).getTime() + request.durationDays * 24 * 60 * 60 * 1000),
            regions: request.regions,
            status: 'draft',
        }).returning();
        
        // Save stops
        const stops = itinerary.days.flatMap((day: any) =>
            day.stops.map((stop: any) => ({
                itineraryId: savedItinerary.id,
                poiId: stop.poiId,
                dayNumber: day.dayNumber,
                orderIndex: stop.orderIndex,
                recommendedDurationMinutes: stop.recommendedDurationMinutes,
            }))
        );
        
        await db.insert(itineraryStops).values(stops);
        
        return savedItinerary;
    }
}

// src/routes/itineraries.ts
const itineraryRoutes: FastifyPluginAsync = async (fastify) => {
    const generator = new ItineraryGenerator();
    
    fastify.post('/generate', async (request, reply) => {
        const itinerary = await generator.generateItinerary(request.body);
        return itinerary;
    });
    
    fastify.get('/', async (request, reply) => {
        const userId = request.user.id;
        const itineraries = await db.select().from(itineraries).where(eq(itineraries.userId, userId));
        return { itineraries };
    });
};
```

#### 5.2 iOS Itinerary UI (iOS - Day 4-5)

```swift
// Features/Itinerary/Views/ItineraryListView.swift
import SwiftUI

struct ItineraryListView: View {
    @StateObject private var viewModel = ItineraryViewModel()
    @State private var showingGenerator = false
    
    var body: some View {
        NavigationView {
            ZStack {
                Color.bgPrimary.ignoresSafeArea()
                
                if viewModel.itineraries.isEmpty {
                    VStack(spacing: 16) {
                        Image(systemName: "map")
                            .font(.system(size: 64))
                            .foregroundColor(.textTertiary)
                        
                        Text("No Trip Plans Yet")
                            .font(.siteName)
                            .foregroundColor(.textPrimary)
                        
                        Text("Create your first AI-powered itinerary")
                            .font(.bodyMedium)
                            .foregroundColor(.textSecondary)
                        
                        Button(action: { showingGenerator = true }) {
                            HStack {
                                Image(systemName: "sparkles")
                                Text("Generate Itinerary")
                            }
                            .fontWeight(.semibold)
                            .frame(maxWidth: .infinity)
                            .frame(height: 56)
                            .background(Color.primaryGold)
                            .foregroundColor(.bgPrimary)
                            .cornerRadius(12)
                        }
                        .padding(.horizontal, 32)
                    }
                } else {
                    ScrollView {
                        LazyVStack(spacing: 16) {
                            ForEach(viewModel.itineraries) { itinerary in
                                NavigationLink(destination: ItineraryDetailView(itinerary: itinerary)) {
                                    ItineraryCard(itinerary: itinerary)
                                }
                            }
                        }
                        .padding()
                    }
                }
            }
            .navigationTitle("My Trips")
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: { showingGenerator = true }) {
                        Image(systemName: "plus")
                            .foregroundColor(.primaryGold)
                    }
                }
            }
            .sheet(isPresented: $showingGenerator) {
                ItineraryGeneratorView()
            }
        }
        .onAppear {
            viewModel.loadItineraries()
        }
    }
}

struct ItineraryCard: View {
    let itinerary: Itinerary
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    Text(itinerary.title)
                        .font(.poiTitle)
                        .foregroundColor(.textPrimary)
                    
                    Text("\(itinerary.startDate.formatted(date: .abbreviated, time: .omitted)) - \(itinerary.endDate.formatted(date: .abbreviated, time: .omitted))")
                        .font(.caption)
                        .foregroundColor(.textSecondary)
                }
                
                Spacer()
                
                Image(systemName: "chevron.right")
                    .foregroundColor(.textTertiary)
            }
            
            HStack(spacing: 8) {
                ForEach(itinerary.regions, id: \.self) { region in
                    Text(region.capitalized)
                        .font(.caption)
                        .padding(.horizontal, 8)
                        .padding(.vertical, 4)
                        .background(Color.primaryGold.opacity(0.2))
                        .foregroundColor(.primaryGold)
                        .cornerRadius(4)
                }
            }
        }
        .padding()
        .background(Color.bgSecondary)
        .cornerRadius(12)
    }
}

// Features/Itinerary/Views/ItineraryGeneratorView.swift
struct ItineraryGeneratorView: View {
    @Environment(\.dismiss) var dismiss
    @StateObject private var viewModel = ItineraryGeneratorViewModel()
    
    @State private var durationDays = 3
    @State private var selectedRegions: Set<String> = []
    @State private var selectedInterests: Set<String> = []
    
    let regions = ["cairo", "luxor", "aswan", "alexandria"]
    let interests = ["ancient_history", "architecture", "archaeology", "art"]
    
    var body: some View {
        NavigationView {
            Form {
                Section("Trip Duration") {
                    Stepper("\(durationDays) Days", value: $durationDays, in: 1...14)
                }
                
                Section("Regions") {
                    ForEach(regions, id: \.self) { region in
                        Toggle(region.capitalized, isOn: Binding(
                            get: { selectedRegions.contains(region) },
                            set: { isOn in
                                if isOn {
                                    selectedRegions.insert(region)
                                } else {
                                    selectedRegions.remove(region)
                                }
                            }
                        ))
                    }
                }
                
                Section("Interests") {
                    ForEach(interests, id: \.self) { interest in
                        Toggle(interest.replacingOccurrences(of: "_", with: " ").capitalized, isOn: Binding(
                            get: { selectedInterests.contains(interest) },
                            set: { isOn in
                                if isOn {
                                    selectedInterests.insert(interest)
                                } else {
                                    selectedInterests.remove(interest)
                                }
                            }
                        ))
                    }
                }
                
                Section {
                    Button(action: {
                        viewModel.generateItinerary(
                            durationDays: durationDays,
                            regions: Array(selectedRegions),
                            interests: Array(selectedInterests)
                        )
                    }) {
                        HStack {
                            if viewModel.isGenerating {
                                ProgressView()
                            } else {
                                Image(systemName: "sparkles")
                            }
                            Text("Generate with AI")
                        }
                        .frame(maxWidth: .infinity)
                    }
                    .disabled(selectedRegions.isEmpty || viewModel.isGenerating)
                }
            }
            .navigationTitle("Plan Your Trip")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Cancel") {
                        dismiss()
                    }
                }
            }
        }
    }
}
```

### Sprint 5 Success Criteria
- ✅ AI generates optimized itineraries
- ✅ Itinerary list view displays saved trips
- ✅ Generator form collects user preferences
- ✅ Day-by-day breakdown shows POIs
- ✅ Users can edit and customize itineraries

---

## 📅 Sprint 6: Polish & Launch (Weeks 11-12)

### Sprint Goal
Complete onboarding, settings, push notifications, testing, and prepare for launch.

### Tasks

#### 6.1 Onboarding Flow (iOS & Android - Day 1-2)

```swift
// iOS - Features/Onboarding/Views/OnboardingView.swift
import SwiftUI

struct OnboardingView: View {
    @State private var currentPage = 0
    @AppStorage("hasCompletedOnboarding") private var hasCompletedOnboarding = false
    
    let pages = [
        OnboardingPage(
            image: "building.columns.fill",
            title: "Explore Ancient Egypt",
            description: "Discover 5,000 years of history with AI-powered audio guides"
        ),
        OnboardingPage(
            image: "location.fill",
            title: "Location-Based Audio",
            description: "Automatic audio triggers as you approach each monument"
        ),
        OnboardingPage(
            image: "arkit",
            title: "AR Navigation",
            description: "Find your way with augmented reality wayfinding"
        ),
        OnboardingPage(
            image: "sparkles",
            title: "AI Egyptologist",
            description: "Ask questions and get expert answers instantly"
        ),
    ]
    
    var body: some View {
        VStack {
            TabView(selection: $currentPage) {
                ForEach(0..<pages.count, id: \.self) { index in
                    OnboardingPageView(page: pages[index])
                        .tag(index)
                }
            }
            .tabViewStyle(.page)
            .indexViewStyle(.page(backgroundDisplayMode: .always))
            
            Button(action: {
                if currentPage < pages.count - 1 {
                    withAnimation {
                        currentPage += 1
                    }
                } else {
                    hasCompletedOnboarding = true
                }
            }) {
                Text(currentPage < pages.count - 1 ? "Next" : "Get Started")
                    .fontWeight(.semibold)
                    .frame(maxWidth: .infinity)
                    .frame(height: 56)
                    .background(Color.primaryGold)
                    .foregroundColor(.bgPrimary)
                    .cornerRadius(12)
            }
            .padding()
        }
        .background(Color.bgPrimary)
    }
}

struct OnboardingPage {
    let image: String
    let title: String
    let description: String
}

struct OnboardingPageView: View {
    let page: OnboardingPage
    
    var body: some View {
        VStack(spacing: 32) {
            Spacer()
            
            Image(systemName: page.image)
                .font(.system(size: 100))
                .foregroundColor(.primaryGold)
            
            VStack(spacing: 16) {
                Text(page.title)
                    .font(.displaySmall)
                    .foregroundColor(.textPrimary)
                    .multilineTextAlignment(.center)
                
                Text(page.description)
                    .font(.bodyLarge)
                    .foregroundColor(.textSecondary)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, 32)
            }
            
            Spacer()
        }
    }
}
```

#### 6.2 Push Notifications (Backend & Mobile - Day 3)

```typescript
// Backend - services/notifications/src/push.ts
import admin from 'firebase-admin';

export class PushNotificationService {
    private messaging: admin.messaging.Messaging;
    
    constructor() {
        admin.initializeApp({
            credential: admin.credential.cert(serviceAccount),
        });
        this.messaging = admin.messaging();
    }
    
    async sendPOINotification(userId: string, poi: POI) {
        const userTokens = await this.getUserTokens(userId);
        
        const message = {
            notification: {
                title: `Nearby: ${poi.name}`,
                body: `You're close to ${poi.name}. Tap to learn more!`,
            },
            data: {
                type: 'poi_proximity',
                poiId: poi.id,
            },
            tokens: userTokens,
        };
        
        await this.messaging.sendMulticast(message);
    }
    
    async sendItineraryReminder(userId: string, itinerary: Itinerary) {
        const userTokens = await this.getUserTokens(userId);
        
        const message = {
            notification: {
                title: 'Trip Starting Soon!',
                body: `Your ${itinerary.title} starts tomorrow. Ready to explore?`,
            },
            data: {
                type: 'itinerary_reminder',
                itineraryId: itinerary.id,
            },
            tokens: userTokens,
        };
        
        await this.messaging.sendMulticast(message);
    }
}
```

#### 6.3 Settings Screen (iOS - Day 4)

```swift
// Features/Settings/Views/SettingsView.swift
import SwiftUI

struct SettingsView: View {
    @AppStorage("preferredLanguage") private var preferredLanguage = "en"
    @AppStorage("expertiseLevel") private var expertiseLevel = "casual"
    @AppStorage("autoPlayAudio") private var autoPlayAudio = true
    @AppStorage("downloadOnWifiOnly") private var downloadOnWifiOnly = true
    
    var body: some View {
        NavigationView {
            Form {
                Section("Content Preferences") {
                    Picker("Language", selection: $preferredLanguage) {
                        Text("English").tag("en")
                        Text("العربية").tag("ar")
                        Text("Français").tag("fr")
                    }
                    
                    Picker("Expertise Level", selection: $expertiseLevel) {
                        Text("Casual").tag("casual")
                        Text("Enthusiast").tag("enthusiast")
                        Text("Scholar").tag("scholar")
                    }
                }
                
                Section("Audio") {
                    Toggle("Auto-play Audio", isOn: $autoPlayAudio)
                }
                
                Section("Downloads") {
                    Toggle("WiFi Only", isOn: $downloadOnWifiOnly)
                    
                    NavigationLink("Manage Downloads") {
                        DownloadManagerView()
                    }
                }
                
                Section("Account") {
                    NavigationLink("Profile") {
                        ProfileView()
                    }
                    
                    NavigationLink("Subscription") {
                        SubscriptionView()
                    }
                }
                
                Section {
                    Link("Privacy Policy", destination: URL(string: "https://egytour.app/privacy")!)
                    Link("Terms of Service", destination: URL(string: "https://egytour.app/terms")!)
                    
                    Button("Sign Out", role: .destructive) {
                        // Sign out
                    }
                }
            }
            .navigationTitle("Settings")
        }
    }
}
```

#### 6.4 Testing Strategy (Day 5-7)

```bash
# iOS Testing
# Run unit tests
xcodebuild test -scheme Egytour -destination 'platform=iOS Simulator,name=iPhone 15 Pro'

# Run UI tests
xcodebuild test -scheme EgytourUITests -destination 'platform=iOS Simulator,name=iPhone 15 Pro'

# Android Testing
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Backend Testing
npm test

# Integration Tests
npm run test:integration

# Load Testing
artillery run load-test.yml
```

#### 6.5 CI/CD Pipeline (Day 8-9)

```yaml
# .github/workflows/ios.yml
name: iOS CI/CD

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  test:
    runs-on: macos-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Setup Xcode
        uses: maxim-lobanov/setup-xcode@v1
        with:
          xcode-version: latest-stable
      
      - name: Run Tests
        run: |
          xcodebuild test \
            -scheme Egytour \
            -destination 'platform=iOS Simulator,name=iPhone 15 Pro'
  
  deploy:
    needs: test
    runs-on: macos-latest
    if: github.ref == 'refs/heads/main'
    steps:
      - uses: actions/checkout@v3
      
      - name: Build and Upload to TestFlight
        run: |
          fastlane ios beta

# .github/workflows/android.yml
name: Android CI/CD

on:
  push:
    branches: [main, develop]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Setup JDK
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      
      - name: Run Tests
        run: ./gradlew test
  
  deploy:
    needs: test
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    steps:
      - uses: actions/checkout@v3
      
      - name: Build and Upload to Play Store
        run: |
          fastlane android internal
```

#### 6.6 Launch Checklist (Day 10-12)

```markdown
# Pre-Launch Checklist

## App Store Preparation
- [ ] App Store screenshots (6.7", 6.5", 5.5")
- [ ] App preview video
- [ ] App description and keywords
- [ ] Privacy policy URL
- [ ] Support URL
- [ ] Age rating questionnaire
- [ ] App review notes

## Google Play Preparation
- [ ] Play Store screenshots
- [ ] Feature graphic (1024x500)
- [ ] App description
- [ ] Privacy policy
- [ ] Content rating questionnaire
- [ ] Target audience

## Technical
- [ ] All unit tests passing
- [ ] All UI tests passing
- [ ] Performance benchmarks met
- [ ] Crash rate < 1%
- [ ] API rate limits configured
- [ ] CDN configured
- [ ] Database backups enabled
- [ ] Monitoring alerts set up

## Content
- [ ] Giza Pyramids fully seeded
- [ ] All audio files uploaded
- [ ] All images optimized
- [ ] Translations complete (EN, AR, FR)

## Legal
- [ ] Privacy policy published
- [ ] Terms of service published
- [ ] GDPR compliance verified
- [ ] Payment processing configured

## Marketing
- [ ] Landing page live
- [ ] Social media accounts created
- [ ] Press kit prepared
- [ ] Launch announcement ready
```

### Sprint 6 Success Criteria
- ✅ Onboarding flow complete
- ✅ Push notifications working
- ✅ Settings fully functional
- ✅ All tests passing
- ✅ CI/CD pipelines deployed
- ✅ App Store submissions ready
- ✅ Beta testing complete

---

## 📊 Overall Success Metrics

### Technical KPIs
- ✅ API response time < 2s (p95)
- ✅ App crash rate < 1%
- ✅ Audio trigger accuracy > 95%
- ✅ AR frame rate > 30fps
- ✅ Map rendering < 2s

### Product KPIs
- ✅ 1,000 downloads in first month
- ✅ 100 paying users
- ✅ 4.5+ App Store rating
- ✅ 60% audio completion rate
- ✅ 30% DAU/MAU ratio

### Business KPIs
- ✅ $1,000 MRR by Month 1
- ✅ CAC < $3
- ✅ LTV > $20
- ✅ Churn rate < 10%

---

## 🎉 Launch Day!

**Congratulations!** After 12 weeks of intensive development, EgyTour is ready to launch. You've built:

- ✅ Native iOS app with ARKit
- ✅ Native Android app with ARCore
- ✅ Location-based audio triggers
- ✅ AI-powered Q&A system
- ✅ Trip planning with AI
- ✅ Offline content downloads
- ✅ Beautiful, Egyptian-themed UI

**Next Steps:**
1. Submit to App Store & Google Play
2. Monitor crash reports and user feedback
3. Begin Sprint 7: Post-launch improvements
4. Expand to more sites beyond Giza

**You did it!** 🎊
