import SwiftUI

struct ItineraryGeneratorView: View {
    @Environment(\.dismiss) var dismiss
    @ObservedObject var viewModel: ItineraryViewModel
    
    @State private var durationDays = 3
    @State private var selectedRegions: Set<String> = ["cairo", "luxor"]
    @State private var selectedInterests: Set<String> = ["ancient_history", "architecture"]
    
    let availableRegions = [
        ("cairo", "Cairo & Pyramids"),
        ("luxor", "Luxor (Thebes)"),
        ("aswan", "Aswan & Nubia"),
        ("alexandria", "Alexandria & Med")
    ]
    
    let availableInterests = [
        ("ancient_history", "Ancient History"),
        ("architecture", "Architecture"),
        ("archaeology", "Archaeological Digs"),
        ("art", "Pharaonic Art")
    ]
    
    var body: some View {
        NavigationView {
            Form {
                // Duration Section
                Section(header: Text("Trip Duration")) {
                    Stepper(value: $durationDays, in: 1...14) {
                        HStack {
                            Image(systemName: "calendar")
                                .foregroundColor(.primaryGold)
                            Text("\(durationDays) Days")
                                .fontWeight(.semibold)
                        }
                    }
                }
                
                // Regions Section
                Section(header: Text("Regions to Explore")) {
                    ForEach(availableRegions, id: \.0) { id, label in
                        HStack {
                            Text(label)
                            Spacer()
                            if selectedRegions.contains(id) {
                                Image(systemName: "checkmark")
                                    .foregroundColor(.primaryGold)
                            }
                        }
                        .contentShape(Rectangle())
                        .onTapGesture {
                            if selectedRegions.contains(id) {
                                if selectedRegions.count > 1 {
                                    selectedRegions.remove(id)
                                }
                            } else {
                                selectedRegions.insert(id)
                            }
                        }
                    }
                }
                
                // Interests Section
                Section(header: Text("Your Historical Interests")) {
                    ForEach(availableInterests, id: \.0) { id, label in
                        HStack {
                            Text(label)
                            Spacer()
                            if selectedInterests.contains(id) {
                                Image(systemName: "checkmark")
                                    .foregroundColor(.primaryGold)
                            }
                        }
                        .contentShape(Rectangle())
                        .onTapGesture {
                            if selectedInterests.contains(id) {
                                selectedInterests.remove(id)
                            } else {
                                selectedInterests.insert(id)
                            }
                        }
                    }
                }
                
                // Generate Button
                Section {
                    Button(action: {
                        viewModel.generateItinerary(
                            durationDays: durationDays,
                            regions: Array(selectedRegions),
                            interests: Array(selectedInterests)
                        ) { success in
                            if success {
                                dismiss()
                            }
                        }
                    }) {
                        HStack {
                            Spacer()
                            if viewModel.isGenerating {
                                ProgressView()
                                    .progressViewStyle(CircularProgressViewStyle(tint: .white))
                                Spacer().frame(width: 10)
                                Text("Crafting Itinerary...")
                            } else {
                                Image(systemName: "sparkles")
                                Text("Generate with Egyptologist AI")
                            }
                            Spacer()
                        }
                        .font(.headline)
                        .foregroundColor(.white)
                        .padding(.vertical, 8)
                    }
                    .listRowBackground(Color.primaryGold)
                    .disabled(viewModel.isGenerating || selectedRegions.isEmpty)
                }
            }
            .navigationTitle("Plan Your Journey")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Cancel") {
                        dismiss()
                    }
                }
            }
        }
    }
}
