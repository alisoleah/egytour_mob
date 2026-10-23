import AVFoundation
import MediaPlayer
import Combine

class AudioService: ObservableObject {
    static let shared = AudioService()
    
    @Published var isPlaying = false
    @Published var currentPOIId: String = "giza-1"
    @Published var currentPOIName: String = "Great Pyramid of Khufu"
    @Published var currentLanguage: String = "en" // "en", "ar", "fr"
    @Published var currentExpertise: String = "casual" // "casual", "enthusiast", "scholar"
    @Published var playbackSpeed: Float = 1.0 // 0.75, 1.0, 1.25, 1.5
    @Published var progress: Double = 0
    @Published var duration: Double = 120
    @Published var isOfflineAvailable: Bool = false
    @Published var currentTranscript: String = ""
    
    private var player: AVPlayer?
    private var timeObserver: Any?
    
    init() {
        setupAudioSession()
        setupRemoteCommands()
        loadTrack(poiId: "giza-1", poiName: "Great Pyramid of Khufu", language: "en", expertise: "casual")
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
    
    func loadTrack(poiId: String, poiName: String, language: String? = nil, expertise: String? = nil) {
        self.currentPOIId = poiId
        self.currentPOIName = poiName
        if let lang = language { self.currentLanguage = lang }
        if let exp = expertise { self.currentExpertise = exp }
        
        let lang = self.currentLanguage
        let exp = self.currentExpertise
        
        // Estimated duration by tier
        let estimatedDur: Double = (exp == "scholar") ? 360 : (exp == "enthusiast") ? 240 : 120
        self.duration = estimatedDur
        self.progress = 0
        
        updateTranscript(poiName: poiName, language: lang, expertise: exp)
        
        // Check if offline audio exists locally
        let documents = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
        let localFile = documents.appendingPathComponent("\(poiId)_\(lang)_\(exp).mp3")
        let isLocal = FileManager.default.fileExists(atPath: localFile.path)
        self.isOfflineAvailable = isLocal
        
        let playURL: URL = isLocal ? localFile : (URL(string: "https://cdn.egytour.app/audio/\(poiId)/\(lang)/\(exp).mp3") ?? localFile)
        
        let playerItem = AVPlayerItem(url: playURL)
        player = AVPlayer(playerItem: playerItem)
        
        if let observer = timeObserver {
            player?.removeTimeObserver(observer)
        }
        
        timeObserver = player?.addPeriodicTimeObserver(
            forInterval: CMTime(seconds: 0.5, preferredTimescale: 600),
            queue: .main
        ) { [weak self] time in
            guard let self = self else { return }
            let dur = self.player?.currentItem?.duration.seconds ?? self.duration
            if dur.isFinite && dur > 0 {
                self.progress = (time.seconds / dur).clamped(to: 0...1)
                self.duration = dur
            }
        }
        
        if isPlaying {
            play()
        }
    }
    
    func setLanguage(_ lang: String) {
        if currentLanguage != lang {
            loadTrack(poiId: currentPOIId, poiName: currentPOIName, language: lang, expertise: currentExpertise)
        }
    }
    
    func setExpertise(_ level: String) {
        if currentExpertise != level {
            loadTrack(poiId: currentPOIId, poiName: currentPOIName, language: currentLanguage, expertise: level)
        }
    }
    
    func setPlaybackSpeed(_ speed: Float) {
        self.playbackSpeed = speed
        if isPlaying {
            player?.rate = speed
        }
    }
    
    func cyclePlaybackSpeed() {
        let nextSpeed: Float
        switch playbackSpeed {
        case 0.75: nextSpeed = 1.0
        case 1.0: nextSpeed = 1.25
        case 1.25: nextSpeed = 1.5
        default: nextSpeed = 0.75
        }
        setPlaybackSpeed(nextSpeed)
    }
    
    func play() {
        player?.play()
        player?.rate = playbackSpeed
        isPlaying = true
        updateNowPlaying()
    }
    
    func pause() {
        player?.pause()
        isPlaying = false
    }
    
    func togglePlayPause() {
        if isPlaying { pause() } else { play() }
    }
    
    func seek(to value: Double) {
        let dur = duration > 0 ? duration : 120
        let time = CMTime(seconds: dur * value, preferredTimescale: 600)
        player?.seek(to: time)
        self.progress = value
    }
    
    func seekRelative(seconds: Double) {
        let currentSec = (player?.currentTime().seconds ?? 0)
        let targetSec = (currentSec + seconds).clamped(to: 0...duration)
        let time = CMTime(seconds: targetSec, preferredTimescale: 600)
        player?.seek(to: time)
        if duration > 0 {
            self.progress = (targetSec / duration).clamped(to: 0...1)
        }
    }
    
    private func updateNowPlaying() {
        var nowPlayingInfo = [String: Any]()
        nowPlayingInfo[MPNowPlayingInfoPropertyElapsedPlaybackTime] = player?.currentTime().seconds
        nowPlayingInfo[MPMediaItemPropertyPlaybackDuration] = duration
        MPNowPlayingInfoCenter.default().nowPlayingInfo = nowPlayingInfo
    }
    
    private func updateTranscript(poiName: String, language: String, expertise: String) {
        let narrator: String
        let text: String
        
        switch language {
        case "ar":
            narrator = "د. أحمد محمد (كبير الباحثين بوزارة الآثار)"
            switch expertise {
            case "scholar":
                text = "مرحباً بكم عند \(poiName). تشير النقوش الجنائزية للأسرة الرابعة ووثائق وادي الجرف (بردية ميرير) إلى أن أعمال البناء استغرقت قرابة 27 عاماً بمشاركة نخبة الحرفيين المصريين المنظمين في فرق عمل عسكرية ودينية محترمة، نافية تماماً خرافة السخرة أو الكائنات الفضائية."
            case "enthusiast":
                text = "أهلاً بكم في جولة \(poiName) المتعمقة. نكشف هنا الهندسة الفريدة لغرفة الملك والبهو العظيم، ونتتبع كيف نُقلت كتل الجرانيت البالغة 50 طناً من محاجر أسوان على بعد 800 كم عبر فيضان النيل المقدس."
            default:
                text = "أهلاً بك عند \(poiName)، الأعجوبة الوحيدة الباقية من عجائب العالم القديم. استمتع بمشاهدة الكتل الحجرية الضخمة التي بنيت بارتفاع 146 متراً لتمجيد روح الملك خفرع وخوفو نحو السماء."
            }
        case "fr":
            narrator = "Dr. Marie Dubois (Institut Français d'Archéologie Orientale)"
            switch expertise {
            case "scholar":
                text = "Bienvenue au site de \(poiName). L'analyse architecturale et les papyrus de Wadi al-Jarf attestent de la logistique magistrale déployée par la IVe dynastie sous la direction de Khéops, réfutant catégoriquement les thèses d'esclavage massif."
            case "enthusiast":
                text = "Explorez la majestueuse structure de \(poiName). Du couloir ascendant à la Chambre du Roi, découvrez comment les astronomes égyptiens ont aligné l'édifice avec une précision millimétrique sur le nord géographique."
            default:
                text = "Bienvenue devant \(poiName), l'ultime merveille du monde antique encore debout. Admirez ses 2,3 millions de blocs de calcaire érigés il y a plus de 4 500 ans."
            }
        default:
            narrator = "Dr. James Wilson (Chief Egyptology Curator)"
            switch expertise {
            case "scholar":
                text = "Welcome to \(poiName). Archaeological findings from Mark Lehner's Workers' Village excavations and the Diary of Merer unequivocally confirm that skilled, well-provisioned Egyptian builders engineered this 146.6m colossus during the 4th Dynasty, refuting outdated myths of enslaved labor."
            case "enthusiast":
                text = "Stand in awe before \(poiName). We investigate the Grand Gallery's corbelled vaulting, the 50-ton red granite relieving chambers above the King's Chamber, and the stellar shafts aligned precisely with Orion and Thuban."
            default:
                text = "Welcome to \(poiName), the last remaining Wonder of the Ancient World. Built over 4,500 years ago with 2.3 million megalithic stones, it stood as the tallest human-made structure on Earth for nearly four millennia."
            }
        }
        
        self.currentTranscript = "Narrator: \(narrator)\n\n\(text)"
    }
}

fileprivate extension Comparable {
    func clamped(to limits: ClosedRange<Self>) -> Self {
        return min(max(self, limits.lowerBound), limits.upperBound)
    }
}
