import SwiftUI

struct ItineraryDetailView: View {
    let itinerary: Itinerary
    @State private var selectedDayNumber = 1
    
    var currentDay: ItineraryDay? {
        itinerary.days.first { $0.dayNumber == selectedDayNumber } ?? itinerary.days.first
    }
    
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                // Trip Header Card
                VStack(alignment: .leading, spacing: 10) {
                    Text(itinerary.title)
                        .font(.title2)
                        .fontWeight(.bold)
                        .foregroundColor(.primaryGold)
                    
                    HStack(spacing: 12) {
                        Label("\(itinerary.durationDays) Days", systemImage: "calendar")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                        
                        Label("\(itinerary.days.flatMap { $0.stops }.count) Total Stops", systemImage: "mappin.and.ellipse")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                    }
                    
                    // Region Tags
                    HStack(spacing: 8) {
                        ForEach(itinerary.regions, id: \.self) { region in
                            Text(region.uppercased())
                                .font(.caption2)
                                .fontWeight(.bold)
                                .padding(.horizontal, 10)
                                .padding(.vertical, 4)
                                .background(Color.primaryGold.opacity(0.18))
                                .foregroundColor(.primaryGold)
                                .cornerRadius(6)
                        }
                    }
                }
                .padding(20)
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(Color(.secondarySystemBackground))
                .cornerRadius(18)
                .padding(.horizontal, 16)
                
                // Day Selector Tabs
                if itinerary.days.count > 1 {
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 10) {
                            ForEach(itinerary.days) { day in
                                Button(action: {
                                    withAnimation {
                                        selectedDayNumber = day.dayNumber
                                    }
                                }) {
                                    VStack(spacing: 4) {
                                        Text("DAY \(day.dayNumber)")
                                            .font(.caption)
                                            .fontWeight(.bold)
                                        Text(day.region.capitalized)
                                            .font(.caption2)
                                    }
                                    .padding(.horizontal, 16)
                                    .padding(.vertical, 10)
                                    .background(selectedDayNumber == day.dayNumber ? Color.primaryGold : Color(.secondarySystemBackground))
                                    .foregroundColor(selectedDayNumber == day.dayNumber ? .white : .primary)
                                    .cornerRadius(12)
                                }
                            }
                        }
                        .padding(.horizontal, 16)
                    }
                }
                
                // Current Day Schedule
                if let day = currentDay {
                    VStack(alignment: .leading, spacing: 16) {
                        // Day Theme Header
                        HStack {
                            VStack(alignment: .leading, spacing: 4) {
                                Text("Day \(day.dayNumber) Theme")
                                    .font(.caption)
                                    .fontWeight(.bold)
                                    .foregroundColor(.primaryGold)
                                Text(day.theme)
                                    .font(.headline)
                                    .fontWeight(.bold)
                            }
                            Spacer()
                        }
                        .padding(.horizontal, 16)
                        
                        // Vertical Timeline of Stops
                        VStack(spacing: 0) {
                            ForEach(Array(day.stops.enumerated()), id: \.element.id) { index, stop in
                                TimelineStopRow(
                                    stop: stop,
                                    index: index + 1,
                                    isLast: index == day.stops.count - 1
                                )
                            }
                        }
                        .padding(.horizontal, 16)
                    }
                }
            }
            .padding(.vertical, 16)
        }
        .navigationTitle("Trip Itinerary")
        .navigationBarTitleDisplayMode(.inline)
    }
}

// MARK: - Timeline Stop Row
struct TimelineStopRow: View {
    let stop: ItineraryStop
    let index: Int
    let isLast: Bool
    
    var body: some View {
        HStack(alignment: .top, spacing: 16) {
            // Timeline Node Column
            VStack(spacing: 0) {
                ZStack {
                    Circle()
                        .fill(Color.primaryGold)
                        .frame(width: 32, height: 32)
                    
                    Text("\(index)")
                        .font(.caption)
                        .fontWeight(.bold)
                        .foregroundColor(.white)
                }
                
                if !isLast {
                    Rectangle()
                        .fill(Color.primaryGold.opacity(0.35))
                        .frame(width: 2)
                        .frame(minHeight: 80)
                }
            }
            
            // Stop Card
            VStack(alignment: .leading, spacing: 8) {
                HStack {
                    Text(stop.title)
                        .font(.headline)
                        .fontWeight(.bold)
                    
                    Spacer()
                    
                    Label("\(stop.recommendedDurationMinutes)m", systemImage: "clock")
                        .font(.caption)
                        .foregroundColor(.primaryGold)
                        .padding(.horizontal, 8)
                        .padding(.vertical, 3)
                        .background(Color.primaryGold.opacity(0.12))
                        .cornerRadius(6)
                }
                
                if !stop.notes.isEmpty {
                    Text(stop.notes)
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                        .lineLimit(3)
                }
            }
            .padding(14)
            .background(Color(.secondarySystemBackground))
            .cornerRadius(14)
            .padding(.bottom, 16)
        }
    }
}
