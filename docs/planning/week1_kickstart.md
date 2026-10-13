# EgyTour - Native Development Week 1 Kickstart Guide

## 🎯 Decision Confirmed: Native Swift + Kotlin

**Excellent choice!** You'll get:
- ✅ Maximum AR performance (ARKit + ARCore)
- ✅ Best Maps integration (MapKit + Google Maps)
- ✅ Future-proof architecture
- ✅ Platform-specific excellence

---

## 📋 Week 1 Overview

### Goals
- ✅ Setup iOS and Android projects
- ✅ Implement design system
- ✅ Create shared backend API
- ✅ Build authentication screens
- ✅ Setup CI/CD pipelines

### Team Structure

```
Team Composition (Recommended):
├── iOS Team
│   ├── Senior iOS Developer (Swift/SwiftUI)
│   └── iOS Developer (Swift/SwiftUI)
├── Android Team
│   ├── Senior Android Developer (Kotlin/Compose)
│   └── Android Developer (Kotlin/Compose)
├── Backend Team
│   └── Backend Developer (Node.js/Fastify)
├── Design
│   └── UI/UX Designer
└── Content
    └── Egyptologist (Part-time)

Total: 7 people
```

---

## 🚀 Day 1: Project Setup

### iOS Project Setup

```bash
#!/bin/bash
# setup-ios-project.sh

echo "🍎 Setting up EgyTour iOS Project..."

# Create Xcode project directory
mkdir -p EgyTour-iOS
cd EgyTour-iOS

# Initialize Git
git init
git remote add origin https://github.com/your-org/egytour-ios.git

# Create .gitignore
cat > .gitignore << 'EOF'
# Xcode
*.xcodeproj/*
!*.xcodeproj/project.pbxproj
!*.xcodeproj/xcshareddata/
!*.xcworkspace/contents.xcworkspacedata
*.xcuserstate
*.xcuserdatad/

# Swift Package Manager
.build/
.swiftpm/

# CocoaPods
Pods/
*.xcworkspace

# Fastlane
fastlane/report.xml
fastlane/Preview.html
fastlane/screenshots
fastlane/test_output

# Build
DerivedData/
build/

# Other
.DS_Store
*.swp
EOF

# Create directory structure
mkdir -p Egytour/{App,Features,Services,Core,Resources}
mkdir -p Egytour/Features/{Auth,Explore,POI,AR,Itinerary,Downloads}
mkdir -p Egytour/Services
mkdir -p Egytour/Core/{Extensions,Utilities,Constants}
mkdir -p Egytour/Resources/{Assets.xcassets,Fonts,Localizations}

echo "✅ iOS project structure created!"

# Create Package.swift for SPM dependencies
cat > Package.swift << 'EOF'
// swift-tools-version:5.9
import PackageDescription

let package = Package(
    name: "Egytour",
    platforms: [.iOS(.v17)],
    products: [
        .library(name: "Egytour", targets: ["Egytour"])
    ],
    dependencies: [
        .package(url: "https://github.com/Alamofire/Alamofire.git", from: "5.8.0"),
        .package(url: "https://github.com/realm/realm-swift.git", from: "10.45.0"),
    ],
    targets: [
        .target(
            name: "Egytour",
            dependencies: ["Alamofire", .product(name: "RealmSwift", package: "realm-swift")]
        )
    ]
)
EOF

echo "✅ iOS project setup complete!"
```

### Android Project Setup

```bash
#!/bin/bash
# setup-android-project.sh

echo "🤖 Setting up EgyTour Android Project..."

# Create Android project directory
mkdir -p EgyTour-Android
cd EgyTour-Android

# Initialize Git
git init
git remote add origin https://github.com/your-org/egytour-android.git

# Create .gitignore
cat > .gitignore << 'EOF'
# Gradle
.gradle/
build/
local.properties

# Android Studio
.idea/
*.iml
.DS_Store

# Build
*.apk
*.aab

# Other
captures/
.externalNativeBuild/
.cxx/
EOF

# Create directory structure
mkdir -p app/src/main/java/com/egytour/{features,services,core,data}
mkdir -p app/src/main/java/com/egytour/features/{auth,explore,poi,ar,itinerary,downloads}
mkdir -p app/src/main/res/{layout,drawable,values,font}

echo "✅ Android project structure created!"

# Create settings.gradle.kts
cat > settings.gradle.kts << 'EOF'
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Egytour"
include(":app")
EOF

# Create build.gradle.kts (project level)
cat > build.gradle.kts << 'EOF'
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
}
EOF

# Create app/build.gradle.kts
cat > app/build.gradle.kts << 'EOF'
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "com.egytour"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.egytour"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Compose
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.activity:activity-compose:1.8.1")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.5")
    
    // Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
    
    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    
    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    
    // Maps
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    
    // ARCore
    implementation("com.google.ar:core:1.41.0")
    
    // ExoPlayer
    implementation("androidx.media3:media3-exoplayer:1.2.0")
    implementation("androidx.media3:media3-ui:1.2.0")
}
EOF

echo "✅ Android project setup complete!"
```

