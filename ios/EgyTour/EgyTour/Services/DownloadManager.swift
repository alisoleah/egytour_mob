import Foundation
import Combine

class DownloadManager: NSObject, ObservableObject {
    static let shared = DownloadManager()
    
    @Published var activeDownloads: [String: Double] = [:] // URL: Progress
    @Published var completedDownloads: Set<String> = [] // Local file paths
    
    private var urlSession: URLSession!
    private var downloadTasks: [String: URLSessionDownloadTask] = [:]
    
    override init() {
        super.init()
        let config = URLSessionConfiguration.background(withIdentifier: "com.egytour.background_downloads")
        urlSession = URLSession(configuration: config, delegate: self, delegateQueue: nil)
        restoreCompletedDownloads()
    }
    
    func downloadAudio(for poiId: String, url: URL) {
        if isFileDownloaded(poiId: poiId) { return }
        
        let task = urlSession.downloadTask(with: url)
        task.taskDescription = poiId
        downloadTasks[poiId] = task
        activeDownloads[poiId] = 0.0
        task.resume()
    }
    
    func isFileDownloaded(poiId: String) -> Bool {
        let fileURL = localFileURL(for: poiId)
        return FileManager.default.fileExists(atPath: fileURL.path)
    }
    
    func localFileURL(for poiId: String) -> URL {
        let documents = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
        return documents.appendingPathComponent("\(poiId).mp3")
    }
    
    private func restoreCompletedDownloads() {
        // Scan documents directory for existing mp3s
        let documents = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
        do {
            let fileURLs = try FileManager.default.contentsOfDirectory(at: documents, includingPropertiesForKeys: nil)
            for url in fileURLs where url.pathExtension == "mp3" {
                let poiId = url.deletingPathExtension().lastPathComponent
                completedDownloads.insert(poiId)
            }
        } catch {
            print("Error restoring downloads: \(error)")
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
