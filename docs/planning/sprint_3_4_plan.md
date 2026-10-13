# EgyTour - Sprint Execution Plan Part 2 (Sprints 3-6)

## 📅 Sprint 3: Navigation & Maps (Weeks 5-6)

### Sprint Goal
Implement native maps with POI markers and AR wayfinding on both platforms.

### Tasks

#### 3.1 iOS MapKit Implementation (iOS - Day 1-3)

```swift
// Features/Explore/Views/MapView.swift
import SwiftUI
import MapKit

struct ExploreMapView: View {
    @StateObject private var viewModel = ExploreViewModel()
    @State private var region = MKCoordinateRegion(
        center: CLLocationCoordinate2D(latitude: 29.9792, longitude: 31.1342),
        span: MKCoordinateSpan(latitudeDelta: 0.1, longitudeDelta: 0.1)
    )
    @State private var selectedPOI: POI?
    
    var body: some View {
        ZStack {
            Map(coordinateRegion: $region, annotationItems: viewModel.pois) { poi in
                MapAnnotation(coordinate: CLLocationCoordinate2D(
                    latitude: poi.coordinates.latitude,
                    longitude: poi.coordinates.longitude
                )) {
                    POIMarkerView(poi: poi, isSelected: selectedPOI?.id == poi.id)
                        .onTapGesture {
                            withAnimation {
                                selectedPOI = poi
                            }
                        }
                }
            }
            .edgesIgnoringSafeArea(.all)
            
            // Bottom sheet with POI cards
            VStack {
                Spacer()
                
                if !viewModel.pois.isEmpty {
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 16) {
                            ForEach(viewModel.pois) { poi in
                                POICardView(poi: poi)
                                    .frame(width: 300)
                                    .onTapGesture {
                                        selectedPOI = poi
                                    }
                            }
                        }
                        .padding(.horizontal)
                    }
                    .frame(height: 200)
                    .background(
                        LinearGradient(
                            colors: [.clear, Color.bgPrimary.opacity(0.95)],
                            startPoint: .top,
                            endPoint: .bottom
                        )
                    )
                }
            }
            
            // AR Navigation Button
            VStack {
                HStack {
                    Spacer()
                    
                    if let poi = selectedPOI {
                        Button(action: {
                            // Navigate to AR view
                        }) {
                            VStack(spacing: 4) {
                                Image(systemName: "arkit")
                                    .font(.title2)
                                Text("AR Navigate")
                                    .font(.caption)
                            }
                            .foregroundColor(.white)
                            .padding()
                            .background(Color.primaryGold)
                            .clipShape(RoundedRectangle(cornerRadius: 12))
                            .shadow(radius: 10)
                        }
                        .padding()
                    }
                }
                
                Spacer()
            }
        }
        .onAppear {
            viewModel.loadPOIs()
        }
    }
}

struct POIMarkerView: View {
    let poi: POI
    let isSelected: Bool
    @State private var isPulsing = false
    
    var body: some View {
        ZStack {
            // Pulsing ring
            Circle()
                .fill(Color.primaryGold.opacity(0.3))
                .frame(width: 40, height: 40)
                .scaleEffect(isPulsing ? 1.5 : 1.0)
                .opacity(isPulsing ? 0 : 1)
                .animation(
                    Animation.easeOut(duration: 1.5).repeatForever(autoreverses: false),
                    value: isPulsing
                )
            
            // Marker
            Circle()
                .fill(isSelected ? Color.papyrusOrange : Color.primaryGold)
                .frame(width: 20, height: 20)
                .overlay(
                    Circle()
                        .stroke(Color.white, lineWidth: 2)
                )
                .shadow(color: Color.primaryGold.opacity(0.5), radius: 10)
                .scaleEffect(isSelected ? 1.2 : 1.0)
        }
        .onAppear {
            isPulsing = true
        }
    }
}

struct POICardView: View {
    let poi: POI
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            AsyncImage(url: URL(string: poi.imageUrl ?? "")) { image in
                image
                    .resizable()
                    .aspectRatio(contentMode: .fill)
            } placeholder: {
                Color.bgSecondary
            }
            .frame(height: 120)
            .clipShape(RoundedRectangle(cornerRadius: 12))
            
            VStack(alignment: .leading, spacing: 4) {
                Text(poi.historicalPeriod ?? "")
                    .font(.caption)
                    .foregroundColor(.primaryGold)
                    .textCase(.uppercase)
                
                Text(poi.name)
                    .font(.poiTitle)
                    .foregroundColor(.textPrimary)
                    .lineLimit(2)
            }
        }
        .padding()
        .background(Color.bgSecondary)
        .clipShape(RoundedRectangle(cornerRadius: 16))
        .shadow(radius: 5)
    }
}
```