### Backend API Setup

```bash
#!/bin/bash
# setup-backend.sh

echo "🔧 Setting up Backend API..."

mkdir -p egytour-backend
cd egytour-backend

# Initialize Node.js project
npm init -y

# Install dependencies
npm install fastify @fastify/cors @fastify/jwt @fastify/rate-limit \
  drizzle-orm postgres zod bcrypt \
  @google/generative-ai \
  @sentry/node pino pino-pretty

npm install -D typescript @types/node tsx drizzle-kit

# Create tsconfig.json
cat > tsconfig.json << 'EOF'
{
  "compilerOptions": {
    "target": "ES2022",
    "module": "commonjs",
    "lib": ["ES2022"],
    "outDir": "./dist",
    "rootDir": "./src",
    "strict": true,
    "esModuleInterop": true,
    "skipLibCheck": true,
    "forceConsistentCasingInFileNames": true,
    "resolveJsonModule": true
  },
  "include": ["src/**/*"],
  "exclude": ["node_modules"]
}
EOF

# Create directory structure
mkdir -p src/{routes,services,plugins,utils}
mkdir -p src/routes/{auth,sites,pois,itineraries,ai,payments}

echo "✅ Backend setup complete!"
```

---

## 📱 Day 2: Design System Implementation

### iOS Design System

```swift
// Egytour/Core/Constants/Colors.swift
import SwiftUI

extension Color {
    // MARK: - Primary: Pharaoh's Gold
    static let primaryGold = Color(hex: "E6A800")
    static let primaryGold50 = Color(hex: "FFF9E6")
    static let primaryGold100 = Color(hex: "FFEEB3")
    static let primaryGold900 = Color(hex: "805C00")
    
    // MARK: - Secondary: Nile Blue
    static let nileBlue = Color(hex: "0095C4")
    static let nileBlue50 = Color(hex: "E6F4F9")
    
    // MARK: - Accent: Papyrus Orange
    static let papyrusOrange = Color(hex: "E67E22")
    
    // MARK: - Dark Mode (Default)
    static let bgPrimary = Color(hex: "0D0B09")
    static let bgSecondary = Color(hex: "1A1714")
    static let bgTertiary = Color(hex: "252220")
    
    static let textPrimary = Color(hex: "FAFAF5")
    static let textSecondary = Color(hex: "B8B5AD")
    static let textTertiary = Color(hex: "7A766D")
    
    static let border = Color(hex: "3D3832")
    
    // MARK: - Helper
    init(hex: String) {
        let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hex.count {
        case 6: // RGB
            (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        case 8: // ARGB
            (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
        default:
            (a, r, g, b) = (255, 0, 0, 0)
        }
        self.init(
            .sRGB,
            red: Double(r) / 255,
            green: Double(g) / 255,
            blue: Double(b) / 255,
            opacity: Double(a) / 255
        )
    }
}

// Egytour/Core/Constants/Typography.swift
import SwiftUI

extension Font {
    // MARK: - Display (Playfair Display)
    static let displayLarge = Font.custom("PlayfairDisplay-Bold", size: 48)
    static let displayMedium = Font.custom("PlayfairDisplay-SemiBold", size: 36)
    static let displaySmall = Font.custom("PlayfairDisplay-SemiBold", size: 28)
    
    // MARK: - Headings (Playfair Display)
    static let siteName = Font.custom("PlayfairDisplay-SemiBold", size: 28)
    static let sectionTitle = Font.custom("PlayfairDisplay-SemiBold", size: 24)
    
    // MARK: - Body (Inter)
    static let poiTitle = Font.custom("Inter-SemiBold", size: 20)
    static let bodyLarge = Font.custom("Inter-Regular", size: 18)
    static let bodyMedium = Font.custom("Inter-Regular", size: 16)
    static let bodySmall = Font.custom("Inter-Regular", size: 14)
    
    // MARK: - Captions (Inter)
    static let caption = Font.custom("Inter-Medium", size: 12)
    static let captionSmall = Font.custom("Inter-Medium", size: 10)
    
    // MARK: - Mono (JetBrains Mono)
    static let mono = Font.custom("JetBrainsMono-Medium", size: 14)
}
```

