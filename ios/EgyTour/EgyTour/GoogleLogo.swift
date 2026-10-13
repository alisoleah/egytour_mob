import SwiftUI

struct GoogleLogo: View {
    var body: some View {
        Canvas { context, size in
            let rect = CGRect(origin: .zero, size: size)

            // Paths for each color of the Google 'G'
            let bluePath = Path { path in
                path.move(to: CGPoint(x: size.width * 0.5, y: 0))
                path.addArc(center: CGPoint(x: size.width * 0.5, y: size.height * 0.5), radius: size.width * 0.5, startAngle: .degrees(-90), endAngle: .degrees(0), clockwise: false)
                path.addLine(to: CGPoint(x: size.width, y: size.height * 0.5))
                path.addLine(to: CGPoint(x: size.width, y: size.height * 0.9))
                path.addArc(center: CGPoint(x: size.width * 0.5, y: size.height * 0.5), radius: size.width * 0.4, startAngle: .degrees(0), endAngle: .degrees(-90), clockwise: true)
            }
            context.fill(bluePath, with: .color(Color(red: 66/255, green: 133/255, blue: 244/255)))

            let redPath = Path { path in
                path.move(to: CGPoint(x: size.width * 0.5, y: 0))
                path.addArc(center: CGPoint(x: size.width * 0.5, y: size.height * 0.5), radius: size.width * 0.5, startAngle: .degrees(-90), endAngle: .degrees(-180), clockwise: false)
                path.addLine(to: CGPoint(x: 0, y: size.height * 0.5))
                path.addLine(to: CGPoint(x: 0, y: size.height * 0.1))
                path.addArc(center: CGPoint(x: size.width * 0.5, y: size.height * 0.5), radius: size.width * 0.4, startAngle: .degrees(-180), endAngle: .degrees(-90), clockwise: true)
            }
            context.fill(redPath, with: .color(Color(red: 234/255, green: 67/255, blue: 53/255)))

            let yellowPath = Path { path in
                path.move(to: CGPoint(x: 0, y: size.height * 0.5))
                path.addArc(center: CGPoint(x: size.width * 0.5, y: size.height * 0.5), radius: size.width * 0.5, startAngle: .degrees(180), endAngle: .degrees(90), clockwise: false)
                path.addLine(to: CGPoint(x: size.width * 0.5, y: size.height))
                path.addLine(to: CGPoint(x: size.width * 0.1, y: size.height))
                path.addArc(center: CGPoint(x: size.width * 0.5, y: size.height * 0.5), radius: size.width * 0.4, startAngle: .degrees(90), endAngle: .degrees(180), clockwise: true)
            }
            context.fill(yellowPath, with: .color(Color(red: 251/255, green: 188/255, blue: 5/255)))

            let greenPath = Path { path in
                path.move(to: CGPoint(x: size.width, y: size.height * 0.5))
                path.addArc(center: CGPoint(x: size.width * 0.5, y: size.height * 0.5), radius: size.width * 0.5, startAngle: .degrees(0), endAngle: .degrees(90), clockwise: false)
                path.addLine(to: CGPoint(x: size.width * 0.5, y: size.height))
                path.addLine(to: CGPoint(x: size.width * 0.9, y: size.height))
                path.addArc(center: CGPoint(x: size.width * 0.5, y: size.height * 0.5), radius: size.width * 0.4, startAngle: .degrees(90), endAngle: .degrees(0), clockwise: true)
            }
            context.fill(greenPath, with: .color(Color(red: 52/255, green: 168/255, blue: 83/255)))
        }
    }
}
