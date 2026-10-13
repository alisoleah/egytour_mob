import Foundation
import Combine

class ItineraryViewModel: ObservableObject {
    @Published var itineraries: [Itinerary] = []
    @Published var isLoading = false
    @Published var isGenerating = false
    
    private let apiEndpoint = "http://localhost:3000/api/v1/itineraries/generate"
    
    init() {
        loadItineraries()
    }
    
    func loadItineraries() {
        if itineraries.isEmpty {
            // Seed initial sample Classical Itinerary
            let sample = generateFallbackItinerary(days: 3, regions: ["cairo", "luxor"])
            self.itineraries = [sample]
        }
    }
    
    func generateItinerary(
        durationDays: Int,
        regions: [String],
        interests: [String] = ["ancient_history", "architecture"],
        completion: @escaping (Bool) -> Void
    ) {
        self.isGenerating = true
        
        guard let url = URL(string: apiEndpoint) else {
            let fallback = generateFallbackItinerary(days: durationDays, regions: regions)
            self.itineraries.insert(fallback, at: 0)
            self.isGenerating = false
            completion(true)
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.timeoutInterval = 10
        
        let body: [String: Any] = [
            "durationDays": durationDays,
            "regions": regions,
            "interests": interests
        ]
        
        do {
            request.httpBody = try JSONSerialization.data(withJSONObject: body)
        } catch {
            let fallback = generateFallbackItinerary(days: durationDays, regions: regions)
            self.itineraries.insert(fallback, at: 0)
            self.isGenerating = false
            completion(true)
            return
        }
        
        URLSession.shared.dataTask(with: request) { [weak self] data, response, error in
            DispatchQueue.main.async {
                guard let self = self else { return }
                self.isGenerating = false
                
                if let data = data,
                   let json = try? JSONSerialization.jsonObject(with: data) as? [String: Any],
                   let itinObj = json["itinerary"] as? [String: Any],
                   let title = itinObj["title"] as? String {
                    
                    let daysCount = itinObj["durationDays"] as? Int ?? durationDays
                    let parsedRegions = itinObj["regions"] as? [String] ?? regions
                    
                    var parsedDays: [ItineraryDay] = []
                    if let rawDays = itinObj["days"] as? [[String: Any]] {
                        for d in rawDays {
                            let dayNum = d["dayNumber"] as? Int ?? 1
                            let theme = d["theme"] as? String ?? "Day \(dayNum)"
                            let reg = d["region"] as? String ?? "cairo"
                            
                            var stops: [ItineraryStop] = []
                            if let rawStops = d["stops"] as? [[String: Any]] {
                                for (idx, s) in rawStops.enumerated() {
                                    let sTitle = s["title"] as? String ?? "Stop"
                                    let sReg = s["region"] as? String ?? reg
                                    let duration = s["recommendedDurationMinutes"] as? Int ?? 90
                                    let notes = s["notes"] as? String ?? ""
                                    let poi = s["poiId"] as? String
                                    
                                    stops.append(ItineraryStop(
                                        poiId: poi,
                                        title: sTitle,
                                        region: sReg,
                                        orderIndex: idx,
                                        recommendedDurationMinutes: duration,
                                        notes: notes
                                    ))
                                }
                            }
                            
                            parsedDays.append(ItineraryDay(dayNumber: dayNum, theme: theme, region: reg, stops: stops))
                        }
                    }
                    
                    let newItin = Itinerary(
                        id: "itin-\(UUID().uuidString.prefix(6))",
                        title: title,
                        durationDays: daysCount,
                        regions: parsedRegions,
                        days: parsedDays,
                        createdAt: Date().description
                    )
                    
                    self.itineraries.insert(newItin, at: 0)
                    completion(true)
                } else {
                    let fallback = self.generateFallbackItinerary(days: durationDays, regions: regions)
                    self.itineraries.insert(fallback, at: 0)
                    completion(true)
                }
            }
        }.resume()
    }
    
    private func generateFallbackItinerary(days: Int, regions: [String]) -> Itinerary {
        var dayList: [ItineraryDay] = []
        
        // Day 1: Giza & Pyramids
        dayList.append(ItineraryDay(
            dayNumber: 1,
            theme: "Giza Plateau: Pyramids & The Sphinx",
            region: "cairo",
            stops: [
                ItineraryStop(
                    poiId: "giza-1",
                    title: "Great Pyramid of Khufu",
                    region: "cairo",
                    orderIndex: 0,
                    recommendedDurationMinutes: 150,
                    notes: "Morning exploration of the sole surviving Ancient Wonder of the World."
                ),
                ItineraryStop(
                    poiId: "giza-2",
                    title: "The Great Sphinx & Valley Temple",
                    region: "cairo",
                    orderIndex: 1,
                    recommendedDurationMinutes: 90,
                    notes: "View the 73-meter limestone guardian and Thutmose IV's Dream Stele."
                ),
                ItineraryStop(
                    poiId: nil,
                    title: "Grand Egyptian Museum (GEM)",
                    region: "cairo",
                    orderIndex: 2,
                    recommendedDurationMinutes: 180,
                    notes: "Marvel at the full Tutankhamun collection and the Grand Staircase."
                )
            ]
        ))
        
        // Day 2: Historic Cairo & Saqqara
        if days >= 2 {
            dayList.append(ItineraryDay(
                dayNumber: 2,
                theme: "Saqqara Step Pyramid & Medieval Cairo",
                region: "cairo",
                stops: [
                    ItineraryStop(
                        poiId: nil,
                        title: "Djoser Step Pyramid at Saqqara",
                        region: "cairo",
                        orderIndex: 0,
                        recommendedDurationMinutes: 120,
                        notes: "Visit the world's oldest monumental stone building designed by Imhotep."
                    ),
                    ItineraryStop(
                        poiId: nil,
                        title: "Khan el-Khalili Bazaar",
                        region: "cairo",
                        orderIndex: 1,
                        recommendedDurationMinutes: 120,
                        notes: "Wander the 14th-century souk and historic spice markets."
                    )
                ]
            ))
        }
        
        // Day 3: Luxor East Bank
        if days >= 3 {
            dayList.append(ItineraryDay(
                dayNumber: 3,
                theme: "Thebes East Bank: Temples of Karnak & Luxor",
                region: "luxor",
                stops: [
                    ItineraryStop(
                        poiId: nil,
                        title: "Karnak Temple Complex",
                        region: "luxor",
                        orderIndex: 0,
                        recommendedDurationMinutes: 180,
                        notes: "Walk among 134 towering papyrus columns in the largest religious complex ever built."
                    ),
                    ItineraryStop(
                        poiId: "luxor-1",
                        title: "Luxor Temple by Sunset",
                        region: "luxor",
                        orderIndex: 1,
                        recommendedDurationMinutes: 120,
                        notes: "Witness illuminated statues of Ramesses II and the Avenue of Sphinxes."
                    )
                ]
            ))
        }
        
        return Itinerary(
            id: "itin-\(UUID().uuidString.prefix(6))",
            title: "\(days)-Day Classical Egyptian Odyssey",
            durationDays: days,
            regions: regions.isEmpty ? ["cairo", "luxor"] : regions,
            days: dayList,
            createdAt: Date().description
        )
    }
}
