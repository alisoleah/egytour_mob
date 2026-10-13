import SwiftUI

struct SettingsView: View {
    @AppStorage("preferredLanguage") private var preferredLanguage = "en"
    @AppStorage("expertiseLevel") private var expertiseLevel = "enthusiast"
    @AppStorage("autoPlayAudio") private var autoPlayAudio = true
    @AppStorage("downloadOnWifiOnly") private var downloadOnWifiOnly = true
    @AppStorage("hapticFeedback") private var hapticFeedback = true
    @AppStorage("proximityAlerts") private var proximityAlerts = true
    
    @State private var showingClearCacheAlert = false
    @State private var cacheCleared = false
    var onSignOut: (() -> Void)? = nil
    
    var body: some View {
        NavigationView {
            Form {
                // User Profile Header
                Section {
                    HStack(spacing: 16) {
                        ZStack {
                            Circle()
                                .fill(Color.primaryGold.opacity(0.18))
                                .frame(width: 58, height: 58)
                            
                            Image(systemName: "person.crop.circle.fill")
                                .font(.system(size: 48))
                                .foregroundColor(.primaryGold)
                        }
                        
                        VStack(alignment: .leading, spacing: 4) {
                            Text("Archaeology Explorer")
                                .font(.headline)
                                .fontWeight(.bold)
                            
                            Text("explorer@egytour.app")
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                            
                            Text("VIP PASS • ALL SITES UNLOCKED")
                                .font(.caption2)
                                .fontWeight(.heavy)
                                .foregroundColor(.primaryGold)
                                .padding(.horizontal, 6)
                                .padding(.vertical, 2)
                                .background(Color.primaryGold.opacity(0.12))
                                .cornerRadius(4)
                        }
                    }
                    .padding(.vertical, 6)
                }
                
                // Content Preferences
                Section(header: Text("Content & Narration")) {
                    Picker("Narration Language", selection: $preferredLanguage) {
                        Text("English (EN)").tag("en")
                        Text("العربية (AR)").tag("ar")
                        Text("Français (FR)").tag("fr")
                        Text("Deutsch (DE)").tag("de")
                    }
                    
                    Picker("Scholar Depth", selection: $expertiseLevel) {
                        Text("Casual (Overview)").tag("casual")
                        Text("Enthusiast (Standard)").tag("enthusiast")
                        Text("Scholar (Academic & Inscriptions)").tag("scholar")
                    }
                }
                
                // Audio & AR
                Section(header: Text("Audio & AR Wayfinding")) {
                    Toggle("Auto-Play Geo-Audio", isOn: $autoPlayAudio)
                    Toggle("Download On Wi-Fi Only", isOn: $downloadOnWifiOnly)
                    Toggle("Haptic Compass Guidance", isOn: $hapticFeedback)
                    Toggle("Proximity Monument Alerts", isOn: $proximityAlerts)
                }
                
                // Offline Storage
                Section(header: Text("Offline Storage")) {
                    NavigationLink(destination: OfflineManagerView()) {
                        HStack {
                            Text("Manage Offline Packs")
                            Spacer()
                            Text("5 Packs Available")
                                .font(.caption)
                                .foregroundColor(.primaryGold)
                        }
                    }
                    
                    HStack {
                        Text("Current Cache Usage")
                        Spacer()
                        Text(cacheCleared ? "0 MB" : "142.8 MB")
                            .foregroundColor(.secondary)
                    }
                    
                    Button(action: {
                        showingClearCacheAlert = true
                    }) {
                        Text("Clear Offline Cache")
                            .foregroundColor(.red)
                    }
                    .alert(isPresented: $showingClearCacheAlert) {
                        Alert(
                            title: Text("Clear Offline Storage?"),
                            message: Text("This will remove downloaded audio tours and map tiles. They will be re-downloaded when viewed."),
                            primaryButton: .destructive(Text("Clear")) {
                                cacheCleared = true
                            },
                            secondaryButton: .cancel()
                        )
                    }
                }
                
                // App Info & Policies
                Section(header: Text("About EgyTour")) {
                    HStack {
                        Text("Version")
                        Spacer()
                        Text("1.0.0 (Pharaoh Release)")
                            .foregroundColor(.secondary)
                    }
                    
                    HStack {
                        Text("Egyptology Source")
                        Spacer()
                        Text("SCA & Ministry of Antiquities")
                            .foregroundColor(.primaryGold)
                            .font(.caption)
                    }
                    
                    Link("Privacy Policy", destination: URL(string: "https://egytour.app/privacy")!)
                    Link("Terms of Heritage Usage", destination: URL(string: "https://egytour.app/terms")!)
                }
                
                // Sign Out
                Section {
                    Button(action: {
                        onSignOut?()
                    }) {
                        HStack {
                            Spacer()
                            Text("Sign Out")
                                .fontWeight(.bold)
                                .foregroundColor(.red)
                            Spacer()
                        }
                    }
                }
            }
            .navigationTitle("Settings")
        }
    }
}