#### 3.2 iOS ARKit Navigation (iOS - Day 4-5)

```swift
// Features/AR/Views/ARNavigationView.swift
import SwiftUI
import ARKit
import RealityKit

struct ARNavigationView: View {
    @StateObject private var viewModel: ARViewModel
    let destination: POI
    
    init(destination: POI) {
        self.destination = destination
        _viewModel = StateObject(wrappedValue: ARViewModel(destination: destination))
    }
    
    var body: some View {
        ZStack {
            ARViewContainer(viewModel: viewModel)
                .edgesIgnoringSafeArea(.all)
            
            VStack {
                // Destination info overlay
                HStack {
                    VStack(alignment: .leading, spacing: 8) {
                        Text(destination.name)
                            .font(.siteName)
                            .foregroundColor(.white)
                            .shadow(radius: 10)
                        
                        HStack {
                            Image(systemName: "location.fill")
                            Text("\(Int(viewModel.distanceToDestination))m away")
                                .font(.caption)
                        }
                        .foregroundColor(.primaryGold)
                    }
                    Spacer()
                }
                .padding()
                .background(
                    LinearGradient(
                        colors: [.black.opacity(0.6), .clear],
                        startPoint: .top,
                        endPoint: .bottom
                    )
                )
                
                Spacer()
                
                // Controls
                HStack(spacing: 20) {
                    Button(action: { viewModel.toggleARMode() }) {
                        Image(systemName: viewModel.isARActive ? "map.fill" : "arkit")
                            .font(.title2)
                            .foregroundColor(.white)
                            .frame(width: 60, height: 60)
                            .background(Color.primaryGold)
                            .clipShape(Circle())
                            .shadow(radius: 10)
                    }
                }
                .padding(.bottom, 40)
            }
        }
    }
}

struct ARViewContainer: UIViewRepresentable {
    @ObservedObject var viewModel: ARViewModel
    
    func makeUIView(context: Context) -> ARView {
        let arView = ARView(frame: .zero)
        
        // Configure AR session
        let config = ARWorldTrackingConfiguration()
        config.planeDetection = [.horizontal]
        config.worldAlignment = .gravityAndHeading
        
        arView.session.run(config)
        
        // Setup AR scene
        viewModel.setupARScene(arView: arView)
        
        return arView
    }
    
    func updateUIView(_ uiView: ARView, context: Context) {
        viewModel.updateARContent(arView: uiView)
    }
}

// Features/AR/ViewModels/ARViewModel.swift
import ARKit
import RealityKit
import CoreLocation

class ARViewModel: NSObject, ObservableObject {
    @Published var distanceToDestination: Double = 0
    @Published var isARActive = true
    
    let destination: POI
    private var arView: ARView?
    private var directionAnchor: AnchorEntity?
    private let locationService = LocationService.shared
    
    init(destination: POI) {
        self.destination = destination
        super.init()
    }
    
    func setupARScene(arView: ARView) {
        self.arView = arView
        
        // Create direction arrow
        let arrowEntity = createDirectionArrow()
        
        // Anchor at 2 meters in front
        let anchor = AnchorEntity(world: [0, 0, -2])
        anchor.addChild(arrowEntity)
        
        arView.scene.addAnchor(anchor)
        self.directionAnchor = anchor
    }
    
    func updateARContent(arView: ARView) {
        guard let userLocation = locationService.currentLocation else { return }
        
        // Calculate bearing to destination
        let bearing = calculateBearing(
            from: userLocation.coordinate,
            to: CLLocationCoordinate2D(
                latitude: destination.coordinates.latitude,
                longitude: destination.coordinates.longitude
            )
        )
        
        // Rotate arrow to point at destination
        let rotation = simd_quatf(angle: Float(bearing.degreesToRadians), axis: [0, 1, 0])
        directionAnchor?.transform.rotation = rotation
        
        // Update distance
        let destLocation = CLLocation(
            latitude: destination.coordinates.latitude,
            longitude: destination.coordinates.longitude
        )
        distanceToDestination = userLocation.distance(from: destLocation)
    }
    
    private func createDirectionArrow() -> ModelEntity {
        // Create golden arrow mesh
        let mesh = MeshResource.generateBox(size: [0.1, 0.1, 0.5])
        var material = SimpleMaterial()
        material.color = .init(tint: UIColor(Color.primaryGold), texture: nil)
        material.metallic = .init(floatLiteral: 0.8)
        material.roughness = .init(floatLiteral: 0.2)
        
        let arrow = ModelEntity(mesh: mesh, materials: [material])
        
        // Add pulsing animation
        var transform = arrow.transform
        transform.scale = [1.2, 1.2, 1.2]
        arrow.move(to: transform, relativeTo: arrow, duration: 0.5, timingFunction: .easeInOut)
        
        return arrow
    }
    
    private func calculateBearing(from: CLLocationCoordinate2D, to: CLLocationCoordinate2D) -> Double {
        let lat1 = from.latitude.degreesToRadians
        let lon1 = from.longitude.degreesToRadians
        let lat2 = to.latitude.degreesToRadians
        let lon2 = to.longitude.degreesToRadians
        
        let dLon = lon2 - lon1
        let y = sin(dLon) * cos(lat2)
        let x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)
        let bearing = atan2(y, x)
        
        return bearing.radiansToDegrees
    }
    
    func toggleARMode() {
        isARActive.toggle()
    }
}

extension Double {
    var degreesToRadians: Double { self * .pi / 180 }
    var radiansToDegrees: Double { self * 180 / .pi }
}
```

