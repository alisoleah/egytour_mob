import SwiftUI
import MapKit

struct ExploreMapView: View {
    @StateObject private var viewModel = ExploreViewModel()
    @State private var isARPresented = false
    @State private var isMonumentDetailPresented = false
    @State private var isCheckoutPresented = false
    @State private var isSuccessPresented = false
    
    var body: some View {
        ZStack {
            // Native Map with Polyline & Annotation Support
            NativeMapView(
                pois: viewModel.pois,
                selectedPOI: viewModel.selectedPOI,
                routeCoordinates: viewModel.routeCoordinates,
                onSelectPOI: { poi in
                    withAnimation {
                        viewModel.selectedPOI = poi
                    }
                }
            )
            .edgesIgnoringSafeArea(.all)
            
            // Top Turn-by-Turn Guidance Banner (When navigating)
            VStack {
                if viewModel.isNavigating, !viewModel.routeSteps.isEmpty {
                    TurnByTurnBannerView(
                        currentStep: viewModel.routeSteps[viewModel.currentStepIndex],
                        stepIndex: viewModel.currentStepIndex + 1,
                        totalSteps: viewModel.routeSteps.count,
                        totalDistanceMeters: viewModel.totalDistanceMeters,
                        etaMinutes: viewModel.estimatedTimeMinutes,
                        onNextStep: { viewModel.nextStep() },
                        onExit: { viewModel.exitNavigation() }
                    )
                    .transition(.move(edge: .top).combined(with: .opacity))
                    .padding(.top, 45)
                    .padding(.horizontal, 16)
                }
                
                Spacer()
                
                // Bottom POI Details & Routing Card
                if let poi = viewModel.selectedPOI, !viewModel.isNavigating {
                    POIRouteCardView(
                        poi: poi,
                        isLoading: viewModel.isLoading,
                        hasRoute: !viewModel.routeCoordinates.isEmpty,
                        distanceMeters: viewModel.totalDistanceMeters,
                        etaMinutes: viewModel.estimatedTimeMinutes,
                        onDismiss: {
                            viewModel.selectedPOI = nil
                            viewModel.exitNavigation()
                        },
                        onCalculateRoute: {
                            viewModel.calculateRoute(to: poi)
                        },
                        onStartNavigation: {
                            withAnimation {
                                viewModel.startNavigation()
                            }
                        },
                        onExploreAR: {
                            isARPresented = true
                        },
                        onViewDetails: {
                            isMonumentDetailPresented = true
                        }
                    )
                    .padding(.bottom, 50)
                    .transition(.move(edge: .bottom).combined(with: .opacity))
                    .animation(.spring(), value: viewModel.selectedPOI?.id)
                }
            }
        }
        .fullScreenCover(isPresented: $isARPresented) {
            if let poi = viewModel.selectedPOI {
                ARNavigationView(destination: poi, onDismiss: {
                    isARPresented = false
                })
            }
        }
        .fullScreenCover(isPresented: $isMonumentDetailPresented) {
            MonumentDetailView(
                title: viewModel.selectedPOI?.name ?? "Giza Monument",
                description: viewModel.selectedPOI?.description ?? "Ancient monumental site on the Giza Plateau.",
                onBack: { isMonumentDetailPresented = false },
                onPlayAudio: { isMonumentDetailPresented = false },
                onStartAR: {
                    isMonumentDetailPresented = false
                    isARPresented = true
                },
                onUnlockZone: {
                    isMonumentDetailPresented = false
                    isCheckoutPresented = true
                }
            )
        }
        .fullScreenCover(isPresented: $isCheckoutPresented) {
            CheckoutView(
                passTitle: viewModel.selectedPOI?.name ?? "Giza Necropolis Pass",
                onDismiss: { isCheckoutPresented = false },
                onPaymentSuccess: {
                    isCheckoutPresented = false
                    isSuccessPresented = true
                }
            )
        }
        .fullScreenCover(isPresented: $isSuccessPresented) {
            PurchaseSuccessView(
                zoneName: viewModel.selectedPOI?.name ?? "Giza Necropolis Zone",
                onExplore: { isSuccessPresented = false },
                onDownloadPacks: { isSuccessPresented = false },
                onClose: { isSuccessPresented = false }
            )
        }
    }
}

// MARK: - Native MKMapView Wrapper with Polyline Renderer
struct NativeMapView: UIViewRepresentable {
    let pois: [POI]
    let selectedPOI: POI?
    let routeCoordinates: [CLLocationCoordinate2D]
    var onSelectPOI: (POI) -> Void
    
    func makeUIView(context: Context) -> MKMapView {
        let mapView = MKMapView()
        mapView.delegate = context.coordinator
        mapView.showsUserLocation = true
        mapView.userTrackingMode = .none
        
        // Center on Giza Plateau initially
        let initialRegion = MKCoordinateRegion(
            center: CLLocationCoordinate2D(latitude: 29.9770, longitude: 31.1345),
            span: MKCoordinateSpan(latitudeDelta: 0.025, longitudeDelta: 0.025)
        )
        mapView.setRegion(initialRegion, animated: false)
        
        return mapView
    }
    
