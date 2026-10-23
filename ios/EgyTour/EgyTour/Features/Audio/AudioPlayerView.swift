import SwiftUI

struct AudioPlayerView: View {
    @ObservedObject var audioService = AudioService.shared
    @State private var showTranscript: Bool = false
    
    var onDismiss: (() -> Void)? = nil
    
    var body: some View {
        ScrollView {
            VStack(spacing: 20) {
                // Top Dismiss Indicator / Header
                HStack {
                    if let onDismiss = onDismiss {
                        Button(action: onDismiss) {
                            Image(systemName: "chevron.down")
                                .font(.title3)
                                .foregroundColor(.secondary)
                        }
                    }
                    Spacer()
                    
                    // Offline / Streaming status pill
                    HStack(spacing: 4) {
                        Image(systemName: audioService.isOfflineAvailable ? "checkmark.circle.fill" : "antenna.radiowaves.left.and.right")
                            .font(.caption)
                            .foregroundColor(audioService.isOfflineAvailable ? .primaryGold : .secondary)
                        Text(audioService.isOfflineAvailable ? "Offline Saved" : "Streaming")
                            .font(.caption2)
                            .fontWeight(.semibold)
                            .foregroundColor(audioService.isOfflineAvailable ? .primaryGold : .secondary)
                    }
                    .padding(.horizontal, 10)
                    .padding(.vertical, 4)
                    .background(Color.primaryGold.opacity(audioService.isOfflineAvailable ? 0.15 : 0.05))
                    .cornerRadius(12)
                }
                .padding(.top, 12)
                .padding(.horizontal, 20)
                
                // Cover Art / Hero Card
                ZStack {
                    RoundedRectangle(cornerRadius: 24)
                        .fill(
                            LinearGradient(
                                colors: [Color.primaryGold.opacity(0.3), Color(.secondarySystemBackground)],
                                startPoint: .topLeading,
                                endPoint: .bottomTrailing
                            )
                        )
                        .overlay(
                            RoundedRectangle(cornerRadius: 24)
                                .stroke(Color.primaryGold.opacity(0.4), lineWidth: 1)
                        )
                    
                    VStack(spacing: 12) {
                        ZStack {
                            Circle()
                                .fill(Color.primaryGold.opacity(0.2))
                                .frame(width: 76, height: 76)
                            
                            Image(systemName: "headphones")
                                .font(.system(size: 34))
                                .foregroundColor(.primaryGold)
                        }
                        
                        Text(audioService.currentPOIName)
                            .font(.title3)
                            .fontWeight(.bold)
                            .foregroundColor(.primary)
                            .multilineTextAlignment(.center)
                            .padding(.horizontal, 16)
                        
                        Text("Giza Necropolis • 4th Dynasty")
                            .font(.caption)
                            .fontWeight(.semibold)
                            .foregroundColor(.primaryGold)
                    }
                    .padding(.vertical, 24)
                }
                .frame(height: 210)
                .padding(.horizontal, 20)
                
                // Language Selector Chips (EN, AR, FR)
                HStack(spacing: 8) {
                    ForEach([("en", "English 🇬🇧"), ("ar", "العربية 🇪🇬"), ("fr", "Français 🇫🇷")], id: \.0) { code, title in
                        let isSelected = audioService.currentLanguage == code
                        Button(action: {
                            audioService.setLanguage(code)
                        }) {
                            Text(title)
                                .font(.caption)
                                .fontWeight(isSelected ? .bold : .medium)
                                .foregroundColor(isSelected ? .black : .primary)
                                .padding(.horizontal, 12)
                                .padding(.vertical, 8)
                                .background(isSelected ? Color.primaryGold : Color(.secondarySystemBackground))
                                .cornerRadius(12)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 12)
                                        .stroke(isSelected ? Color.clear : Color.secondary.opacity(0.2), lineWidth: 1)
                                )
                        }
                    }
                }
                .padding(.horizontal, 20)
                
                // Expertise Tier Segmented Control
                HStack(spacing: 6) {
                    ForEach([("casual", "Casual", "2 min"), ("enthusiast", "Enthusiast", "4 min"), ("scholar", "Scholar", "6 min")], id: \.0) { key, label, dur in
                        let isSelected = audioService.currentExpertise == key
                        Button(action: {
                            audioService.setExpertise(key)
                        }) {
                            VStack(spacing: 2) {
                                Text(label)
                                    .font(.subheadline)
                                    .fontWeight(isSelected ? .bold : .semibold)
                                    .foregroundColor(isSelected ? .black : .primary)
                                Text(dur)
                                    .font(.caption2)
                                    .foregroundColor(isSelected ? .black.opacity(0.7) : .secondary)
                            }
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 8)
                            .background(isSelected ? Color.primaryGold : Color(.secondarySystemBackground))
                            .cornerRadius(12)
                        }
                    }
                }
                .padding(4)
                .background(Color(.secondarySystemBackground))
                .cornerRadius(14)
                .padding(.horizontal, 20)
                