#### 3.3 Android Google Maps Implementation (Android - Day 1-3)

```kotlin
// features/explore/ui/MapScreen.kt
package com.egytour.features.explore.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.egytour.core.theme.EgytourColors

@Composable
fun ExploreMapScreen(
    viewModel: ExploreViewModel = hiltViewModel()
) {
    val pois by viewModel.pois.collectAsState()
    val selectedPOI by viewModel.selectedPOI.collectAsState()
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(29.9792, 31.1342), // Giza
            12f
        )
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                mapType = MapType.NORMAL,
                isMyLocationEnabled = true
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = true
            )
        ) {
            pois.forEach { poi ->
                POIMarker(
                    poi = poi,
                    isSelected = selectedPOI?.id == poi.id,
                    onClick = { viewModel.selectPOI(poi) }
                )
            }
        }
        
        // Bottom sheet with POI cards
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            if (pois.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    EgytourColors.BgPrimary.copy(alpha = 0.95f)
                                )
                            )
                        ),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(pois) { poi ->
                        POICard(
                            poi = poi,
                            modifier = Modifier.width(300.dp),
                            onClick = { viewModel.selectPOI(poi) }
                        )
                    }
                }
            }
        }
        
        // AR Navigation Button
        if (selectedPOI != null) {
            FloatingActionButton(
                onClick = { /* Navigate to AR */ },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                containerColor = EgytourColors.PrimaryGold
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(Icons.Default.ViewInAr, "AR Navigate")
                    Text("AR", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
fun POIMarker(
    poi: POI,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val position = LatLng(poi.coordinates.latitude, poi.coordinates.longitude)
    
    Marker(
        state = MarkerState(position = position),
        title = poi.name,
        onClick = {
            onClick()
            true
        },
        icon = BitmapDescriptorFactory.defaultMarkerWithHue(
            if (isSelected) BitmapDescriptorFactory.HUE_ORANGE
            else BitmapDescriptorFactory.HUE_YELLOW
        )
    )
    
    // Trigger radius circle
    Circle(
        center = position,
        radius = poi.triggerRadiusMeters.toDouble(),
        fillColor = EgytourColors.PrimaryGold.copy(alpha = 0.2f),
        strokeColor = EgytourColors.PrimaryGold,
        strokeWidth = 2f
    )
}
```

