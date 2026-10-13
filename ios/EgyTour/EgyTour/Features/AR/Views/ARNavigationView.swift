import SwiftUI
import ARKit
import RealityKit

struct ARNavigationView: View {
    @StateObject private var viewModel: ARViewModel
    let destination: POI
    var onDismiss: () -> Void
    
    init(destination: POI, onDismiss: @escaping () -> Void) {
        self.destination = destination
        self.onDismiss = onDismiss
        _viewModel = StateObject(wrappedValue: ARViewModel(destination: destination))
    }
    
    var body: some View {
        ZStack {
            ARViewContainer(viewModel: viewModel)
                .edgesIgnoringSafeArea(.all)
            
            VStack {
                // Top Header Bar
                HStack {
                    Button(action: onDismiss) {
                        Image(systemName: "xmark.circle.fill")
                            .font(.system(size: 32))
                            .foregroundColor(.white)
                            .shadow(radius: 6)
                    }
                    
                    Spacer()
                    
                    // Live Guidance Pill
                    HStack(spacing: 8) {
                        Circle()
                            .fill(viewModel.isFacingDestination ? Color.green : Color.primaryGold)
                            .frame(width: 10, height: 10)
                        
                        Text(viewModel.guidanceText)
                            .font(.subheadline)
                            .fontWeight(.semibold)
                            .foregroundColor(.white)
                    }
                    .padding(.horizontal, 14)
                    .padding(.vertical, 8)
                    .background(Color.black.opacity(0.65))
                    .clipShape(Capsule())
                    .overlay(
                        Capsule().stroke(viewModel.isFacingDestination ? Color.green : Color.primaryGold.opacity(0.6), lineWidth: 1.5)
                    )
                }
                .padding(.horizontal, 20)
                .padding(.top, 50)
                
                Spacer()
                
                // Central HUD Directional Compass Arrow
                VStack(spacing: 8) {
                    ZStack {
                        // Outer Ring
                        Circle()
                            .stroke(Color.primaryGold.opacity(0.3), lineWidth: 3)
                            .frame(width: 90, height: 90)
                        
                        if viewModel.isFacingDestination {
                            Circle()
                                .stroke(Color.primaryGold, lineWidth: 2)
                                .frame(width: 106, height: 106)
                                .scaleEffect(1.05)
                        }
                        
                        // Rotating Navigation Pointer Arrow
                        Image(systemName: "location.north.fill")
                            .font(.system(size: 42))
                            .foregroundColor(viewModel.isFacingDestination ? .green : .primaryGold)
                            .rotationEffect(.degrees(viewModel.relativeBearing))
                            .animation(.interactiveSpring(response: 0.35, dampingFraction: 0.7), value: viewModel.relativeBearing)
                    }
                    
                    Text("\(Int(viewModel.targetBearing))° AZIMUTH")
                        .font(.caption2)
                        .fontWeight(.bold)
                        .tracking(1.5)
                        .foregroundColor(.primaryGold)
                }
                .padding(.bottom, 20)
                
                Spacer()
                
                // Bottom Destination & Turn-by-Turn Info Card
                VStack(alignment: .leading, spacing: 10) {
                    HStack {
                        VStack(alignment: .leading, spacing: 4) {
                            Text(destination.name)
                                .font(.title3)
                                .fontWeight(.bold)
                                .foregroundColor(.white)
                            
                            HStack(spacing: 12) {
                                Label("\(Int(viewModel.distanceToDestination))m", systemImage: "figure.walk")
                                    .font(.subheadline)
                                    .foregroundColor(.primaryGold)
                                
                                Label("~\(max(1, Int(viewModel.distanceToDestination / 80))) min", systemImage: "clock")
                                    .font(.subheadline)
                                    .foregroundColor(.white.opacity(0.8))
                            }
                        }
                        
                        Spacer()
                        
                        // Compass Bearing Pill
                        VStack {
                            Text("\(abs(Int(viewModel.relativeBearing)))°")
                                .font(.title2)
                                .fontWeight(.bold)
                                .foregroundColor(.primaryGold)
                            Text(viewModel.relativeBearing >= 0 ? "RIGHT" : "LEFT")
                                .font(.caption2)
                                .fontWeight(.semibold)
                                .foregroundColor(.white.opacity(0.7))
                        }
                        .padding(.horizontal, 12)
                        .padding(.vertical, 8)
                        .background(Color.white.opacity(0.12))
                        .cornerRadius(12)
                    }
                }
                .padding(20)
                .background(
                    RoundedRectangle(cornerRadius: 20)
                        .fill(Color.black.opacity(0.75))
                        .overlay(
                            RoundedRectangle(cornerRadius: 20)
                                .stroke(Color.primaryGold.opacity(0.4), lineWidth: 1)
                        )
                )
                .padding(.horizontal, 20)
                .padding(.bottom, 40)
            }
        }
    }
}

struct ARViewContainer: UIViewRepresentable {
    @ObservedObject var viewModel: ARViewModel
    
    func makeUIView(context: Context) -> ARView {
        let arView = ARView(frame: .zero)
        
        let config = ARWorldTrackingConfiguration()
        config.planeDetection = [.horizontal]
        config.worldAlignment = .gravityAndHeading
        
        arView.session.run(config)
        viewModel.setupARScene(arView: arView)
        
        return arView
    }
    
    func updateUIView(_ uiView: ARView, context: Context) {
        // Continuous updates handled via ViewModel combine pipeline
    }
}

