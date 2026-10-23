import SwiftUI

enum PackStateIOS {
    case downloaded
    case downloading(Double)
    case notDownloaded
}

struct RegionPackIOS: Identifiable {
    let id: String
    let title: String
    let subtitle: String
    let sizeMb: Int
}

struct OfflineManagerView: View {
    var onDismiss: () -> Void = {}
    
    @ObservedObject var downloadManager = DownloadManager.shared
    @State private var wifiOnly = true
    @State private var autoSync = false
    
    let packs: [RegionPackIOS] = [
        RegionPackIOS(id: "1", title: "Giza Plateau & Pyramids", subtitle: "All 5 POIs, 45 Audio Guides, 3D AR Assets", sizeMb: 450),
        RegionPackIOS(id: "2", title: "Luxor & Karnak Temples", subtitle: "Great Hypostyle Hall, Sacred Lake, Avenue of Sphinxes", sizeMb: 620),
        RegionPackIOS(id: "3", title: "Valley of the Kings", subtitle: "Tutankhamun, Ramses VI, Seti I tombs", sizeMb: 380),
        RegionPackIOS(id: "4", title: "Historic Islamic & Coptic Cairo", subtitle: "Khan el-Khalili, Citadel, Hanging Church", sizeMb: 290),
        RegionPackIOS(id: "5", title: "Aswan & Abu Simbel Temples", subtitle: "Philae Island, Colossal Ramses II Statues", sizeMb: 410)
    ]
    
