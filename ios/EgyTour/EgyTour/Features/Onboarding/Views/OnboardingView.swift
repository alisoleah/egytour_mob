import SwiftUI

struct OnboardingItem {
    let systemImage: String
    let badgeText: String
    let title: String
    let description: String
}

struct OnboardingView: View {
    var onComplete: () -> Void
    @State private var currentPage = 0
    
    let pages: [OnboardingItem] = [
        OnboardingItem(
            systemImage: "headphones.circle.fill",
            badgeText: "GEO-TRIGGERED NARRATION",
            title: "Hear History Come Alive",
            description: "GPS-synchronized audio narratives automatically play as you approach ancient monuments across Giza, Luxor, Aswan, and beyond."
        ),
        OnboardingItem(
            systemImage: "camera.viewfinder",
            badgeText: "AR WAYFINDING",
            title: "Augmented Reality Navigation",
            description: "Follow real-time 3D directional pointers and compass heading directly over your camera feed to effortlessly find hidden tombs and temple chambers."
        ),
        OnboardingItem(
            systemImage: "sparkles",
            badgeText: "GEMINI RAG AI",
            title: "Your Personal Egyptologist",
            description: "Chat with Dr. Zahi, powered by vetted historical archives. Generate custom multi-day itineraries tailored to your schedule and passions."
        )
    ]
    
    var body: some View {
        ZStack {
            Color(.systemBackground).edgesIgnoringSafeArea(.all)
            
            VStack(spacing: 0) {
                // Top Skip Button
                HStack {
                    Spacer()
                    Button("Skip") {
                        onComplete()
                    }
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                    .padding(.horizontal, 20)
                    .padding(.top, 16)
                }
                
                // Pager
                TabView(selection: $currentPage) {
                    ForEach(0..<pages.count, id: \.self) { index in
                        OnboardingCardView(item: pages[index])
                            .tag(index)
                    }
                }
                .tabViewStyle(PageTabViewStyle(indexDisplayMode: .never))
                
                // Bottom Indicator and Button
                VStack(spacing: 24) {
                    // Page Indicator Dots
                    HStack(spacing: 8) {
                        ForEach(0..<pages.count, id: \.self) { index in
                            Capsule()
                                .fill(currentPage == index ? Color.primaryGold : Color.secondary.opacity(0.3))
                                .frame(width: currentPage == index ? 24 : 8, height: 8)
                                .animation(.spring(), value: currentPage)
                        }
                    }
                    
                    // Action Button
                    Button(action: {
                        if currentPage < pages.count - 1 {
                            withAnimation {
                                currentPage += 1
                            }
                        } else {
                            onComplete()
                        }
                    }) {
                        HStack {
                            Text(currentPage < pages.count - 1 ? "Next" : "Begin Your Expedition")
                                .fontWeight(.bold)
                            Image(systemName: currentPage < pages.count - 1 ? "arrow.right" : "sparkles")
                        }
                        .font(.headline)
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 16)
                        .background(Color.primaryGold)
                        .cornerRadius(14)
                        .shadow(color: Color.primaryGold.opacity(0.35), radius: 8, y: 4)
                    }
                    .padding(.horizontal, 24)
                }
                .padding(.bottom, 32)
            }
        }
    }
}

struct OnboardingCardView: View {
    let item: OnboardingItem
    
    var body: some View {
        VStack(spacing: 24) {
            Spacer()
            
            // Icon Hero
            ZStack {
                Circle()
                    .fill(Color.primaryGold.opacity(0.12))
                    .frame(width: 140, height: 140)
                
                Image(systemName: item.systemImage)
                    .font(.system(size: 68))
                    .foregroundColor(.primaryGold)
            }
            
            // Badge
            Text(item.badgeText)
                .font(.caption)
                .fontWeight(.bold)
                .foregroundColor(.primaryGold)
                .padding(.horizontal, 12)
                .padding(.vertical, 5)
                .background(Color.primaryGold.opacity(0.15))
                .cornerRadius(8)
            
            // Title & Description
            VStack(spacing: 12) {
                Text(item.title)
                    .font(.title)
                    .fontWeight(.bold)
                    .multilineTextAlignment(.center)
                    .foregroundColor(.primary)
                
                Text(item.description)
                    .font(.body)
                    .foregroundColor(.secondary)
                    .multilineTextAlignment(.center)
                    .lineSpacing(4)
                    .padding(.horizontal, 32)
            }
            
            Spacer()
        }
        .padding()
    }
}
