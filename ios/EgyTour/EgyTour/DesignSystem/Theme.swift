import SwiftUI

extension Color {
    static let primaryGold = Color(hex: "E6A800")
    static let nileBlue = Color(hex: "0095C4")
    static let papyrusOrange = Color(hex: "E67E22")
    
    static let bgPrimary = Color("BgPrimary") // Define in Assets.xcassets
    static let bgSecondary = Color("BgSecondary")
    
    static let textPrimary = Color.white
    static let textSecondary = Color.white.opacity(0.8)
    static let textTertiary = Color.white.opacity(0.6)
    
    static let border = Color.white.opacity(0.1)
    
    init(hex: String) {
        let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hex.count {
        case 3: // RGB (12-bit)
            (a, r, g, b) = (255, (int >> 8) * 17, (int >> 4 & 0xF) * 17, (int & 0xF) * 17)
        case 6: // RGB (24-bit)
            (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        case 8: // ARGB (32-bit)
            (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
        default:
            (a, r, g, b) = (1, 1, 1, 0)
        }
        
        self.init(
            .sRGB,
            red: Double(r) / 255,
            green: Double(g) / 255,
            blue: Double(b) / 255,
            opacity: Double(a) / 255
        )
    }
}

extension Font {
    static let displayLarge = Font.custom("PlayfairDisplay-Bold", size: 32)
    static let displayMedium = Font.custom("PlayfairDisplay-SemiBold", size: 28)
    static let displaySmall = Font.custom("PlayfairDisplay-Regular", size: 24)
    
    static let bodyLarge = Font.system(size: 18)
    static let bodyMedium = Font.system(size: 16)
    static let bodySmall = Font.system(size: 14)
    
    static let siteName = Font.custom("PlayfairDisplay-Bold", size: 22)
    static let poiTitle = Font.system(size: 18, weight: .semibold)
}
