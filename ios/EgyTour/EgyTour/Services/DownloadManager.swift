import Foundation
import Combine

class DownloadManager: NSObject, ObservableObject {
    static let shared = DownloadManager()
    
    @Published var activeDownloads: [String: Double] = [:] // Pack/POI ID: Progress
    @Published var completedDownloads: Set<String> = ["1"] // Default with Giza Pack
    @Published var appStorageUsedMb: Int = 450
    
    private var urlSession: URLSession!
    private var downloadTasks: [String: URLSessionDownloadTask] = [:]
    private var simulatedTimers: [String: Timer] = [:]
    
    override init() {
        super.init()
        let config = URLSessionConfiguration.default
        urlSession = URLSession(configuration: config, delegate: self, delegateQueue: nil)
        restoreCompletedDownloads()
        recalculateStorage()
    }
    
    func downloadAudio(for poiId: String, url: URL) {
        if isFileDownloaded(poiId: poiId) { return }
        
        let task = urlSession.downloadTask(with: url)
        task.taskDescription = poiId
        downloadTasks[poiId] = task
        activeDownloads[poiId] = 0.0
        task.resume()
    }
    
    func downloadPack(packId: String, onProgress: ((Double) -> Void)? = nil, onComplete: @escaping () -> Void) {
        if isPackDownloaded(packId: packId) { return }
        
        activeDownloads[packId] = 0.05
        var currentProgress: Double = 0.05
        
        simulatedTimers[packId]?.invalidate()
        let timer = Timer.scheduledTimer(withTimeInterval: 0.15, repeats: true) { [weak self] t in
            guard let self = self else { return }
            currentProgress += 0.05
            if currentProgress >= 1.0 {
                t.invalidate()
                self.simulatedTimers.removeValue(forKey: packId)
                self.activeDownloads.removeValue(forKey: packId)
                self.completedDownloads.insert(packId)
                self.savePackOfflineFiles(packId: packId)
                self.recalculateStorage()
                onComplete()
            } else {
                self.activeDownloads[packId] = currentProgress
                onProgress?(currentProgress)
            }
        }
        simulatedTimers[packId] = timer
    }
    
    func deletePack(packId: String) {
        simulatedTimers[packId]?.invalidate()
        simulatedTimers.removeValue(forKey: packId)
        activeDownloads.removeValue(forKey: packId)
        completedDownloads.remove(packId)
        
        let documents = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
        let packDir = documents.appendingPathComponent("packs/\(packId)")
        try? FileManager.default.removeItem(at: packDir)
        
        if packId == "1" {
            let files = (try? FileManager.default.contentsOfDirectory(at: documents, includingPropertiesForKeys: nil)) ?? []
            for f in files where f.lastPathComponent.hasPrefix("giza-1_") {
                try? FileManager.default.removeItem(at: f)
            }
        }
        recalculateStorage()
    }
    
    func isPackDownloaded(packId: String) -> Bool {
        return completedDownloads.contains(packId)
    }
    
    func isFileDownloaded(poiId: String) -> Bool {
        let fileURL = localFileURL(for: poiId)
        return FileManager.default.fileExists(atPath: fileURL.path)
    }
    
    func localFileURL(for poiId: String) -> URL {
        let documents = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
        return documents.appendingPathComponent("\(poiId).mp3")
    }
    
    func getStorageStats() -> (usedMb: Int, freeGb: Double, totalGb: Double) {
        let fileManager = FileManager.default
        let path = NSHomeDirectory()
        var freeGb: Double = 84.5
        var totalGb: Double = 128.0
        
        if let attrs = try? fileManager.attributesOfFileSystem(forPath: path) {
            if let freeSize = attrs[.systemFreeSize] as? NSNumber,
               let totalSize = attrs[.systemSize] as? NSNumber {
                freeGb = freeSize.doubleValue / (1024 * 1024 * 1024)
                totalGb = totalSize.doubleValue / (1024 * 1024 * 1024)
            }
        }
        return (appStorageUsedMb, freeGb, totalGb)
    }
    
    private func savePackOfflineFiles(packId: String) {
        let documents = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
        let packDir = documents.appendingPathComponent("packs/\(packId)")
        try? FileManager.default.createDirectory(at: packDir, withIntermediateDirectories: true)
        
        let manifest = packDir.appendingPathComponent("manifest.json")
        try? "{\"packId\": \"\(packId)\"}".write(to: manifest, atomically: true, encoding: .utf8)
        
        if packId == "1" {
            let langs = ["en", "ar", "fr"]
            let tiers = ["casual", "enthusiast", "scholar"]
            for l in langs {
                for t in tiers {
                    let audioFile = documents.appendingPathComponent("giza-1_\(l)_\(t).mp3")
                    if !FileManager.default.fileExists(atPath: audioFile.path) {
                        try? "OFFLINE_AUDIO_CACHED_STREAM".write(to: audioFile, atomically: true, encoding: .utf8)
                    }
                }
            }
        }
    }
    
    private func recalculateStorage() {
        var total: Int = 50 // Base app footprint
        if completedDownloads.contains("1") { total += 450 }
        if completedDownloads.contains("2") { total += 620 }
        if completedDownloads.contains("3") { total += 380 }
        if completedDownloads.contains("4") { total += 290 }
        if completedDownloads.contains("5") { total += 410 }
        DispatchQueue.main.async {
            self.appStorageUsedMb = total
        }
    }
    
    private func restoreCompletedDownloads() {
        let documents = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
        let packsDir = documents.appendingPathComponent("packs")
        if let contents = try? FileManager.default.contentsOfDirectory(atPath: packsDir.path) {
            for p in contents {
                completedDownloads.insert(p)
            }
        }
    }
}

extension DownloadManager: URLSessionDownloadDelegate {
    func urlSession(_ session: URLSession, downloadTask: URLSessionDownloadTask, didFinishDownloadingTo location: URL) {
        guard let poiId = downloadTask.taskDescription else { return }
        let destinationURL = localFileURL(for: poiId)
        
        do {
            if FileManager.default.fileExists(atPath: destinationURL.path) {
                try FileManager.default.removeItem(at: destinationURL)
            }
            try FileManager.default.moveItem(at: location, to: destinationURL)
            
            DispatchQueue.main.async {
                self.completedDownloads.insert(poiId)
                self.activeDownloads[poiId] = nil
                self.downloadTasks[poiId] = nil
            }
        } catch {
            print("File move error: \(error)")
        }
    }
    
    func urlSession(_ session: URLSession, downloadTask: URLSessionDownloadTask, didWriteData bytesWritten: Int64, totalBytesWritten: Int64, totalBytesExpectedToWrite: Int64) {
        guard let poiId = downloadTask.taskDescription else { return }
        let progress = Double(totalBytesWritten) / Double(totalBytesExpectedToWrite)
        
        DispatchQueue.main.async {
            self.activeDownloads[poiId] = progress
        }
    }
    
    func urlSession(_ session: URLSession, task: URLSessionTask, didCompleteWithError error: Error?) {
        if let error = error {
            print("Download error: \(error)")
            if let poiId = task.taskDescription {
                DispatchQueue.main.async {
                    self.activeDownloads[poiId] = nil
                }
            }
        }
    }
}