    var body: some View {
        NavigationView {
            ZStack {
                Color(hex: "1A1A1A").ignoresSafeArea()
                
                ScrollView {
                    VStack(spacing: 20) {
                        // Storage Gauge Card
                        storageGaugeCard
                        
                        // Download Settings
                        downloadSettingsCard
                        
                        // Regional Packs Section
                        VStack(alignment: .leading, spacing: 12) {
                            Text("Monument Offline Packs")
                                .font(.system(size: 18, weight: .bold))
                                .foregroundColor(.white)
                            
                            VStack(spacing: 12) {
                                ForEach(packs) { pack in
                                    packCard(for: pack)
                                }
                            }
                        }
                    }
                    .padding(16)
                }
            }
            .navigationTitle("Offline Manager")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button(action: onDismiss) {
                        Image(systemName: "chevron.left")
                            .font(.system(size: 16, weight: .semibold))
                            .foregroundColor(.white)
                    }
                }
            }
        }
    }
    
    private var storageGaugeCard: some View {
        let stats = downloadManager.getStorageStats()
        let usedGb = Double(stats.usedMb) / 1024.0
        let percent = (stats.totalGb > 0) ? (usedGb / stats.totalGb) : 0.01
        
        return VStack(alignment: .leading, spacing: 14) {
            HStack {
                Text("DEVICE STORAGE")
                    .font(.system(size: 11, weight: .bold))
                    .foregroundColor(.white.opacity(0.5))
                    .tracking(1.2)
                
                Spacer()
                
                Text("High-Speed Storage")
                    .font(.system(size: 10, weight: .bold))
                    .foregroundColor(.primaryGold)
                    .padding(.horizontal, 6)
                    .padding(.vertical, 2)
                    .background(Color.primaryGold.opacity(0.15))
                    .cornerRadius(4)
            }
            
            HStack(alignment: .bottom) {
                VStack(alignment: .leading, spacing: 2) {
                    Text("EgyTour Used")
                        .font(.system(size: 12))
                        .foregroundColor(.white.opacity(0.7))
                    Text(String(format: "%.1f GB", usedGb))
                        .font(.system(size: 30, weight: .bold))
                        .foregroundColor(.primaryGold)
                }
                
                Spacer()
                
                VStack(alignment: .trailing, spacing: 2) {
                    Text("Total Device")
                        .font(.system(size: 11))
                        .foregroundColor(.white.opacity(0.5))
                    Text(String(format: "%.0f GB", stats.totalGb))
                        .font(.system(size: 14, weight: .semibold))
                        .foregroundColor(.white.opacity(0.8))
                }
            }
            
            // Progress Bar
            GeometryReader { geo in
                ZStack(alignment: .leading) {
                    RoundedRectangle(cornerRadius: 4)
                        .fill(Color.white.opacity(0.1))
                        .frame(height: 8)
                    
                    RoundedRectangle(cornerRadius: 4)
                        .fill(Color.primaryGold)
                        .frame(width: max(8, geo.size.width * CGFloat(percent)), height: 8)
                }
            }
            .frame(height: 8)
            
            Text("EgyTour offline packs use \(String(format: "%.1f", percent * 100))% of storage (\(String(format: "%.1f", stats.freeGb)) GB free).")
                .font(.system(size: 11))
                .foregroundColor(.white.opacity(0.5))
        }
        .padding(18)
        .background(Color(hex: "262626"))
        .cornerRadius(16)
        .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.primaryGold.opacity(0.3), lineWidth: 1))
    }
    
    private var downloadSettingsCard: some View {
        VStack(spacing: 14) {
            Toggle(isOn: $wifiOnly) {
                VStack(alignment: .leading, spacing: 2) {
                    Text("Download over Wi-Fi Only")
                        .font(.system(size: 14, weight: .semibold))
                        .foregroundColor(.white)
                    Text("Avoid cellular data consumption")
                        .font(.system(size: 12))
                        .foregroundColor(.white.opacity(0.6))
                }
            }
            .toggleStyle(SwitchToggleStyle(tint: .primaryGold))
            
            Divider().background(Color.white.opacity(0.1))
            
            Toggle(isOn: $autoSync) {
                VStack(alignment: .leading, spacing: 2) {
                    Text("Auto-Sync Scheduled Trips")
                        .font(.system(size: 14, weight: .semibold))
                        .foregroundColor(.white)
                    Text("Pre-cache stops from your active itineraries")
                        .font(.system(size: 12))
                        .foregroundColor(.white.opacity(0.6))
                }
            }
            .toggleStyle(SwitchToggleStyle(tint: .primaryGold))
        }
        .padding(16)
        .background(Color(hex: "262626"))
        .cornerRadius(14)
        .overlay(RoundedRectangle(cornerRadius: 14).stroke(Color.white.opacity(0.1), lineWidth: 1))
    }
    
    private func packCard(for pack: RegionPackIOS) -> some View {
        let isDownloaded = downloadManager.isPackDownloaded(packId: pack.id)
        let downloadProgress = downloadManager.activeDownloads[pack.id]
        
        return VStack(alignment: .leading, spacing: 10) {
            HStack(alignment: .top) {
                VStack(alignment: .leading, spacing: 4) {
                    Text(pack.title)
                        .font(.system(size: 15, weight: .bold))
                        .foregroundColor(.white)
                    
                    Text(pack.subtitle)
                        .font(.system(size: 12))
                        .foregroundColor(.white.opacity(0.6))
                    
                    Text("\(pack.sizeMb) MB")
                        .font(.system(size: 12, weight: .bold))
                        .foregroundColor(.primaryGold)
                        .padding(.top, 2)
                }
                
                Spacer()
                
                if isDownloaded {
                    Button(action: {
                        downloadManager.deletePack(packId: pack.id)
                    }) {
                        Image(systemName: "checkmark.circle.fill")
                            .font(.system(size: 24))
                            .foregroundColor(Color(hex: "4CAF50"))
                    }
                } else if let progress = downloadProgress {
                    Button(action: {
                        downloadManager.deletePack(packId: pack.id)
                    }) {
                        Image(systemName: "pause.circle.fill")
                            .font(.system(size: 24))
                            .foregroundColor(.primaryGold)
                    }
                } else {
                    Button(action: {
                        downloadManager.downloadPack(packId: pack.id) {}
                    }) {
                        Image(systemName: "arrow.down.circle")
                            .font(.system(size: 24))
                            .foregroundColor(.white.opacity(0.8))
                    }
                }
            }
            
            if let progress = downloadProgress {
                VStack(alignment: .leading, spacing: 4) {
                    GeometryReader { geo in
                        ZStack(alignment: .leading) {
                            RoundedRectangle(cornerRadius: 2)
                                .fill(Color.white.opacity(0.1))
                                .frame(height: 4)
                            RoundedRectangle(cornerRadius: 2)
                                .fill(Color.primaryGold)
                                .frame(width: geo.size.width * CGFloat(progress), height: 4)
                        }
                    }
                    .frame(height: 4)
                    
                    Text("Downloading... \(Int(progress * 100))%")
                        .font(.system(size: 10))
                        .foregroundColor(.white.opacity(0.5))
                }
            }
        }
        .padding(14)
        .background(Color(hex: "262626"))
        .cornerRadius(12)
        .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.white.opacity(0.08), lineWidth: 1))
    }
}
