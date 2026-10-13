import SwiftUI

struct PurchaseSuccessView: View {
    var zoneName: String = "Giza Necropolis Zone"
    var passTitle: String = "Giza Full Access Pass"
    var onExplore: () -> Void = {}
    var onDownloadPacks: () -> Void = {}
    var onClose: () -> Void = {}
    
    @State private var pulse = false
    
    var body: some View {
        ZStack {
            Color(hex: "1A1A1A").ignoresSafeArea()
            
            VStack {
                // Top close button
                HStack {
                    Spacer()
                    Button(action: onClose) {
                        Image(systemName: "xmark")
                            .font(.system(size: 14, weight: .bold))
                            .foregroundColor(.white)
                            .padding(10)
                            .background(Color.white.opacity(0.1))
                            .clipShape(Circle())
                    }
                }
                .padding(.horizontal, 20)
                .padding(.top, 16)
                
                Spacer()
                
                VStack(spacing: 24) {
                    // Animated glow badge
                    ZStack {
                        Circle()
                            .fill(Color.primaryGold.opacity(0.18))
                            .frame(width: 110, height: 110)
                            .scaleEffect(pulse ? 1.15 : 1.0)
                            .animation(Animation.easeInOut(duration: 1.2).repeatForever(autoreverses: true), value: pulse)
                        
                        Circle()
                            .fill(Color(hex: "262626"))
                            .frame(width: 80, height: 80)
                            .overlay(
                                Circle().stroke(Color.primaryGold.opacity(0.4), lineWidth: 2)
                            )
                        
                        Image(systemName: "checkmark.circle.fill")
                            .font(.system(size: 52))
                            .foregroundColor(.primaryGold)
                    }
                    .onAppear { pulse = true }
                    
                    VStack(spacing: 8) {
                        Text("Zone Unlocked!")
                            .font(.system(size: 30, weight: .bold))
                            .foregroundColor(.white)
                        
                        Text("You have successfully unlocked the \(zoneName) with full offline audio narration and navigation.")
                            .font(.system(size: 15))
                            .foregroundColor(.white.opacity(0.75))
                            .multilineTextAlignment(.center)
                            .padding(.horizontal, 32)
                    }
                    
                    // Receipt Card
                    HStack(spacing: 14) {
                        Image("great_pyramid")
                            .resizable()
                            .scaledToFill()
                            .frame(width: 68, height: 68)
                            .cornerRadius(12)
                            .clipped()
                        
                        VStack(alignment: .leading, spacing: 4) {
                            Text("PURCHASED")
                                .font(.system(size: 9, weight: .bold))
                                .foregroundColor(.primaryGold)
                                .padding(.horizontal, 6)
                                .padding(.vertical, 2)
                                .background(Color.primaryGold.opacity(0.15))
                                .cornerRadius(4)
                            
                            Text(passTitle)
                                .font(.system(size: 16, weight: .bold))
                                .foregroundColor(.white)
                            
                            Text("Lifetime offline pass")
                                .font(.system(size: 12))
                                .foregroundColor(.white.opacity(0.6))
                        }
                        
                        Spacer()
                    }
                    .padding(14)
                    .background(Color(hex: "262626"))
                    .cornerRadius(16)
                    .overlay(
                        RoundedRectangle(cornerRadius: 16)
                            .stroke(Color.white.opacity(0.1), lineWidth: 1)
                    )
                    .padding(.horizontal, 24)
                    
                    // Perks
                    VStack(alignment: .leading, spacing: 10) {
                        PerkRow(text: "3 Historical Narration Depths (Casual, Enthusiast, Scholar)")
                        PerkRow(text: "3D AR Wayfinding with offline bearing azimuth")
                        PerkRow(text: "High-resolution offline map tiles & GPS trigger zones")
                    }
                    .padding(.horizontal, 24)
                }
                
                Spacer()
                
                // Bottom CTAs
                VStack(spacing: 12) {
                    Button(action: onExplore) {
                        Text("Start Exploring Now")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(.black)
                            .frame(maxWidth: .infinity)
                            .frame(height: 52)
                            .background(Color.primaryGold)
                            .cornerRadius(14)
                    }
                    
                    Button(action: onDownloadPacks) {
                        HStack(spacing: 6) {
                            Image(systemName: "arrow.down.circle")
                            Text("Download Audio Packs for Offline")
                        }
                        .font(.system(size: 14, weight: .semibold))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 50)
                        .background(Color.white.opacity(0.08))
                        .cornerRadius(14)
                    }
                }
                .padding(.horizontal, 20)
                .padding(.bottom, 24)
            }
        }
    }
}

private struct PerkRow: View {
    let text: String
    
    var body: some View {
        HStack(spacing: 10) {
            Image(systemName: "checkmark")
                .font(.system(size: 12, weight: .bold))
                .foregroundColor(.primaryGold)
            
            Text(text)
                .font(.system(size: 13))
                .foregroundColor(.white.opacity(0.8))
            
            Spacer()
        }
    }
}