### Android Design System

```kotlin
// app/src/main/java/com/egytour/core/theme/Color.kt
package com.egytour.core.theme

import androidx.compose.ui.graphics.Color

object EgytourColors {
    // Primary: Pharaoh's Gold
    val PrimaryGold = Color(0xFFE6A800)
    val PrimaryGold50 = Color(0xFFFFF9E6)
    val PrimaryGold100 = Color(0xFFFFEEB3)
    val PrimaryGold900 = Color(0xFF805C00)
    
    // Secondary: Nile Blue
    val NileBlue = Color(0xFF0095C4)
    val NileBlue50 = Color(0xFFE6F4F9)
    
    // Accent: Papyrus Orange
    val PapyrusOrange = Color(0xFFE67E22)
    
    // Dark Mode (Default)
    val BgPrimary = Color(0xFF0D0B09)
    val BgSecondary = Color(0xFF1A1714)
    val BgTertiary = Color(0xFF252220)
    
    val TextPrimary = Color(0xFFFAFAF5)
    val TextSecondary = Color(0xFFB8B5AD)
    val TextTertiary = Color(0xFF7A766D)
    
    val Border = Color(0xFF3D3832)
}

// app/src/main/java/com/egytour/core/theme/Type.kt
package com.egytour.core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.egytour.R

val PlayfairDisplay = FontFamily(
    Font(R.font.playfair_display_regular),
    Font(R.font.playfair_display_semibold, FontWeight.SemiBold),
    Font(R.font.playfair_display_bold, FontWeight.Bold)
)

val Inter = FontFamily(
    Font(R.font.inter_regular),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold)
)

val JetBrainsMono = FontFamily(
    Font(R.font.jetbrains_mono_medium, FontWeight.Medium)
)

val EgytourTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp,
        lineHeight = 56.sp
    ),
    displayMedium = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.SemiBold,
        fontSize = 36.sp,
        lineHeight = 44.sp
    ),
    titleLarge = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    labelMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
)

// app/src/main/java/com/egytour/core/theme/Theme.kt
package com.egytour.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = EgytourColors.PrimaryGold,
    secondary = EgytourColors.NileBlue,
    tertiary = EgytourColors.PapyrusOrange,
    background = EgytourColors.BgPrimary,
    surface = EgytourColors.BgSecondary,
    onPrimary = EgytourColors.BgPrimary,
    onSecondary = EgytourColors.TextPrimary,
    onBackground = EgytourColors.TextPrimary,
    onSurface = EgytourColors.TextPrimary
)

@Composable
fun EgytourTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = EgytourTypography,
        content = content
    )
}
```

---

## 🔐 Day 3: Authentication Implementation

### iOS Authentication

