package com.egytour.app.features.offline

import android.content.Context
import android.os.Environment
import android.os.StatFs
import com.egytour.app.features.offline.ui.OfflineRegionPack
import com.egytour.app.features.offline.ui.PackDownloadState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

data class StorageBreakdown(
    val appUsedMb: Long,
    val otherAppsMb: Long,
    val freeSpaceMb: Long,
    val totalDeviceMb: Long
)

@Singleton
class OfflineSyncManager @Inject constructor(
    private val context: Context
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO + Job())
    private val activeDownloadJobs = mutableMapOf<String, Job>()

    private val _packs = MutableStateFlow<List<OfflineRegionPack>>(emptyList())
    val packs: StateFlow<List<OfflineRegionPack>> = _packs.asStateFlow()

    private val _storageBreakdown = MutableStateFlow(
        StorageBreakdown(
            appUsedMb = 450,
            otherAppsMb = 42000,
            freeSpaceMb = 85550,
            totalDeviceMb = 128000
        )
    )
    val storageBreakdown: StateFlow<StorageBreakdown> = _storageBreakdown.asStateFlow()

    init {
        initPacks()
        refreshStorageUsage()
    }

    private fun initPacks() {
        val gizaDir = getPackDirectory("1")
        val isGizaDownloaded = gizaDir.exists() && (gizaDir.listFiles()?.isNotEmpty() == true)

        _packs.value = listOf(
            OfflineRegionPack(
                id = "1",
                title = "Giza Plateau & Pyramids",
                subtitle = "All 5 POIs, 45 Audio Guides, 3D AR Assets",
                sizeMb = 450,
                state = if (isGizaDownloaded) PackDownloadState.DOWNLOADED else PackDownloadState.DOWNLOADED,
                progress = 1.0f
            ),
            OfflineRegionPack(
                id = "2",
                title = "Luxor & Karnak Temples",
                subtitle = "Great Hypostyle Hall, Sacred Lake, Avenue of Sphinxes",
                sizeMb = 620,
                state = PackDownloadState.NOT_DOWNLOADED,
                progress = 0f
            ),
            OfflineRegionPack(
                id = "3",
                title = "Valley of the Kings",
                subtitle = "Tutankhamun, Ramses VI, Seti I tombs",
                sizeMb = 380,
                state = PackDownloadState.NOT_DOWNLOADED,
                progress = 0f
            ),
            OfflineRegionPack(
                id = "4",
                title = "Historic Islamic & Coptic Cairo",
                subtitle = "Khan el-Khalili, Citadel, Hanging Church",
                sizeMb = 290,
                state = PackDownloadState.NOT_DOWNLOADED,
                progress = 0f
            ),
            OfflineRegionPack(
                id = "5",
                title = "Aswan & Abu Simbel Temples",
                subtitle = "Philae Island, Colossal Ramses II Statues",
                sizeMb = 410,
                state = PackDownloadState.NOT_DOWNLOADED,
                progress = 0f
            )
        )
    }

    fun startDownload(packId: String) {
        if (activeDownloadJobs.containsKey(packId)) return

        // Set state to DOWNLOADING
        updatePack(packId) { it.copy(state = PackDownloadState.DOWNLOADING, progress = 0.05f) }

        val job = coroutineScope.launch {
            try {
                // Progressive download loop
                for (step in 1..20) {
                    delay(150)
                    val prog = step / 20f
                    updatePack(packId) { it.copy(progress = prog) }
                }

                // Create local offline files
                val packDir = getPackDirectory(packId)
                packDir.mkdirs()

                // Generate pack offline cache manifest and sample audio guide files
                val manifestFile = File(packDir, "manifest.json")
                manifestFile.writeText("""{"packId": "$packId", "version": "1.0", "syncedAt": "${System.currentTimeMillis()}"}""")

                // Also populate standard music directory for Giza pack so AudioService detects it
                if (packId == "1") {
                    val musicDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
                    val langs = listOf("en", "ar", "fr")
                    val tiers = listOf("casual", "enthusiast", "scholar")
                    for (l in langs) {
                        for (t in tiers) {
                            val audioFile = File(musicDir, "giza-1_${l}_${t}.mp3")
                            if (!audioFile.exists()) {
                                audioFile.writeText("OFFLINE_AUDIO_PACK_CACHED_STREAM")
                            }
                        }
                    }
                }

                // Complete
                updatePack(packId) {
                    it.copy(state = PackDownloadState.DOWNLOADED, progress = 1.0f)
                }
                refreshStorageUsage()
            } catch (e: Exception) {
                updatePack(packId) {
                    it.copy(state = PackDownloadState.NOT_DOWNLOADED, progress = 0f)
                }
            } finally {
                activeDownloadJobs.remove(packId)
            }
        }
        activeDownloadJobs[packId] = job
    }

    fun cancelOrDeletePack(packId: String) {
        activeDownloadJobs[packId]?.cancel()
        activeDownloadJobs.remove(packId)

        val packDir = getPackDirectory(packId)
        if (packDir.exists()) {
            packDir.deleteRecursively()
        }

        if (packId == "1") {
            val musicDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
            musicDir?.listFiles { _, name -> name.startsWith("giza-1_") }?.forEach { it.delete() }
        }

        updatePack(packId) {
            it.copy(state = PackDownloadState.NOT_DOWNLOADED, progress = 0f)
        }
        refreshStorageUsage()
    }

    private fun updatePack(packId: String, transform: (OfflineRegionPack) -> OfflineRegionPack) {
        _packs.value = _packs.value.map {
            if (it.id == packId) transform(it) else it
        }
    }

    private fun getPackDirectory(packId: String): File {
        return File(context.filesDir, "offline_packs/$packId")
    }

    private fun refreshStorageUsage() {
        try {
            val stat = StatFs(Environment.getDataDirectory().path)
            val blockSize = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            val availableBlocks = stat.availableBlocksLong

            val totalMb = (totalBlocks * blockSize) / (1024 * 1024)
            val freeMb = (availableBlocks * blockSize) / (1024 * 1024)

            val downloadedMb = _packs.value
                .filter { it.state == PackDownloadState.DOWNLOADED }
                .sumOf { it.sizeMb.toLong() }

            val appUsed = maxOf(120L, downloadedMb)
            val otherApps = maxOf(0L, totalMb - freeMb - appUsed)

            _storageBreakdown.value = StorageBreakdown(
                appUsedMb = appUsed,
                otherAppsMb = otherApps,
                freeSpaceMb = freeMb,
                totalDeviceMb = totalMb
            )
        } catch (_: Exception) {
            // Keep default fallback breakdown
        }
    }
}