                // Progress Slider & Timestamps
                VStack(spacing: 6) {
                    Slider(
                        value: Binding(
                            get: { audioService.progress },
                            set: { audioService.seek(to: $0) }
                        )
                    )
                    .accentColor(.primaryGold)
                    
                    HStack {
                        Text(formatSeconds(audioService.progress * audioService.duration))
                        Spacer()
                        Text(formatSeconds(audioService.duration))
                    }
                    .font(.caption)
                    .foregroundColor(.secondary)
                }
                .padding(.horizontal, 20)
                
                // Playback Control Buttons
                HStack(spacing: 24) {
                    // Speed Selector Button
                    Button(action: {
                        audioService.cyclePlaybackSpeed()
                    }) {
                        Text("\(String(format: "%.2gx", audioService.playbackSpeed))")
                            .font(.caption)
                            .fontWeight(.bold)
                            .foregroundColor(.primaryGold)
                            .frame(width: 44, height: 44)
                            .background(Color(.secondarySystemBackground))
                            .clipShape(Circle())
                            .overlay(Circle().stroke(Color.primaryGold.opacity(0.3), lineWidth: 1))
                    }
                    
                    // Rewind 15s
                    Button(action: {
                        audioService.seekRelative(seconds: -15)
                    }) {
                        Image(systemName: "gobackward.15")
                            .font(.title2)
                            .foregroundColor(.primary)
                            .frame(width: 46, height: 46)
                            .background(Color(.secondarySystemBackground))
                            .clipShape(Circle())
                    }
                    
                    // Play / Pause FAB
                    Button(action: {
                        audioService.togglePlayPause()
                    }) {
                        ZStack {
                            Circle()
                                .fill(Color.primaryGold)
                                .frame(width: 66, height: 66)
                                .shadow(color: Color.primaryGold.opacity(0.4), radius: 8, y: 4)
                            
                            Image(systemName: audioService.isPlaying ? "pause.fill" : "play.fill")
                                .font(.title)
                                .foregroundColor(.black)
                        }
                    }
                    
                    // Forward 15s
                    Button(action: {
                        audioService.seekRelative(seconds: 15)
                    }) {
                        Image(systemName: "goforward.15")
                            .font(.title2)
                            .foregroundColor(.primary)
                            .frame(width: 46, height: 46)
                            .background(Color(.secondarySystemBackground))
                            .clipShape(Circle())
                    }
                    
                    // Transcript Button
                    Button(action: {
                        withAnimation { showTranscript.toggle() }
                    }) {
                        Image(systemName: "doc.text.fill")
                            .font(.title3)
                            .foregroundColor(showTranscript ? .black : .primary)
                            .frame(width: 44, height: 44)
                            .background(showTranscript ? Color.primaryGold : Color(.secondarySystemBackground))
                            .clipShape(Circle())
                    }
                }
                .padding(.horizontal, 20)
                
                // Expandable Transcript Section
                if showTranscript {
                    VStack(alignment: .leading, spacing: 10) {
                        HStack {
                            Image(systemName: "quote.bubble.fill")
                                .foregroundColor(.primaryGold)
                            Text("Egyptologist Audio Transcript")
                                .font(.headline)
                                .foregroundColor(.primary)
                            Spacer()
                        }
                        
                        Text(audioService.currentTranscript)
                            .font(.subheadline)
                            .lineSpacing(4)
                            .foregroundColor(.secondary)
                    }
                    .padding(16)
                    .background(Color(.secondarySystemBackground))
                    .cornerRadius(16)
                    .overlay(
                        RoundedRectangle(cornerRadius: 16)
                            .stroke(Color.primaryGold.opacity(0.3), lineWidth: 1)
                    )
                    .padding(.horizontal, 20)
                    .transition(.opacity.combined(with: .move(edge: .top)))
                }
                
                Spacer(modifier = Modifier.height(24))
            }
        }
        .background(Color(.systemBackground).edgesIgnoringSafeArea(.all))
    }
    
    private func formatSeconds(_ seconds: Double) -> String {
        guard seconds.isFinite && !seconds.isNaN else { return "00:00" }
        let total = Int(seconds)
        let m = total / 60
        let s = total % 60
        return String(format: "%02d:%02d", m, s)
    }
}
