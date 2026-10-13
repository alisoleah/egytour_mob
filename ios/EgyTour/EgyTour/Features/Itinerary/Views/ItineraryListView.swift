import SwiftUI

struct ItineraryListView: View {
    @StateObject private var viewModel = ItineraryViewModel()
    @State private var showingGenerator = false
    
    var body: some View {
        NavigationView {
            ZStack {
                Color(.systemGroupedBackground).edgesIgnoringSafeArea(.all)
                
                if viewModel.itineraries.isEmpty {
                    VStack(spacing: 18) {
                        Image(systemName: "map.fill")
                            .font(.system(size: 64))
                            .foregroundColor(.primaryGold)
                        
                        Text("No Trip Plans Yet")
                            .font(.title2)
                            .fontWeight(.bold)
                        
                        Text("Let our AI Egyptologist design an optimal, custom travel route for your stay in Egypt.")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                            .multilineTextAlignment(.center)
                            .padding(.horizontal, 32)
                        
                        Button(action: { showingGenerator = true }) {
                            HStack {
                                Image(systemName: "sparkles")
                                Text("Generate Itinerary")
                            }
                            .font(.headline)
                            .foregroundColor(.white)
                            .padding(.horizontal, 24)
                            .padding(.vertical, 14)
                            .background(Color.primaryGold)
                            .cornerRadius(12)
                        }
                        .padding(.top, 8)
                    }
                    .padding()
                } else {
                    ScrollView {
                        LazyVStack(spacing: 16) {
                            ForEach(viewModel.itineraries) { itinerary in
                                NavigationLink(destination: ItineraryDetailView(itinerary: itinerary)) {
                                    ItineraryCardView(itinerary: itinerary)
                                }
                                .buttonStyle(PlainButtonStyle())
                            }
                        }
                        .padding(16)
                    }
                }
            }
            .navigationTitle("Trip Plans")
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: { showingGenerator = true }) {
                        Image(systemName: "plus.circle.fill")
                            .font(.title3)
                            .foregroundColor(.primaryGold)
                    }
                }
            }
            .sheet(isPresented: $showingGenerator) {
                ItineraryGeneratorView(viewModel: viewModel)
            }
        }
        .onAppear {
            viewModel.loadItineraries()
        }
    }
}

struct ItineraryCardView: View {
    let itinerary: Itinerary
    
    var body: some View {
        VStack(alignment: .leading, spacing: 14) {
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    Text(itinerary.title)
                        .font(.headline)
                        .fontWeight(.bold)
                        .foregroundColor(.primary)
                    
                    HStack(spacing: 12) {
                        Label("\(itinerary.durationDays) Days", systemImage: "calendar")
                            .font(.caption)
                            .foregroundColor(.primaryGold)
                        
                        Label("\(itinerary.days.flatMap { $0.stops }.count) Stops", systemImage: "mappin.and.ellipse")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                }
                
                Spacer()
                
                Image(systemName: "chevron.right")
                    .foregroundColor(.secondary)
            }
            
            Divider().opacity(0.6)
            
            // Regions Tags
            HStack(spacing: 8) {
                ForEach(itinerary.regions, id: \.self) { region in
                    Text(region.capitalized)
                        .font(.caption2)
                        .fontWeight(.semibold)
                        .padding(.horizontal, 8)
                        .padding(.vertical, 4)
                        .background(Color.primaryGold.opacity(0.15))
                        .foregroundColor(.primaryGold)
                        .cornerRadius(6)
                }
                
                Spacer()
                
                Text("AI Optimized")
                    .font(.caption2)
                    .foregroundColor(.secondary)
            }
        }
        .padding(16)
        .background(Color(.secondarySystemBackground))
        .cornerRadius(16)
        .shadow(color: .black.opacity(0.08), radius: 6, y: 2)
    }
}
