import Foundation

struct ItineraryStop: Identifiable, Codable, Equatable {
    var id: String { "\(orderIndex)-\(title)" }
    let poiId: String?
    let title: String
    let region: String
    let orderIndex: Int
    let recommendedDurationMinutes: Int
    let notes: String
}

struct ItineraryDay: Identifiable, Codable, Equatable {
    var id: Int { dayNumber }
    let dayNumber: Int
    let theme: String
    let region: String
    let stops: [ItineraryStop]
}

struct Itinerary: Identifiable, Codable, Equatable {
    let id: String
    let title: String
    let durationDays: Int
    let regions: [String]
    let days: [ItineraryDay]
    let createdAt: String
}