#### 3.4 Android ARCore Navigation (Android - Day 4-5)

```kotlin
// features/ar/ui/ARNavigationScreen.kt
package com.egytour.features.ar.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.ar.core.*
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.egytour.core.theme.EgytourColors

@Composable
fun ARNavigationScreen(
    destination: POI,
    viewModel: ARViewModel = hiltViewModel()
) {
    val distance by viewModel.distanceToDestination.collectAsState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                ArFragment().apply {
                    setupARScene(this, destination, viewModel)
                }.view
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Destination info overlay
        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
                .fillMaxWidth(0.9f),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.6f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = destination.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = EgytourColors.PrimaryGold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${distance.toInt()}m away",
                        style = MaterialTheme.typography.bodyMedium,
                        color = EgytourColors.PrimaryGold
                    )
                }
            }
        }
        
        // Toggle AR button
        FloatingActionButton(
            onClick = { viewModel.toggleARMode() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            containerColor = EgytourColors.PrimaryGold
        ) {
            Icon(
                imageVector = Icons.Default.Map,
                contentDescription = "Toggle Map"
            )
        }
    }
}

private fun setupARScene(
    arFragment: ArFragment,
    destination: POI,
    viewModel: ARViewModel
) {
    arFragment.arSceneView.scene.addOnUpdateListener { frameTime ->
        val frame = arFragment.arSceneView.arFrame ?: return@addOnUpdateListener
        
        // Update arrow direction
        updateARContent(arFragment, destination, viewModel)
    }
    
    // Create direction arrow
    ModelRenderable.builder()
        .setSource(context, R.raw.arrow_model)
        .build()
        .thenAccept { renderable ->
            val anchor = arFragment.arSceneView.session?.createAnchor(
                Pose.makeTranslation(0f, 0f, -2f)
            )
            
            val anchorNode = AnchorNode(anchor)
            anchorNode.renderable = renderable
            arFragment.arSceneView.scene.addChild(anchorNode)
        }
}
```

### Sprint 3 Success Criteria
- ✅ Maps display POI markers on both platforms
- ✅ User location tracking on map
- ✅ POI selection shows details
- ✅ AR navigation shows direction arrow
- ✅ Distance to destination updates in real-time
- ✅ Smooth transitions between map and AR

---

## 📅 Sprint 4: AI Features (Weeks 7-8)

### Sprint Goal
Implement RAG-powered AI Q&A system with chat interface.

### Tasks

#### 4.1 RAG Backend Setup (Backend - Day 1-3)

```typescript
// services/ai-guide/src/rag.ts
import { GoogleGenerativeAI } from '@google/generative-ai';
import { db } from '../../packages/db';
import { knowledgeChunks } from '../../packages/db/schema';
import { sql } from 'drizzle-orm';

export class RAGService {
    private genAI: GoogleGenerativeAI;
    
    constructor() {
        this.genAI = new GoogleGenerativeAI(process.env.GEMINI_API_KEY!);
    }
    
    async askQuestion(question: string, poiId?: string): Promise<{
        answer: string;
        sources: Array<{ title: string; chunkId: string }>;
        confidence: number;
    }> {
        // 1. Generate embedding for question (simplified - use actual embedding model)
        const questionEmbedding = await this.generateEmbedding(question);
        
        // 2. Vector similarity search
        const relevantChunks = await db.execute(sql`
            SELECT id, content, metadata, poi_id,
                   1 - (embedding <=> ${questionEmbedding}::vector) as similarity
            FROM knowledge_chunks
            WHERE ${poiId ? sql`poi_id = ${poiId}` : sql`1=1`}
            ORDER BY embedding <=> ${questionEmbedding}::vector
            LIMIT 5
        `);
        
        // 3. Build context from chunks
        const context = relevantChunks.rows.map(chunk => chunk.content).join('\n\n');
        
        // 4. Generate answer with Gemini
        const model = this.genAI.getGenerativeModel({ model: 'gemini-pro' });
        
        const prompt = `You are an expert Egyptologist providing accurate historical information about ancient Egypt.

