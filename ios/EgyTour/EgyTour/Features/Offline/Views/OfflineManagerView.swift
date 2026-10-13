import SwiftUI

enum PackStateIOS {
    case downloaded
    case downloading(Float)
    case notDownloaded
}

struct RegionPackIOS: Identifiable {
    let id: String
    let title: String
    let subtitle: String
    let sizeMb: Int
    var state: PackStateIOS
}

struct OfflineManagerView: View {
    var onDismiss: () -> Void = {}
    
    @State private var wifiOnly = true
    @State private var autoSync = false
    
    @State private var packs: [RegionPackIOS] = [
        RegionPackIOS(id: "1", title: "Giza Plateau & Pyramids", subtitle: "All 5 POIs, 45 Audio Guides, 3D AR Assets", sizeMb: 450, state: .downloaded),
        RegionPackIOS(id: "2", title: "Luxor & Karnak Temples", subtitle: "Great Hypostyle Hall, Sacred Lake, Avenue of Sphinxes", sizeMb: 620, state: .downloading(0.68)),
        RegionPackIOS(id: "3", title: "Valley of the Kings", subtitle: "Tutankhamun, Ramses VI, Seti I tombs", sizeMb: 380, state: .notDownloaded),
        RegionPackIOS(id: "4", title: "Historic Islamic & Coptic Cairo", subtitle: "Khan el-Khalili, Citadel, Hanging Church", sizeMb: 290, state: .notDownloaded),
        RegionPackIOS(id: "5", title: "Aswan & Abu Simbel Temples", subtitle: "Philae Island, Colossal Ramses II Statues", sizeMb: 410, state: .notDownloaded)
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
        VStack(alignment: .leading, spacing: 14) {
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
                    Text("1.2 GB")
                        .font(.system(size: 30, weight: .bold))
                        .foregroundColor(.primaryGold)
                }
                
                Spacer()
                
                Text("Total Capacity: 256 GB")
                    .font(.system(size: 12))
                    .foregroundColor(.white.opacity(0.5))
            }
            
            // Progress Bar
            GeometryReader { geo in
                ZStack(alignment: .leading) {
                    RoundedRectangle(cornerRadius: 4)
                        .fill(Color.white.opacity(0.08))
                        .frame(height: 8)
                    
                    RoundedRectangle(cornerRadius: 4)
                        .fill(Color.primaryGold)
                        .frame(width: geo.size.width * 0.35, height: 8)
                }
            }
            .frame(height: 8)
            
            Text("EgyTour audio and 3D packs use 0.4% of your total phone storage.")
                .font(.system(size: 11))
                .foregroundColor(.white.opacity(0.5))
        }
        .padding(18)
        .background(Color(hex: "262626"))
        .cornerRadius(16)
        .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.white.opacity(0.1), lineWidth: 1))
    }
    
    private var downloadSettingsCard: some View {
        VStack(spacing: 12) {
            Toggle(isOn: $wifiOnly) {
                VStack(alignment: .leading, spacing: 2) {
                    Text("Download Over Wi-Fi Only")
                        .font(.system(size: 14, weight: .semibold))
                        .foregroundColor(.white)
                    Text("Avoid cellular data roaming charges")
                        .font(.system(size: 12))
                        .foregroundColor(.white.opacity(0.6))
                }
            }
            .toggleStyle(SwitchToggleStyle(tint: .primaryGold))
            
            Divider().background(Color.white.opacity(0.08))
            
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
        VStack(alignment: .leading, spacing: 10) {
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
                
                switch pack.state {
                case .downloaded:
                    Button(action: {
                        toggleState(for: pack.id, to: .notDownloaded)
                    }) {
                        Image(systemName: "checkmark.circle.fill")
                            .font(.system(size: 24))
                            .foregroundColor(Color(hex: "4CAF50"))
                    }
                    
                case .downloading:
                    Button(action: {
                        toggleState(for: pack.id, to: .notDownloaded)
                    }) {
                        Image(systemName: "pause.circle.fill")
                            .font(.system(size: 24))
                            .foregroundColor(.primaryGold)
                    }
                    
                case .notDownloaded:
                    Button(action: {
                        toggleState(for: pack.id, to: .downloading(0.1))
                    }) {
                        Image(systemName: "arrow.down.circle")
                            .font(.system(size: 24))
                            .foregroundColor(.white.opacity(0.8))
                    }
                }
            }
            
            if case .downloading(let progress) = pack.state {
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
    
    private func toggleState(for packId: String, to newState: PackStateIOS) {
        if let idx = packs.firstIndex(where: { $0.id == packId }) {
            packs[idx].state = newState
        }
    }
}