    func updateUIView(_ uiView: MKMapView, context: Context) {
        context.coordinator.parent = self
        
        // 1. Sync Annotations
        let existingAnnotations = uiView.annotations.compactMap { $0 as? POIAnnotation }
        if existingAnnotations.count != pois.count {
            uiView.removeAnnotations(uiView.annotations)
            let newAnnotations = pois.map { POIAnnotation(poi: $0) }
            uiView.addAnnotations(newAnnotations)
        }
        
        // 2. Sync Polyline Overlay
        let currentOverlays = uiView.overlays.filter { $0 is MKPolyline }
        uiView.removeOverlays(currentOverlays)
        
        if !routeCoordinates.isEmpty {
            let polyline = MKPolyline(coordinates: routeCoordinates, count: routeCoordinates.count)
            uiView.addOverlay(polyline, level: .aboveRoads)
            
            // Frame route camera smoothly
            let mapRect = polyline.boundingMapRect
            let edgePadding = UIEdgeInsets(top: 140, left: 60, bottom: 260, right: 60)
            uiView.setVisibleMapRect(mapRect, edgePadding: edgePadding, animated: true)
        }
    }
    
    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }
    
    class Coordinator: NSObject, MKMapViewDelegate {
        var parent: NativeMapView
        
        init(_ parent: NativeMapView) {
            self.parent = parent
        }
        
        func mapView(_ mapView: MKMapView, viewFor annotation: MKAnnotation) -> MKAnnotationView? {
            guard let poiAnnotation = annotation as? POIAnnotation else { return nil }
            
            let identifier = "POIMarker"
            var view = mapView.dequeueReusableAnnotationView(withIdentifier: identifier) as? MKMarkerAnnotationView
            if view == nil {
                view = MKMarkerAnnotationView(annotation: annotation, reuseIdentifier: identifier)
                view?.canShowCallout = false
            } else {
                view?.annotation = annotation
            }
            
            view?.markerTintColor = UIColor(Color.primaryGold)
            view?.glyphTintColor = .white
            
            switch poiAnnotation.poi.type {
            case .pyramid:
                view?.glyphImage = UIImage(systemName: "triangle.fill")
            case .temple:
                view?.glyphImage = UIImage(systemName: "building.columns.fill")
            case .museum:
                view?.glyphImage = UIImage(systemName: "building.2.fill")
            case .market:
                view?.glyphImage = UIImage(systemName: "bag.fill")
            case .modern:
                view?.glyphImage = UIImage(systemName: "building.fill")
            }
            
            return view
        }
        
        func mapView(_ mapView: MKMapView, didSelect view: MKAnnotationView) {
            guard let poiAnnotation = view.annotation as? POIAnnotation else { return }
            parent.onSelectPOI(poiAnnotation.poi)
        }
        
        func mapView(_ mapView: MKMapView, rendererFor overlay: MKOverlay) -> MKOverlayRenderer {
            if let polyline = overlay as? MKPolyline {
                let renderer = MKPolylineRenderer(polyline: polyline)
                renderer.strokeColor = UIColor(Color.primaryGold)
                renderer.lineWidth = 6.0
                renderer.lineCap = .round
                renderer.lineJoin = .round
                return renderer
            }
            return MKOverlayRenderer(overlay: overlay)
        }
    }
}

class POIAnnotation: NSObject, MKAnnotation {
    let poi: POI
    var coordinate: CLLocationCoordinate2D {
        CLLocationCoordinate2D(latitude: poi.coordinates.latitude, longitude: poi.coordinates.longitude)
    }
    var title: String? { poi.name }
    
    init(poi: POI) {
        self.poi = poi
    }
}

// MARK: - Turn-by-Turn Guidance Banner
struct TurnByTurnBannerView: View {
    let currentStep: RouteStep
    let stepIndex: Int
    let totalSteps: Int
    let totalDistanceMeters: Double
    let etaMinutes: Int
    var onNextStep: () -> Void
    var onExit: () -> Void
    