Context from verified sources:
${context}

User question: ${question}

Provide a detailed, accurate answer based ONLY on the context above. If the context doesn't contain enough information, say so. Include specific dates, names, and historical details when available. Keep the answer concise but informative (2-3 paragraphs maximum).

Answer:`;
        
        const result = await model.generateContent(prompt);
        const answer = result.response.text();
        
        // 5. Calculate confidence based on similarity scores
        const avgSimilarity = relevantChunks.rows.reduce((sum, chunk) => sum + chunk.similarity, 0) / relevantChunks.rows.length;
        
        return {
            answer,
            sources: relevantChunks.rows.map(chunk => ({
                title: chunk.metadata?.title || 'Historical Record',
                chunkId: chunk.id,
            })),
            confidence: avgSimilarity,
        };
    }
    
    private async generateEmbedding(text: string): Promise<number[]> {
        // Use sentence-transformers or Gemini embedding API
        // For now, placeholder
        return new Array(768).fill(0);
    }
}

// src/routes/ai.ts
import { FastifyPluginAsync } from 'fastify';
import { z } from 'zod';
import { RAGService } from '../services/ai-guide/rag';

const askSchema = z.object({
    question: z.string().min(1).max(500),
    poiId: z.string().uuid().optional(),
    conversationId: z.string().uuid().optional(),
});

const aiRoutes: FastifyPluginAsync = async (fastify) => {
    const ragService = new RAGService();
    
    fastify.post('/ask', {
        config: {
            rateLimit: {
                max: 10,
                timeWindow: '1 minute'
            }
        }
    }, async (request, reply) => {
        const { question, poiId } = askSchema.parse(request.body);
        
        const result = await ragService.askQuestion(question, poiId);
        
        return {
            answerText: result.answer,
            sources: result.sources,
            confidence: result.confidence,
            followUpSuggestions: [
                "Tell me more about the construction",
                "Who built this?",
                "When was it discovered?"
            ]
        };
    });
};

export default aiRoutes;
```

#### 4.2 iOS AI Chat Interface (iOS - Day 4-5)

