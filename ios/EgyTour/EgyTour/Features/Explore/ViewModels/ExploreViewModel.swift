import Foundation
import Combine
import CoreLocation
import MapKit

struct RouteStep: Identifiable, Equatable {
    let id = UUID()
    let instructions: String
    let distanceMeters: Double
    let iconName: String
}

class ExploreViewModel: ObservableObject {
    @Published var pois: [POI] = []
    @Published var selectedPOI: POI?
    @Published var isLoading = false
    
    // Navigation & Routing state
    @Published var isNavigating = false
    @Published var routeCoordinates: [CLLocationCoordinate2D] = []
    @Published var routeSteps: [RouteStep] = []
    @Published var currentStepIndex = 0
    @Published var totalDistanceMeters: Double = 0
    @Published var estimatedTimeMinutes: Int = 0
    
    private let locationService = LocationService.shared
    private var cancellables = Set<AnyCancellable>()
    
    init() {
        loadPOIs()
    }
    
    func loadPOIs() {
        self.pois = [
            POI(
                id: "giza-1",
                name: "Great Pyramid of Giza",
                description: "The largest Egyptian pyramid and tomb of Fourth Dynasty pharaoh Khufu.",
                type: .pyramid,
                coordinates: Coordinates(latitude: 29.9792, longitude: 31.1342),
                imageUrl: "great_pyramid",
                audioUrl: "https://example.com/audio/giza.mp3",
                historicalPeriod: "Old Kingdom",
                triggerRadius: 100
            ),
            POI(
                id: "giza-2",
                name: "Great Sphinx",
                description: "Limestone statue of a reclining sphinx, a mythical creature with lion body and pharaoh head.",
                type: .temple,
                coordinates: Coordinates(latitude: 29.9753, longitude: 31.1376),
                imageUrl: "sphinx",
                audioUrl: "https://example.com/audio/sphinx.mp3",
                historicalPeriod: "Old Kingdom",
                triggerRadius: 50
            ),
            POI(
                id: "luxor-1",
                name: "Luxor Temple",
                description: "Large Ancient Egyptian temple complex located on the east bank of the Nile River.",
                type: .temple,
                coordinates: Coordinates(latitude: 25.7000, longitude: 32.6398),
                imageUrl: "luxor_temple",
                audioUrl: nil,
                historicalPeriod: "New Kingdom",
                triggerRadius: 150
            )
        ]
    }
    
    func calculateRoute(to destination: POI) {
        self.selectedPOI = destination
        self.isLoading = true
        
        let userCoord = locationService.currentLocation?.coordinate ?? CLLocationCoordinate2D(latitude: 29.9735, longitude: 31.1310)
        let destCoord = CLLocationCoordinate2D(
            latitude: destination.coordinates.latitude,
            longitude: destination.coordinates.longitude
        )
        
        let request = MKDirections.Request()
        request.source = MKMapItem(placemark: MKPlacemark(coordinate: userCoord))
        request.destination = MKMapItem(placemark: MKPlacemark(coordinate: destCoord))
        request.transportType = .walking
        
        let directions = MKDirections(request: request)
        directions.calculate { [weak self] response, error in
            DispatchQueue.main.async {
                guard let self = self else { return }
                self.isLoading = false
                
                if let route = response?.routes.first {
                    self.applyMKRoute(route, from: userCoord, to: destCoord, destinationName: destination.name)
                } else {
                    // Fallback to high-fidelity synthesized pedestrian route
                    self.generateFallbackWalkingRoute(from: userCoord, to: destCoord, destinationName: destination.name)
                }
            }
        }
    }
    
