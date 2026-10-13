import SwiftUI

struct SubMonumentItemIOS: Identifiable {
    let id: String
    let title: String
    let era: String
    let duration: String
    let imageName: String
}

struct MonumentDetailView: View {
    var title: String = "Giza Pyramid Complex"
    var arabicTitle: String = "مجمع أهرامات الجيزة"
    var region: String = "Giza Plateau, Greater Cairo"
    var description: String = "The Giza pyramid complex is an archaeological site on the Giza Plateau. It includes the three Great Pyramids (Khufu, Khafre, Menkaure), the Great Sphinx, several cemeteries, and a workers' village. Constructed over 4,500 years ago during the Fourth Dynasty of the Old Kingdom."
    var isUnlocked: BooleanLiteralType = false
    
    var onBack: () -> Void = {}
    var onPlayAudio: () -> Void = {}
    var onStartAR: () -> Void = {}
    var onUnlockZone: () -> Void = {}
    
    private let subItems = [
        SubMonumentItemIOS(id: "1", title: "Great Pyramid of Khufu", era: "4th Dynasty (2560 BCE)", duration: "12 min", imageName: "great_pyramid"),
        SubMonumentItemIOS(id: "2", title: "The Great Sphinx", era: "Pharaoh Khafre", duration: "8 min", imageName: "pharaoh_golden_statue"),
        SubMonumentItemIOS(id: "3", title: "Khafre Pyramid", era: "Old Kingdom", duration: "9 min", imageName: "giza_pyramids_purchase"),
        SubMonumentItemIOS(id: "4", title: "Khan el-Khalili Gateway", era: "Mamluk Era", duration: "15 min", imageName: "khan_el_khalili_market"),
        SubMonumentItemIOS(id: "5", title: "Luxor Colonnade", era: "New Kingdom", duration: "14 min", imageName: "luxor_temple")
    ]
    
    var body: some View {
        ZStack(alignment: .bottom) {
            Color(hex: "1A1A1A").ignoresSafeArea()
            
            ScrollView {
                VStack(spacing: 0) {
                    // Hero Image Banner
                    heroBanner
                    
                    // Main Content
                    VStack(alignment: .leading, spacing: 20) {
                        // Title Header
                        VStack(alignment: .leading, spacing: 4) {
                            Text(title)
                                .font(.system(size: 26, weight: .bold))
                                .foregroundColor(.white)
                            
                            Text(arabicTitle)
                                .font(.system(size: 16))
                                .foregroundColor(.primaryGold)
                            
                            HStack(spacing: 4) {
                                Image(systemName: "mappin.and.ellipse")
                                    .font(.system(size: 12))
                                    .foregroundColor(.white.opacity(0.6))
                                Text(region)
                                    .font(.system(size: 13))
                                    .foregroundColor(.white.opacity(0.6))
                            }
                            .padding(.top, 2)
                        }
                        
                        // Stat Pills
                        HStack(spacing: 10) {
                            StatPillIOS(label: "Hours", value: "08:00 - 17:00", icon: "clock.fill")
                            StatPillIOS(label: "Entry Fee", value: "EGP 200 / 60", icon: "ticket.fill")
                            StatPillIOS(label: "Audios", value: "45 Guides", icon: "headphones")
                        }
                        
                        // Audio Guide Preview Banner
                        Button(action: onPlayAudio) {
                            HStack(spacing: 14) {
                                ZStack {
                                    Circle()
                                        .fill(Color.primaryGold)
                                        .frame(width: 44, height: 44)
                                    Image(systemName: "play.fill")
                                        .font(.system(size: 18))
                                        .foregroundColor(.black)
                                }
                                
                                VStack(alignment: .leading, spacing: 2) {
                                    Text("Official Archaeological Narration")
                                        .font(.system(size: 14, weight: .bold))
                                        .foregroundColor(.white)
                                    Text("Curated by Egypt Antiquities Authority")
                                        .font(.system(size: 12))
                                        .foregroundColor(.white.opacity(0.6))
                                }
                                
                                Spacer()
                                
                                Image(systemName: "chevron.right")
                                    .font(.system(size: 14, weight: .semibold))
                                    .foregroundColor(.white.opacity(0.4))
                            }
                            .padding(14)
                            .background(Color(hex: "262626"))
                            .cornerRadius(14)
                            .overlay(RoundedRectangle(cornerRadius: 14).stroke(Color.white.opacity(0.1), lineWidth: 1))
                        }
                        .buttonStyle(PlainButtonStyle())
                        
                        // Overview Description
                        VStack(alignment: .leading, spacing: 8) {
                            Text("Overview")
                                .font(.system(size: 18, weight: .bold))
                                .foregroundColor(.white)
                            
                            Text(description)
                                .font(.system(size: 14))
                                .foregroundColor(.white.opacity(0.75))
                                .lineSpacing(4)
                        }
                        
                        // Key POIs in this Zone Carousel
                        VStack(alignment: .leading, spacing: 12) {
                            Text("Points of Interest Inside")
                                .font(.system(size: 18, weight: .bold))
                                .foregroundColor(.white)
                            
                            ScrollView(.horizontal, showsIndicators: false) {
                                HStack(spacing: 12) {
                                    ForEach(subItems) { item in
                                        VStack(alignment: .leading, spacing: 8) {
                                            Image(item.imageName)
                                                .resizable()
                                                .scaledToFill()
                                                .frame(width: 170, height: 110)
                                                .clipped()
                                            
                                            VStack(alignment: .leading, spacing: 4) {
                                                Text(item.title)
                                                    .font(.system(size: 14, weight: .bold))
                                                    .foregroundColor(.white)
                                                    .lineLimit(1)
                                                
                                                Text(item.era)
                                                    .font(.system(size: 11))
                                                    .foregroundColor(.white.opacity(0.5))
                                                    .lineLimit(1)
                                                
                                                HStack(spacing: 4) {
                                                    Image(systemName: "headphones")
                                                        .font(.system(size: 10))
                                                        .foregroundColor(.primaryGold)
                                                    Text(item.duration)
                                                        .font(.system(size: 11, weight: .medium))
                                                        .foregroundColor(.primaryGold)
                                                }
                                            }
                                            .padding(.horizontal, 10)
                                            .padding(.bottom, 10)
                                        }
                                        .frame(width: 170)
                                        .background(Color(hex: "262626"))
                                        .cornerRadius(12)
                                        .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.white.opacity(0.1), lineWidth: 1))
                                    }
                                }
                            }
                        }
                        
                        Spacer().frame(height: 100)
                    }
                    .padding(20)
                }
            }
            