```swift
// Features/AI/Views/AIChatView.swift
import SwiftUI

struct AIChatView: View {
    @StateObject private var viewModel: AIChatViewModel
    @State private var messageText = ""
    let poi: POI
    
    init(poi: POI) {
        self.poi = poi
        _viewModel = StateObject(wrappedValue: AIChatViewModel(poi: poi))
    }
    
    var body: some View {
        VStack(spacing: 0) {
            // Messages
            ScrollViewReader { proxy in
                ScrollView {
                    LazyVStack(spacing: 16) {
                        ForEach(viewModel.messages) { message in
                            MessageBubble(message: message)
                                .id(message.id)
                        }
                        
                        if viewModel.isLoading {
                            HStack {
                                ProgressView()
                                Text("Thinking...")
                                    .font(.caption)
                                    .foregroundColor(.textSecondary)
                            }
                            .padding()
                        }
                    }
                    .padding()
                }
                .onChange(of: viewModel.messages.count) { _ in
                    if let lastMessage = viewModel.messages.last {
                        withAnimation {
                            proxy.scrollTo(lastMessage.id, anchor: .bottom)
                        }
                    }
                }
            }
            
            // Follow-up suggestions
            if !viewModel.followUpSuggestions.isEmpty {
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 8) {
                        ForEach(viewModel.followUpSuggestions, id: \.self) { suggestion in
                            Button(action: {
                                viewModel.askQuestion(suggestion)
                            }) {
                                Text(suggestion)
                                    .font(.caption)
                                    .padding(.horizontal, 12)
                                    .padding(.vertical, 8)
                                    .background(Color.bgSecondary)
                                    .foregroundColor(.textPrimary)
                                    .cornerRadius(16)
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 16)
                                            .stroke(Color.border, lineWidth: 1)
                                    )
                            }
                        }
                    }
                    .padding(.horizontal)
                }
                .padding(.vertical, 8)
            }
            
            // Input
            HStack(spacing: 12) {
                TextField("Ask about \(poi.name)...", text: $messageText)
                    .textFieldStyle(EgytourTextFieldStyle())
                
                Button(action: {
                    viewModel.askQuestion(messageText)
                    messageText = ""
                }) {
                    Image(systemName: "arrow.up.circle.fill")
                        .font(.title2)
                        .foregroundColor(messageText.isEmpty ? .textTertiary : .primaryGold)
                }
                .disabled(messageText.isEmpty)
            }
            .padding()
            .background(Color.bgSecondary)
        }
        .background(Color.bgPrimary)
        .navigationTitle("Ask AI")
        .navigationBarTitleDisplayMode(.inline)
    }
}

struct MessageBubble: View {
    let message: ChatMessage
    
    var body: some View {
        HStack {
            if message.isUser {
                Spacer()
            }
            
            VStack(alignment: message.isUser ? .trailing : .leading, spacing: 4) {
                Text(message.text)
                    .font(.bodyMedium)
                    .foregroundColor(message.isUser ? .bgPrimary : .textPrimary)
                    .padding()
                    .background(message.isUser ? Color.nileBlue : Color.bgSecondary)
                    .cornerRadius(16)
                
                if !message.isUser && !message.sources.isEmpty {
                    Text("Sources: \(message.sources.count)")
                        .font(.caption)
                        .foregroundColor(.textTertiary)
                }
            }
            .frame(maxWidth: 280, alignment: message.isUser ? .trailing : .leading)
            
            if !message.isUser {
                Spacer()
            }
        }
    }
}

// Features/AI/ViewModels/AIChatViewModel.swift
import Foundation

struct ChatMessage: Identifiable {
    let id = UUID()
    let text: String
    let isUser: Bool
    let sources: [String]
    let timestamp: Date
}

@MainActor
class AIChatViewModel: ObservableObject {
    @Published var messages: [ChatMessage] = []
    @Published var isLoading = false
    @Published var followUpSuggestions: [String] = []
    
    let poi: POI
    
    init(poi: POI) {
        self.poi = poi
        
        // Welcome message
        messages.append(ChatMessage(
            text: "Hi! I'm your AI guide for \(poi.name). Ask me anything about its history, architecture, or significance!",
            isUser: false,
            sources: [],
            timestamp: Date()
        ))
    }
    
    func askQuestion(_ question: String) {
        // Add user message
        messages.append(ChatMessage(
            text: question,
            isUser: true,
            sources: [],
            timestamp: Date()
        ))
        
        isLoading = true
        
        Task {
            do {
                let response = try await AIService.shared.askQuestion(question, poiId: poi.id)
                
                // Add AI response
                messages.append(ChatMessage(
                    text: response.answerText,
                    isUser: false,
                    sources: response.sources.map { $0.title },
                    timestamp: Date()
                ))
                
                followUpSuggestions = response.followUpSuggestions
                
            } catch {
                messages.append(ChatMessage(
                    text: "Sorry, I encountered an error. Please try again.",
                    isUser: false,
                    sources: [],
                    timestamp: Date()
                ))
            }
            
            isLoading = false
        }
    }
}
```

### Sprint 4 Success Criteria
- ✅ RAG backend retrieves relevant context
- ✅ Gemini generates accurate answers
- ✅ Chat interface works on both platforms
- ✅ Follow-up suggestions displayed
- ✅ Source citations shown
- ✅ Rate limiting prevents abuse

---

*Continuing with Sprint 5 (Trip Planning) and Sprint 6 (Polish & Launch) - would you like me to complete those as well?*
