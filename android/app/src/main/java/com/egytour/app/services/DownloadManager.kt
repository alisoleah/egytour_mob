package com.egytour.app.services

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidDownloadManager @Inject constructor(
    private val context: Context
) {
    private val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    private val _downloadProgress = MutableStateFlow<Map<String, Float>>(emptyMap())
    val downloadProgress: StateFlow<Map<String, Float>> = _downloadProgress
    
    private val _completedDownloads = MutableStateFlow<Set<String>>(emptySet())
    val completedDownloads: StateFlow<Set<String>> = _completedDownloads

    init {
        restoreCompletedDownloads()
        registerReceiver()
    }

    fun downloadAudio(poiId: String, url: String) {
        if (isFileDownloaded(poiId)) return

        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("Audio Guide: $poiId")
            .setDescription("Downloading audio content...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_MUSIC, "$poiId.mp3")
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(false)

        downloadManager.enqueue(request)
        // Note: Progress tracking would typically require a polling loop or WorkManager for granualar updates
        // For simplicity, we'll mark as 'in progress'
        _downloadProgress.value = _downloadProgress.value.toMutableMap().apply { put(poiId, 0f) }
    }

    fun isFileDownloaded(poiId: String): Boolean {
        val file = getLocalFile(poiId)
        return file.exists()
    }

    fun getLocalFile(poiId: String): File {
        return File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "$poiId.mp3")
    }

    private fun restoreCompletedDownloads() {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val files = dir?.listFiles { _, name -> name.endsWith(".mp3") }
        val completed = files?.map { it.nameWithoutExtension }?.toSet() ?: emptySet()
        _completedDownloads.value = completed
    }

    private fun registerReceiver() {
        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        context.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id != -1L) {
                    // In a real app, verify this ID maps to our POI
                    restoreCompletedDownloads() // Brute force refresh for now
                }
            }
        }, filter, Context.RECEIVER_NOT_EXPORTED)
    }
}