            // Bottom Action Bar
            bottomActionBar
        }
        .navigationBarHidden(true)
    }
    
    private var heroBanner: some View {
        ZStack(alignment: .top) {
            Image("great_pyramid")
                .resizable()
                .scaledToFill()
                .frame(maxWidth: .infinity)
                .frame(height: 320)
                .clipped()
            
            // Gradient Overlay
            LinearGradient(
                gradient: Gradient(colors: [
                    Color.black.opacity(0.7),
                    Color.clear,
                    Color(hex: "1A1A1A")
                ]),
                startPoint: .top,
                endPoint: .bottom
            )
            .frame(height: 320)
            
            // Top Controls
            HStack {
                Button(action: onBack) {
                    Image(systemName: "arrow.left")
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(.white)
                        .padding(10)
                        .background(Color.black.opacity(0.5))
                        .clipShape(Circle())
                }
                
                Spacer()
                
                HStack(spacing: 8) {
                    Button(action: {}) {
                        Image(systemName: "arrow.down.circle")
                            .font(.system(size: 16))
                            .foregroundColor(.white)
                            .padding(10)
                            .background(Color.black.opacity(0.5))
                            .clipShape(Circle())
                    }
                    
                    Button(action: {}) {
                        Image(systemName: "square.and.arrow.up")
                            .font(.system(size: 16))
                            .foregroundColor(.white)
                            .padding(10)
                            .background(Color.black.opacity(0.5))
                            .clipShape(Circle())
                    }
                }
            }
            .padding(.horizontal, 16)
            .padding(.top, 50)
            
            // Tags pinned near bottom of hero
            VStack {
                Spacer()
                HStack(spacing: 8) {
                    Text("UNESCO HERITAGE")
                        .font(.system(size: 10, weight: .bold))
                        .foregroundColor(.black)
                        .padding(.horizontal, 6)
                        .padding(.vertical, 3)
                        .background(Color.primaryGold)
                        .cornerRadius(4)
                    
                    Text("4th Dynasty (c. 2560 BCE)")
                        .font(.system(size: 10, weight: .semibold))
                        .foregroundColor(.white)
                        .padding(.horizontal, 6)
                        .padding(.vertical, 3)
                        .background(Color.white.opacity(0.2))
                        .cornerRadius(4)
                    
                    Spacer()
                }
                .padding(.horizontal, 20)
                .padding(.bottom, 12)
            }
            .frame(height: 320)
        }
    }
    
    private var bottomActionBar: some View {
        HStack(spacing: 12) {
            if isUnlocked {
                Button(action: onPlayAudio) {
                    HStack {
                        Image(systemName: "play.fill")
                        Text("Play Audio Guide")
                    }
                    .font(.system(size: 15, weight: .bold))
                    .foregroundColor(.black)
                    .frame(maxWidth: .infinity)
                    .frame(height: 50)
                    .background(Color.primaryGold)
                    .cornerRadius(12)
                }
                
                Button(action: onStartAR) {
                    HStack {
                        Image(systemName: "location.north.fill")
                            .foregroundColor(.primaryGold)
                        Text("AR Wayfind")
                    }
                    .font(.system(size: 15, weight: .semibold))
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .frame(height: 50)
                    .background(Color(hex: "262626"))
                    .cornerRadius(12)
                    .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.white.opacity(0.1), lineWidth: 1))
                }
            } else {
                Button(action: onUnlockZone) {
                    HStack {
                        Image(systemName: "lock.open.fill")
                        Text("Unlock Zone Pass (EGP 450)")
                    }
                    .font(.system(size: 16, weight: .bold))
                    .foregroundColor(.black)
                    .frame(maxWidth: .infinity)
                    .frame(height: 52)
                    .background(Color.primaryGold)
                    .cornerRadius(12)
                }
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 14)
        .background(Color(hex: "1A1A1A").edgesIgnoringSafeArea(.bottom))
        .overlay(Rectangle().frame(height: 1).foregroundColor(Color.white.opacity(0.1)), alignment: .top)
    }
}

private struct StatPillIOS: View {
    let label: String
    let value: String
    let icon: String
    
    var body: some View {
        VStack(spacing: 4) {
            Image(systemName: icon)
                .font(.system(size: 16))
                .foregroundColor(.primaryGold)
            Text(label)
                .font(.system(size: 10))
                .foregroundColor(.white.opacity(0.5))
            Text(value)
                .font(.system(size: 11, weight: .bold))
                .foregroundColor(.white)
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, 10)
        .background(Color(hex: "262626"))
        .cornerRadius(10)
        .overlay(RoundedRectangle(cornerRadius: 10).stroke(Color.white.opacity(0.1), lineWidth: 1))
    }
}
