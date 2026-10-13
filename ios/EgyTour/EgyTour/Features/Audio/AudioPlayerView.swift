import SwiftUI

struct AudioPlayerView: View {
    @ObservedObject var audioService = AudioService.shared
    
    // Placeholder for POI data, normally passed in or fetched based on currentPOIId
    var poiName: String = "Great Pyramid of Khufu"
    var poiImageName: String = "pyramid_placeholder" // Asset name or URL
    
    var body: some View {
        VStack(spacing: 24) {
            // Dismiss indicator
            Capsule()
                .fill(Color.secondary.opacity(0.3))
                .frame(width: 40, height: 4)
                .padding(.top, 8)
            
            Spacer()
            
            // Cover Art
            RoundedRectangle(cornerRadius: 16)
                .fill(Color.gray.opacity(0.3))
                .aspectRatio(1.0, contentMode: .fit)
                .overlay(
                    Image(systemName: "photo") // Placeholder
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .padding(40)
                        .foregroundColor(.gray)
                )
                .shadow(radius: 10)
                .padding(.horizontal, 32)
            
            VStack(spacing: 8) {
                Text(poiName)
                    .font(.title2)
                    .fontWeight(.bold)
                    .multilineTextAlignment(.center)
                    .foregroundColor(.primary)
                
                Text("Audio Guide")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
            }
            
            Spacer()
            
            // Progress Bar
            VStack(spacing: 8) {
                Slider(value: Binding(
                    get: { audioService.progress },
                    set: { audioService.seek(to: $0) }
                ))
                .accentColor(.orange) // Pharaoh's Gold-ish
                
                HStack {
                    Text(formatTime(audioService.progress * audioService.duration))
                    Spacer()
                    Text(formatTime(audioService.duration))
                }
                .font(.caption)
                .foregroundColor(.secondary)
            }
            .padding(.horizontal)
            
            // Controls
            HStack(spacing: 40) {
                Button(action: { audioService.seek(to: max(0, audioService.progress - 0.1)) }) {
                    Image(systemName: "gobackward.15")
                        .font(.title)
                        .foregroundColor(.primary)
                }
                
                Button(action: {
                    if audioService.isPlaying {
                        audioService.pause()
                    } else {
                        audioService.play()
                    }
                }) {
                    ZStack {
                        Circle()
                            .fill(Color.orange)
                            .frame(width: 72, height: 72)
                        
                        Image(systemName: audioService.isPlaying ? "pause.fill" : "play.fill")
                            .font(.title)
                            .foregroundColor(.white)
                    }
                }
                
                Button(action: { audioService.seek(to: min(1, audioService.progress + 0.1)) }) {
                    Image(systemName: "goforward.15")
                        .font(.title)
                        .foregroundColor(.primary)
                }
            }
            .padding(.bottom, 32)
        }
        .background(Color(.systemBackground))
        .padding()
    }
    
    private func formatTime(_ seconds: Double) -> String {
        guard !seconds.isNaN && seconds.isFinite else { return "0:00" }
        let minutes = Int(seconds) / 60
        let remainingSeconds = Int(seconds) % 60
        return String(format: "%d:%02d", minutes, remainingSeconds)
    }
}

struct AudioPlayerView_Previews: PreviewProvider {
    static var previews: some View {
        AudioPlayerView()
    }
}
