import Foundation
import CoreLocation

struct POI: Identifiable, Codable {
    let id: String
    let name: String
    let description: String?
    let type: POIType
    let coordinates: Coordinates
    let imageUrl: String?
    let audioUrl: String?
    let historicalPeriod: String?
    let triggerRadius: Double // in meters
    
    enum POIType: String, Codable {
        case pyramid, temple, museum, market, modern
    }
}

struct Coordinates: Codable {
    let latitude: Double
    let longitude: Double
}
