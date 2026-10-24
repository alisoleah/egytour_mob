package com.egytour.app.services

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioService @Inject constructor(
    private val context: Context
) {
    private val player: ExoPlayer = ExoPlayer.Builder(context).build()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    private var progressPollingJob: Job? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    private val _currentPositionSeconds = MutableStateFlow(0)
    val currentPositionSeconds: StateFlow<Int> = _currentPositionSeconds.asStateFlow()

    private val _durationSeconds = MutableStateFlow(120)
    val durationSeconds: StateFlow<Int> = _durationSeconds.asStateFlow()

    private val _currentPOIId = MutableStateFlow("giza-1")
    val currentPOIId: StateFlow<String> = _currentPOIId.asStateFlow()

    private val _currentPOIName = MutableStateFlow("Great Pyramid of Khufu")
    val currentPOIName: StateFlow<String> = _currentPOIName.asStateFlow()

    private val _currentLanguage = MutableStateFlow("en") // "en", "ar", "fr"
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    private val _currentExpertise = MutableStateFlow("casual") // "casual", "enthusiast", "scholar"
    val currentExpertise: StateFlow<String> = _currentExpertise.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f) // 0.75f, 1.0f, 1.25f, 1.5f
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private val _isOfflineAvailable = MutableStateFlow(false)
    val isOfflineAvailable: StateFlow<Boolean> = _isOfflineAvailable.asStateFlow()

    private val _currentTranscript = MutableStateFlow("")
    val currentTranscript: StateFlow<String> = _currentTranscript.asStateFlow()

    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                _isPlaying.value = playing
                if (playing) {
                    startProgressTracker()
                } else {
                    stopProgressTracker()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    val dur = player.duration
                    if (dur > 0) {
                        _durationSeconds.value = (dur / 1000).toInt()
                    }
                } else if (playbackState == Player.STATE_ENDED) {
                    _isPlaying.value = false
                    _progress.value = 1.0f
                    stopProgressTracker()
                }
            }
        })

        // Initial setup for default track
        loadTrack(
            poiId = "giza-1",
            poiName = "Great Pyramid of Khufu",
            language = "en",
            expertise = "casual"
        )
    }

    fun loadTrack(
        poiId: String,
        poiName: String,
        language: String = _currentLanguage.value,
        expertise: String = _currentExpertise.value
    ) {
        _currentPOIId.value = poiId
        _currentPOIName.value = poiName
        _currentLanguage.value = language
        _currentExpertise.value = expertise

        // Check if offline audio exists locally
        val localFile = getLocalAudioFile(poiId, language, expertise)
        val isLocal = localFile.exists() && localFile.length() > 0
        _isOfflineAvailable.value = isLocal

        val mediaUri = if (isLocal) {
            Uri.fromFile(localFile)
        } else {
            Uri.parse("http://10.0.2.2:3000/api/v1/audio/$poiId/$language/$expertise.mp3")
        }

        // Expected duration by expertise level
        val estimatedDur = when (expertise) {
            "scholar" -> 360
            "enthusiast" -> 240
            else -> 120
        }
        _durationSeconds.value = estimatedDur
        _currentPositionSeconds.value = 0
        _progress.value = 0f

        updateTranscript(poiName, language, expertise)

        try {
            val mediaItem = MediaItem.fromUri(mediaUri)
            player.setMediaItem(mediaItem)
            player.playbackParameters = PlaybackParameters(_playbackSpeed.value)
            player.prepare()
        } catch (_: Exception) {
            // Graceful handling for demo environments without network media codecs
        }
    }

    fun setLanguage(lang: String) {
        if (_currentLanguage.value != lang) {
            loadTrack(_currentPOIId.value, _currentPOIName.value, language = lang, expertise = _currentExpertise.value)
        }
    }

    fun setExpertise(level: String) {
        if (_currentExpertise.value != level) {
            loadTrack(_currentPOIId.value, _currentPOIName.value, language = _currentLanguage.value, expertise = level)
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        _playbackSpeed.value = speed
        player.playbackParameters = PlaybackParameters(speed)
    }

    fun cyclePlaybackSpeed() {
        val nextSpeed = when (_playbackSpeed.value) {
            0.75f -> 1.0f
            1.0f -> 1.25f
            1.25f -> 1.5f
            else -> 0.75f
        }
        setPlaybackSpeed(nextSpeed)
    }

    fun play() {
        player.play()
    }

    fun pause() {
        player.pause()
    }

    fun togglePlayPause() {
        if (_isPlaying.value) pause() else play()
    }

    fun seekTo(progressFraction: Float) {
        val clamped = progressFraction.coerceIn(0f, 1f)
        val targetMs = (clamped * _durationSeconds.value * 1000).toLong()
        player.seekTo(targetMs)
        _progress.value = clamped
        _currentPositionSeconds.value = (clamped * _durationSeconds.value).toInt()
    }

    fun seekRelative(seconds: Int) {
        val currentMs = player.currentPosition
        val targetMs = (currentMs + (seconds * 1000)).coerceIn(0, (_durationSeconds.value * 1000).toLong())
        player.seekTo(targetMs)
        val dur = _durationSeconds.value
        if (dur > 0) {
            val fraction = (targetMs / 1000f) / dur
            _progress.value = fraction.coerceIn(0f, 1f)
            _currentPositionSeconds.value = (targetMs / 1000).toInt()
        }
    }

    private fun startProgressTracker() {
        progressPollingJob?.cancel()
        progressPollingJob = coroutineScope.launch {
            while (isActive) {
                val currentMs = player.currentPosition
                val durMs = if (player.duration > 0) player.duration else (_durationSeconds.value * 1000).toLong()
                if (durMs > 0) {
                    val frac = (currentMs.toFloat() / durMs.toFloat()).coerceIn(0f, 1f)
                    _progress.value = frac
                    _currentPositionSeconds.value = (currentMs / 1000).toInt()
                }
                delay(500)
            }
        }
    }

    private fun stopProgressTracker() {
        progressPollingJob?.cancel()
        progressPollingJob = null
    }

    private fun getLocalAudioFile(poiId: String, language: String, expertise: String): File {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        return File(dir, "${poiId}_${language}_${expertise}.mp3")
    }

    private fun updateTranscript(poiName: String, language: String, expertise: String) {
        val narrator = when (language) {
            "ar" -> "د. أحمد محمد (كبير الباحثين بوزارة الآثار)"
            "fr" -> "Dr. Marie Dubois (Institut Français d'Archéologie Orientale)"
            else -> "Dr. James Wilson (Chief Egyptology Curator)"
        }

        val text = when (language) {
            "ar" -> when (expertise) {
                "scholar" -> "مرحباً بكم عند $poiName. تشير النقوش الجنائزية للأسرة الرابعة ووثائق وادي الجرف (بردية ميرير) إلى أن أعمال البناء استغرقت قرابة 27 عاماً بمشاركة نخبة الحرفيين المصريين المنظمين في فرق عمل عسكرية ودينية محترمة، نافية تماماً خرافة السخرة أو الكائنات الفضائية."
                "enthusiast" -> "أهلاً بكم في جولة $poiName المتعمقة. نكشف هنا الهندسة الفريدة لغرفة الملك والبهو العظيم، ونتتبع كيف نُقلت كتل الجرانيت البالغة 50 طناً من محاجر أسوان على بعد 800 كم عبر فيضان النيل المقدس."
                else -> "أهلاً بك عند $poiName، الأعجوبة الوحيدة الباقية من عجائب العالم القديم. استمتع بمشاهدة الكتل الحجرية الضخمة التي بنيت بارتفاع 146 متراً لتمجيد روح الملك خفرع وخوفو نحو السماء."
            }
            "fr" -> when (expertise) {
                "scholar" -> "Bienvenue au site de $poiName. L'analyse architecturale et les papyrus de Wadi al-Jarf attestent de la logistique magistrale déployée par la IVe dynastie sous la direction de Khéops, réfutant catégoriquement les thèses d'esclavage massif."
                "enthusiast" -> "Explorez la majestueuse structure de $poiName. Du couloir ascendant à la Chambre du Roi, découvrez comment les astronomes égyptiens ont aligné l'édifice avec une précision millimétrique sur le nord géographique."
                else -> "Bienvenue devant $poiName, l'ultime merveille du monde antique encore debout. Admirez ses 2,3 millions de blocs de calcaire érigés il y a plus de 4 500 ans."
            }
            else -> when (expertise) {
                "scholar" -> "Welcome to $poiName. Archaeological findings from Mark Lehner's Workers' Village excavations and the Diary of Merer unequivocally confirm that skilled, well-provisioned Egyptian builders engineered this 146.6m colossus during the 4th Dynasty, refuting outdated myths of enslaved labor."
                "enthusiast" -> "Stand in awe before $poiName. We investigate the Grand Gallery's corbelled vaulting, the 50-ton red granite relieving chambers above the King's Chamber, and the stellar shafts aligned precisely with Orion and Thuban."
                else -> "Welcome to $poiName, the last remaining Wonder of the Ancient World. Built over 4,500 years ago with 2.3 million megalithic stones, it stood as the tallest human-made structure on Earth for nearly four millennia."
            }
        }

        _currentTranscript.value = "Narrator: $narrator\n\n$text"
    }

    fun release() {
        stopProgressTracker()
        player.release()
    }
}
