import SwiftUI

@main
struct EgytourApp: App {
    @AppStorage("hasCompletedOnboarding") private var hasCompletedOnboarding = false
    @State private var isLoggedIn = false
    
    var body: some Scene {
        WindowGroup {
            if !hasCompletedOnboarding {
                OnboardingView(onComplete: {
                    withAnimation {
                        hasCompletedOnboarding = true
                    }
                })
            } else if isLoggedIn {
                TabView {
                    AudioPlayerView()
                        .tabItem {
                            Label("Audio", systemImage: "headphones")
                        }
                    
                    ExploreMapView()
                        .tabItem {
                            Label("Map", systemImage: "map")
                        }
                    
                    AIChatView()
                        .tabItem {
                            Label("Egyptologist AI", systemImage: "bubble.left.and.bubble.right.fill")
                        }
                    
                    ItineraryListView()
                        .tabItem {
                            Label("Trips", systemImage: "calendar.badge.clock")
                        }
                    
                    SettingsView(onSignOut: {
                        withAnimation {
                            isLoggedIn = false
                        }
                    })
                        .tabItem {
                            Label("Settings", systemImage: "gearshape.fill")
                        }
                }
                .accentColor(.primaryGold)

            } else {
                LoginView(onLoginSuccess: {
                    withAnimation {
                        isLoggedIn = true
                    }
                })
            }
        }
    }
}
