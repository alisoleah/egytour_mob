import ARKit
import RealityKit
import CoreLocation
import Combine
import SwiftUI

class ARViewModel: NSObject, ObservableObject {
    @Published var distanceToDestination: Double = 0
    @Published var relativeBearing: Double = 0 // -180 to 180 degrees relative to phone
    @Published var targetBearing: Double = 0   // 0 to 360 degrees absolute
    @Published var guidanceText: String = "Locating destination..."
    @Published var isFacingDestination: Bool = false
    @Published var isARActive = true
    
    let destination: POI
    private var arView: ARView?
    private var directionAnchor: AnchorEntity?
    private var arrowEntity: ModelEntity?
    private let locationService = LocationService.shared
    private var cancellables = Set<AnyCancellable>()
    
    init(destination: POI) {
        self.destination = destination
        super.init()
        
        setupSubscriptions()
    }
    
    private func setupSubscriptions() {
        // Location updates
        locationService.$currentLocation
            .receive(on: DispatchQueue.main)
            .sink { [weak self] location in
                self?.updateDistance(currentLocation: location)
                self?.updateCalculations()
            }
            .store(in: &cancellables)
            
        // Heading updates
        locationService.$currentHeading
            .receive(on: DispatchQueue.main)
            .sink { [weak self] _ in
                self?.updateCalculations()
            }
            .store(in: &cancellables)
            
        // Start location tracking
        locationService.startTracking()
    }
    
    func setupARScene(arView: ARView) {
        self.arView = arView
        
        // Create direction arrow entity
        let arrow = createDirectionArrow()
        self.arrowEntity = arrow
        
        // Anchor at 1.8 meters in front, slightly lowered
        let anchor = AnchorEntity(world: [0, -0.2, -1.8])
        anchor.addChild(arrow)
        
        arView.scene.addAnchor(anchor)
        self.directionAnchor = anchor
        
        updateCalculations()
    }
    
    private func updateDistance(currentLocation: CLLocation?) {
        guard let userLocation = currentLocation else {
            // Default simulated location near Giza if not available
            let simLocation = CLLocation(latitude: 29.9740, longitude: 31.1300)
            let destLocation = CLLocation(
                latitude: destination.coordinates.latitude,
                longitude: destination.coordinates.longitude
            )
            distanceToDestination = simLocation.distance(from: destLocation)
            return
        }
        
        let destLocation = CLLocation(
            latitude: destination.coordinates.latitude,
            longitude: destination.coordinates.longitude
        )
        distanceToDestination = userLocation.distance(from: destLocation)
    }
    
    private func updateCalculations() {
        let userCoord = locationService.currentLocation?.coordinate ?? CLLocationCoordinate2D(latitude: 29.9740, longitude: 31.1300)
        let destCoord = CLLocationCoordinate2D(
            latitude: destination.coordinates.latitude,
            longitude: destination.coordinates.longitude
        )
        
        // 1. Calculate absolute bearing to destination (0 - 360)
        let bearing = calculateBearing(from: userCoord, to: destCoord)
        targetBearing = bearing
        
        // 2. Calculate device heading
        let deviceHeading = locationService.currentHeading?.trueHeading ?? locationService.currentHeading?.magneticHeading ?? 0
        
        // 3. Calculate relative bearing (-180 to 180)
        var rel = bearing - deviceHeading
        while rel > 180 { rel -= 360 }
        while rel < -180 { rel += 360 }
        relativeBearing = rel
        
        // 4. Update guidance state
        isFacingDestination = abs(rel) <= 15
        if isFacingDestination {
            guidanceText = "Directly Ahead"
        } else if rel > 0 {
            guidanceText = "Turn \(Int(rel))° Right"
        } else {
            guidanceText = "Turn \(Int(-rel))° Left"
        }
        
        // 5. Rotate RealityKit 3D arrow towards target
        let rad = Float(rel.degreesToRadians)
        // Rotate around Y-axis to point toward destination
        directionAnchor?.transform.rotation = simd_quatf(angle: -rad, axis: [0, 1, 0])
    }
    
    private func createDirectionArrow() -> ModelEntity {
        // Create golden arrow shaft
        let shaftMesh = MeshResource.generateBox(size: [0.08, 0.08, 0.4])
        var material = SimpleMaterial()
        material.color = .init(tint: UIColor(Color.primaryGold), texture: nil)
        material.metallic = .init(floatLiteral: 0.9)
        material.roughness = .init(floatLiteral: 0.1)
        
        let shaft = ModelEntity(mesh: shaftMesh, materials: [material])
        
        // Arrow head (pointed pyramid-like box tip)
        let tipMesh = MeshResource.generateBox(size: [0.18, 0.04, 0.18])
        let tip = ModelEntity(mesh: tipMesh, materials: [material])
        tip.position = [0, 0, -0.2]
        shaft.addChild(tip)
        
        return shaft
    }
    
    private func calculateBearing(from: CLLocationCoordinate2D, to: CLLocationCoordinate2D) -> Double {
        let lat1 = from.latitude.degreesToRadians
        let lon1 = from.longitude.degreesToRadians
        let lat2 = to.latitude.degreesToRadians
        let lon2 = to.longitude.degreesToRadians
        
        let dLon = lon2 - lon1
        let y = sin(dLon) * cos(lat2)
        let x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)
        let bearing = atan2(y, x).radiansToDegrees
        
        return (bearing + 360).truncatingRemainder(dividingBy: 360)
    }
    
    func toggleARMode() {
        isARActive.toggle()
    }
}

extension Double {
    var degreesToRadians: Double { self * .pi / 180 }
    var radiansToDegrees: Double { self * 180 / .pi }
}

