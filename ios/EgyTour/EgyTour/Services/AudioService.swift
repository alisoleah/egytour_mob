import AVFoundation
import MediaPlayer
import Combine

class AudioService: ObservableObject {
    static let shared = AudioService()
    
    @Published var isPlaying = false
    @Published var currentPOIId: String?
    @Published var progress: Double = 0
    @Published var duration: Double = 0
    
    private var player: AVPlayer?
    private var timeObserver: Any?
    
    init() {
        setupAudioSession()
        setupRemoteCommands()
    }
    
    private func setupAudioSession() {
        do {
            try AVAudioSession.sharedInstance().setCategory(.playback, mode: .default)
            try AVAudioSession.sharedInstance().setActive(true)
        } catch {
            print("Failed to setup audio session: \(error)")
        }
    }
    
    private func setupRemoteCommands() {
        let commandCenter = MPRemoteCommandCenter.shared()
        
        commandCenter.playCommand.addTarget { [weak self] _ in
            self?.play()
            return .success
        }
        
        commandCenter.pauseCommand.addTarget { [weak self] _ in
            self?.pause()
            return .success
        }
    }
    
    func loadAudio(url: URL, poiId: String) {
        currentPOIId = poiId
        let playerItem = AVPlayerItem(url: url)
        player = AVPlayer(playerItem: playerItem)
        
        // Observe playback progress
        if let observer = timeObserver {
            player?.removeTimeObserver(observer)
        }
        
        timeObserver = player?.addPeriodicTimeObserver(
            forInterval: CMTime(seconds: 0.5, preferredTimescale: 600),
            queue: .main
        ) { [weak self] time in
            guard let self = self,
                  let duration = self.player?.currentItem?.duration.seconds,
                  duration.isFinite else { return }
            
            self.progress = time.seconds / duration
            self.duration = duration
        }
        
        play()
    }
    
    func play() {
        player?.play()
        isPlaying = true
        updateNowPlaying()
    }
    
    func pause() {
        player?.pause()
        isPlaying = false
    }
    
    func seek(to value: Double) {
        guard let duration = player?.currentItem?.duration.seconds else { return }
        let time = CMTime(seconds: duration * value, preferredTimescale: 600)
        player?.seek(to: time)
    }
    
    private func updateNowPlaying() {
        var nowPlayingInfo = [String: Any]()
        nowPlayingInfo[MPNowPlayingInfoPropertyElapsedPlaybackTime] = player?.currentTime().seconds
        nowPlayingInfo[MPMediaItemPropertyPlaybackDuration] = duration
        
        MPNowPlayingInfoCenter.default().nowPlayingInfo = nowPlayingInfo
    }
}