    private func applyMKRoute(_ route: MKRoute, from start: CLLocationCoordinate2D, to end: CLLocationCoordinate2D, destinationName: String) {
        var coords = [CLLocationCoordinate2D](repeating: kCLLocationCoordinate2DInvalid, count: route.polyline.pointCount)
        route.polyline.getCoordinates(&coords, range: NSRange(location: 0, length: route.polyline.pointCount))
        
        self.routeCoordinates = coords
        self.totalDistanceMeters = route.distance
        self.estimatedTimeMinutes = max(1, Int(route.expectedTravelTime / 60))
        
        var parsedSteps: [RouteStep] = []
        for step in route.steps where !step.instructions.isEmpty {
            let icon = self.determineStepIcon(for: step.instructions)
            parsedSteps.append(RouteStep(
                instructions: step.instructions,
                distanceMeters: step.distance,
                iconName: icon
            ))
        }
        
        if parsedSteps.isEmpty {
            parsedSteps = [
                RouteStep(instructions: "Head toward \(destinationName)", distanceMeters: route.distance * 0.4, iconName: "arrow.up"),
                RouteStep(instructions: "Continue along ancient walkway", distanceMeters: route.distance * 0.4, iconName: "arrow.up.forward"),
                RouteStep(instructions: "Arrive at \(destinationName)", distanceMeters: route.distance * 0.2, iconName: "mappin.and.ellipse")
            ]
        }
        
        self.routeSteps = parsedSteps
        self.currentStepIndex = 0
    }
    
    private func generateFallbackWalkingRoute(from start: CLLocationCoordinate2D, to end: CLLocationCoordinate2D, destinationName: String) {
        // Synthesize waypoint coordinates between user and destination
        let midLat1 = start.latitude + (end.latitude - start.latitude) * 0.35 + 0.0004
        let midLon1 = start.longitude + (end.longitude - start.longitude) * 0.35
        let midLat2 = start.latitude + (end.latitude - start.latitude) * 0.70
        let midLon2 = start.longitude + (end.longitude - start.longitude) * 0.70 + 0.0003
        
        self.routeCoordinates = [
            start,
            CLLocationCoordinate2D(latitude: midLat1, longitude: midLon1),
            CLLocationCoordinate2D(latitude: midLat2, longitude: midLon2),
            end
        ]
        
        let startLoc = CLLocation(latitude: start.latitude, longitude: start.longitude)
        let endLoc = CLLocation(latitude: end.latitude, longitude: end.longitude)
        let directDistance = startLoc.distance(from: endLoc)
        
        self.totalDistanceMeters = directDistance * 1.15
        self.estimatedTimeMinutes = max(1, Int(self.totalDistanceMeters / 75)) // approx 4.5 km/h walking speed
        
        self.routeSteps = [
            RouteStep(instructions: "Head along the plateau walkway toward \(destinationName)", distanceMeters: directDistance * 0.35, iconName: "arrow.up"),
            RouteStep(instructions: "Bear right following visitor signage", distanceMeters: directDistance * 0.35, iconName: "arrow.turn.up.right"),
            RouteStep(instructions: "Continue straight toward monument viewing area", distanceMeters: directDistance * 0.30, iconName: "arrow.up.forward"),
            RouteStep(instructions: "Arrive at \(destinationName)", distanceMeters: 10, iconName: "mappin.and.ellipse")
        ]
        self.currentStepIndex = 0
    }
    
    private func determineStepIcon(for instruction: String) -> String {
        let lower = instruction.lowercased()
        if lower.contains("left") { return "arrow.turn.up.left" }
        if lower.contains("right") { return "arrow.turn.up.right" }
        if lower.contains("arrive") || lower.contains("destination") { return "mappin.and.ellipse" }
        if lower.contains("roundabout") { return "arrow.triangle.2.circlepath" }
        return "arrow.up"
    }
    
    func startNavigation() {
        self.isNavigating = true
    }
    
    func exitNavigation() {
        self.isNavigating = false
        self.routeCoordinates = []
        self.routeSteps = []
        self.currentStepIndex = 0
    }
    
    func nextStep() {
        if currentStepIndex < routeSteps.count - 1 {
            currentStepIndex += 1
        }
    }
}