```swift
// Egytour/Features/Auth/Views/LoginView.swift
import SwiftUI

struct LoginView: View {
    @StateObject private var viewModel = AuthViewModel()
    @State private var email = ""
    @State private var password = ""
    
    var body: some View {
        ZStack {
            // Background
            Color.bgPrimary.ignoresSafeArea()
            
            VStack(spacing: 32) {
                Spacer()
                
                // Logo
                VStack(spacing: 12) {
                    Image(systemName: "building.columns.fill")
                        .font(.system(size: 64))
                        .foregroundColor(.primaryGold)
                    
                    Text("EgyTour")
                        .font(.displayMedium)
                        .foregroundColor(.textPrimary)
                }
                
                // Form
                VStack(spacing: 16) {
                    TextField("Email", text: $email)
                        .textFieldStyle(EgytourTextFieldStyle())
                        .textContentType(.emailAddress)
                        .autocapitalization(.none)
                    
                    SecureField("Password", text: $password)
                        .textFieldStyle(EgytourTextFieldStyle())
                        .textContentType(.password)
                }
                .padding(.horizontal, 32)
                
                // Login Button
                Button(action: {
                    viewModel.login(email: email, password: password)
                }) {
                    HStack {
                        Text("Login")
                            .font(.bodyLarge)
                            .fontWeight(.bold)
                        
                        if viewModel.isLoading {
                            ProgressView()
                                .tint(.bgPrimary)
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 56)
                    .background(Color.primaryGold)
                    .foregroundColor(.bgPrimary)
                    .cornerRadius(12)
                }
                .disabled(viewModel.isLoading)
                .padding(.horizontal, 32)
                
                // Register Link
                Button(action: {
                    // Navigate to register
                }) {
                    Text("Don't have an account? **Sign Up**")
                        .font(.bodySmall)
                        .foregroundColor(.textSecondary)
                }
                
                Spacer()
            }
        }
        .alert("Error", isPresented: $viewModel.showError) {
            Button("OK", role: .cancel) { }
        } message: {
            Text(viewModel.errorMessage)
        }
    }
}

// Custom TextField Style
struct EgytourTextFieldStyle: TextFieldStyle {
    func _body(configuration: TextField<Self._Label>) -> some View {
        configuration
            .padding()
            .background(Color.bgSecondary)
            .foregroundColor(.textPrimary)
            .cornerRadius(12)
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .stroke(Color.border, lineWidth: 1)
            )
    }
}

// Egytour/Features/Auth/ViewModels/AuthViewModel.swift
import Foundation
import Combine

@MainActor
class AuthViewModel: ObservableObject {
    @Published var isLoading = false
    @Published var showError = false
    @Published var errorMessage = ""
    
    private let authService = AuthService.shared
    
    func login(email: String, password: String) {
        isLoading = true
        
        Task {
            do {
                let user = try await authService.login(email: email, password: password)
                // Navigate to main app
                print("Logged in: \(user.displayName)")
            } catch {
                errorMessage = error.localizedDescription
                showError = true
            }
            isLoading = false
        }
    }
}

// Egytour/Services/AuthService.swift
import Foundation

class AuthService {
    static let shared = AuthService()
    private let baseURL = "https://api.egytour.app"
    
    func login(email: String, password: String) async throws -> User {
        let url = URL(string: "\(baseURL)/api/v1/auth/login")!
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        let body = ["email": email, "password": password]
        request.httpBody = try JSONEncoder().encode(body)
        
        let (data, response) = try await URLSession.shared.data(for: request)
        
        guard let httpResponse = response as? HTTPURLResponse,
              httpResponse.statusCode == 200 else {
            throw AuthError.invalidCredentials
        }
        
        let loginResponse = try JSONDecoder().decode(LoginResponse.self, from: data)
        
        // Save tokens
        try saveTokens(accessToken: loginResponse.accessToken, refreshToken: loginResponse.refreshToken)
        
        return loginResponse.user
    }
    
    private func saveTokens(accessToken: String, refreshToken: String) throws {
        // Save to Keychain
        KeychainHelper.save(accessToken, for: "accessToken")
        KeychainHelper.save(refreshToken, for: "refreshToken")
    }
}

enum AuthError: Error {
    case invalidCredentials
    case networkError
}

struct LoginResponse: Codable {
    let user: User
    let accessToken: String
    let refreshToken: String
}

struct User: Codable {
    let id: String
    let email: String
    let displayName: String
}
```

### Android Authentication

```kotlin
// app/src/main/java/com/egytour/features/auth/ui/LoginScreen.kt
package com.egytour.features.auth.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.egytour.core.theme.EgytourColors
import com.egytour.features.auth.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onLoginSuccess()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Logo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalance,
                    contentDescription = "Logo",
                    modifier = Modifier.size(64.dp),
                    tint = EgytourColors.PrimaryGold
                )
                
                Text(
                    text = "EgyTour",
                    style = MaterialTheme.typography.displayMedium,
                    color = EgytourColors.TextPrimary
                )
            }
            
            // Form
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EgytourColors.PrimaryGold,
                        unfocusedBorderColor = EgytourColors.Border,
                        focusedTextColor = EgytourColors.TextPrimary,
                        unfocusedTextColor = EgytourColors.TextPrimary
                    )
                )
                
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EgytourColors.PrimaryGold,
                        unfocusedBorderColor = EgytourColors.Border,
                        focusedTextColor = EgytourColors.TextPrimary,
                        unfocusedTextColor = EgytourColors.TextPrimary
                    )
                )
            }
            
            // Login Button
            Button(
                onClick = { viewModel.login(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EgytourColors.PrimaryGold,
                    contentColor = EgytourColors.BgPrimary
                ),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = EgytourColors.BgPrimary
                    )
                } else {
                    Text("Login", style = MaterialTheme.typography.bodyLarge)
                }
            }
            
            // Register Link
            TextButton(onClick = onNavigateToRegister) {
                Text(
                    "Don't have an account? Sign Up",
                    color = EgytourColors.TextSecondary
                )
            }
        }
    }
    
    // Error Dialog
    if (uiState.error != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Error") },
            text = { Text(uiState.error ?: "") },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text("OK")
                }
            }
        )
    }
}

// app/src/main/java/com/egytour/features/auth/viewmodel/AuthViewModel.kt
package com.egytour.features.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egytour.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val user = authRepository.login(email, password)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = true,
                    user = user
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Login failed"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val user: User? = null,
    val error: String? = null
)
```

