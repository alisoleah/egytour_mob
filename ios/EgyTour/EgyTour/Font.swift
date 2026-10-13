import SwiftUI

extension Font {
    static func playfairDisplay(size: CGFloat, weight: FontWeight) -> Font {
        return .custom("PlayfairDisplay-\(weight.name)", size: size)
    }

    static func inter(size: CGFloat, weight: FontWeight) -> Font {
        return .custom("Inter-\(weight.name)", size: size)
    }

    enum FontWeight {
        case regular
        case medium
        case semiBold
        case bold
        case extraBold
        case black

        var name: String {
            switch self {
            case .regular: return "Regular"
            case .medium: return "Medium"
            case .semiBold: return "SemiBold"
            case .bold: return "Bold"
            case .extraBold: return "ExtraBold"
            case .black: return "Black"
            }
        }
    }
}