    var body: some View {
        VStack(spacing: 12) {
            HStack(spacing: 16) {
                // Maneuver Icon
                ZStack {
                    Circle()
                        .fill(Color.primaryGold)
                        .frame(width: 48, height: 48)
                    
                    Image(systemName: currentStep.iconName)
                        .font(.system(size: 24, weight: .bold))
                        .foregroundColor(.white)
                }
                
                // Maneuver Details
                VStack(alignment: .leading, spacing: 4) {
                    Text("In \(Int(currentStep.distanceMeters))m")
                        .font(.headline)
                        .fontWeight(.heavy)
                        .foregroundColor(.primaryGold)
                    
                    Text(currentStep.instructions)
                        .font(.subheadline)
                        .fontWeight(.medium)
                        .foregroundColor(.white)
                        .lineLimit(2)
                }
                
                Spacer()
                
                // Exit Button
                Button(action: onExit) {
                    Image(systemName: "xmark.circle.fill")
                        .font(.system(size: 26))
                        .foregroundColor(.gray)
                }
            }
            
            Divider().background(Color.white.opacity(0.15))
            
            // Bottom Info Bar
            HStack {
                HStack(spacing: 8) {
                    Image(systemName: "clock.fill")
                        .foregroundColor(.primaryGold)
                    Text("\(etaMinutes) min (\(Int(totalDistanceMeters))m remaining)")
                        .font(.caption)
                        .fontWeight(.semibold)
                        .foregroundColor(.white.opacity(0.85))
                }
                
                Spacer()
                
                // Step Progress & Advance
                HStack(spacing: 10) {
                    Text("Step \(stepIndex)/\(totalSteps)")
                        .font(.caption2)
                        .foregroundColor(.white.opacity(0.6))
                    
                    if stepIndex < totalSteps {
                        Button(action: onNextStep) {
                            Text("Next >")
                                .font(.caption)
                                .fontWeight(.bold)
                                .foregroundColor(.primaryGold)
                        }
                    }
                }
            }
        }
        .padding(16)
        .background(
            RoundedRectangle(cornerRadius: 18)
                .fill(Color.black.opacity(0.88))
                .overlay(
                    RoundedRectangle(cornerRadius: 18)
                        .stroke(Color.primaryGold.opacity(0.4), lineWidth: 1.5)
                )
                .shadow(color: .black.opacity(0.4), radius: 12, y: 6)
        )
    }
}

// MARK: - POI Route Card with Navigation Actions
struct POIRouteCardView: View {
    let poi: POI
    let isLoading: Bool
    let hasRoute: Bool
    let distanceMeters: Double
    let etaMinutes: Int
    var onDismiss: () -> Void
    var onCalculateRoute: () -> Void
    var onStartNavigation: () -> Void
    var onExploreAR: () -> Void
    var onViewDetails: (() -> Void)? = nil
    
    var body: some View {
        VStack(alignment: .leading, spacing: 14) {
            HStack {
                VStack(alignment: .leading, spacing: 2) {
                    Text(poi.name)
                        .font(.title3)
                        .fontWeight(.bold)
                        .foregroundColor(.primaryGold)
                    
                    Text(poi.historicalPeriod ?? "Ancient Egypt")
                        .font(.caption)
                        .fontWeight(.semibold)
                        .foregroundColor(.secondary)
                }
                
                Spacer()
                
                Button(action: onDismiss) {
                    Image(systemName: "xmark.circle.fill")
                        .font(.title3)
                        .foregroundColor(.gray)
                }
            }
            
            Text(poi.description ?? "")
                .font(.body)
                .foregroundColor(.secondary)
                .lineLimit(2)
            
            if hasRoute {
                // Route Summary Pill
                HStack(spacing: 16) {
                    Label("\(Int(distanceMeters))m walking", systemImage: "figure.walk")
                        .font(.subheadline)
                        .fontWeight(.semibold)
                        .foregroundColor(.primaryGold)
                    
                    Label("~\(etaMinutes) min walk", systemImage: "clock")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
                .padding(.vertical, 4)
            }
            
            // Action Buttons
            HStack(spacing: 12) {
                if !hasRoute {
                    Button(action: onCalculateRoute) {
                        HStack {
                            if isLoading {
                                ProgressView()
                                    .progressViewStyle(CircularProgressViewStyle(tint: .white))
                            } else {
                                Image(systemName: "arrow.triangle.turn.up.right.diamond.fill")
                                Text("Directions")
                            }
                        }
                        .font(.headline)
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 12)
                        .background(Color.primaryGold)
                        .cornerRadius(10)
                    }
                } else {
                    Button(action: onStartNavigation) {
                        HStack {
                            Image(systemName: "location.fill")
                            Text("Start Walk")
                        }
                        .font(.headline)
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 12)
                        .background(Color.green)
                        .cornerRadius(10)
                    }
                }
                
                Button(action: onExploreAR) {
                    HStack {
                        Image(systemName: "arkit")
                        Text("AR View")
                    }
                    .font(.headline)
                    .foregroundColor(Color.primaryGold)
                    .padding(.horizontal, 14)
                    .padding(.vertical, 12)
                    .background(Color.primaryGold.opacity(0.15))
                    .cornerRadius(10)
                    .overlay(
                        RoundedRectangle(cornerRadius: 10)
                            .stroke(Color.primaryGold, lineWidth: 1.5)
                    )
                }
                
                if let onViewDetails = onViewDetails {
                    Button(action: onViewDetails) {
                        Image(systemName: "info.circle.fill")
                            .font(.headline)
                            .foregroundColor(Color.primaryGold)
                            .padding(.horizontal, 14)
                            .padding(.vertical, 12)
                            .background(Color.primaryGold.opacity(0.15))
                            .cornerRadius(10)
                            .overlay(
                                RoundedRectangle(cornerRadius: 10)
                                    .stroke(Color.primaryGold, lineWidth: 1.5)
                            )
                    }
                }
            }
        }
        .padding(18)
        .background(Color(.systemBackground))
        .cornerRadius(20)
        .shadow(color: .black.opacity(0.25), radius: 16, y: 8)
        .padding(.horizontal, 16)
    }
}