---

## 🔧 Day 4-5: Backend API

### Backend Structure

```typescript
// src/app.ts
import Fastify from 'fastify';
import cors from '@fastify/cors';
import jwt from '@fastify/jwt';
import rateLimit from '@fastify/rate-limit';

import authRoutes from './routes/auth';
import sitesRoutes from './routes/sites';
import poisRoutes from './routes/pois';

const app = Fastify({ logger: true });

// Plugins
await app.register(cors, { origin: true });
await app.register(jwt, { secret: process.env.JWT_SECRET! });
await app.register(rateLimit, { max: 100, timeWindow: '1 minute' });

// Routes
await app.register(authRoutes, { prefix: '/api/v1/auth' });
await app.register(sitesRoutes, { prefix: '/api/v1/sites' });
await app.register(poisRoutes, { prefix: '/api/v1/pois' });

export default app;

// src/routes/auth.ts
import { FastifyPluginAsync } from 'fastify';
import bcrypt from 'bcrypt';
import { z } from 'zod';

const loginSchema = z.object({
  email: z.string().email(),
  password: z.string().min(8),
});

const authRoutes: FastifyPluginAsync = async (fastify) => {
  fastify.post('/login', async (request, reply) => {
    const { email, password } = loginSchema.parse(request.body);
    
    // Find user in database
    const user = await db.query.users.findFirst({
      where: eq(users.email, email),
    });
    
    if (!user) {
      return reply.code(401).send({ error: 'Invalid credentials' });
    }
    
    // Verify password
    const isValid = await bcrypt.compare(password, user.passwordHash);
    if (!isValid) {
      return reply.code(401).send({ error: 'Invalid credentials' });
    }
    
    // Generate tokens
    const accessToken = fastify.jwt.sign(
      { userId: user.id },
      { expiresIn: '15m' }
    );
    
    const refreshToken = fastify.jwt.sign(
      { userId: user.id },
      { expiresIn: '7d' }
    );
    
    return {
      user: {
        id: user.id,
        email: user.email,
        displayName: user.displayName,
      },
      accessToken,
      refreshToken,
    };
  });
};

export default authRoutes;
```

---

## 📅 Week 1 Daily Checklist

### Monday
- [ ] Setup iOS Xcode project
- [ ] Setup Android Studio project
- [ ] Setup backend Node.js project
- [ ] Create Git repositories
- [ ] Setup CI/CD (GitHub Actions)

### Tuesday
- [ ] Implement iOS design system (colors, typography)
- [ ] Implement Android design system (colors, typography)
- [ ] Download and add fonts to projects
- [ ] Create reusable UI components

### Wednesday
- [ ] Implement iOS login screen
- [ ] Implement Android login screen
- [ ] Create backend auth endpoints
- [ ] Test authentication flow

### Thursday
- [ ] Implement iOS register screen
- [ ] Implement Android register screen
- [ ] Setup database (Supabase)
- [ ] Create users table

### Friday
- [ ] Code review and testing
- [ ] Fix bugs
- [ ] Deploy backend to Railway
- [ ] Team demo and retrospective

---

## 🚀 Quick Start Commands

### iOS Development
```bash
# Open Xcode project
open Egytour.xcodeproj

# Run on simulator
xcodebuild -scheme Egytour -destination 'platform=iOS Simulator,name=iPhone 15 Pro'

# Run tests
xcodebuild test -scheme Egytour
```

### Android Development
```bash
# Build project
./gradlew build

# Run on emulator
./gradlew installDebug

# Run tests
./gradlew test
```

### Backend Development
```bash
# Install dependencies
npm install

# Run development server
npm run dev

# Run migrations
npm run db:migrate

# Run tests
npm test
```

---

## ✅ Success Criteria for Week 1

By end of Week 1, you should have:
- ✅ iOS project with login/register screens
- ✅ Android project with login/register screens
- ✅ Backend API with authentication
- ✅ Design system implemented on both platforms
- ✅ CI/CD pipelines configured
- ✅ Team onboarded and productive

---

## 📞 Next Steps

**Ready to start?** Here's what to do now:

1. **Run the setup scripts** above
2. **Hire your team** (2 iOS, 2 Android, 1 backend, 1 designer)
3. **Setup infrastructure** (Apple Developer, Google Play, Supabase, Railway)
4. **Begin Day 1** on Monday!

I'm here to help with any questions or provide more detailed code for specific features!
