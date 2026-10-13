import SwiftUI

struct LoginView: View {
    @State private var email = ""
    @State private var password = ""
    @State private var isLoading = false
    @State private var showError = false
    
    // In a real app, this would use an AuthService
    var onLoginSuccess: () -> Void
    
    var body: some View {
        ZStack {
            Color.bgPrimary.edgesIgnoringSafeArea(.all)
            
            VStack(spacing: 24) {
                // Logo or Branding
                VStack(spacing: 8) {
                    Image(systemName: "pyramid.fill") // Placeholder
                        .font(.system(size: 80))
                        .foregroundColor(.primaryGold)
                    
                    Text("EgyTour")
                        .font(.custom("PlayfairDisplay-Bold", size: 40))
                        .foregroundColor(.textPrimary)
                    
                    Text("Your Pocket Guide to the Pharaohs")
                        .font(.bodyMedium)
                        .foregroundColor(.textSecondary)
                }
                .padding(.bottom, 40)
                
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
                
                Button(action: login) {
                    HStack {
                        if isLoading {
                            ProgressView()
                                .progressViewStyle(CircularProgressViewStyle(tint: .white))
                        } else {
                            Text("Sign In")
                                .fontWeight(.semibold)
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 56)
                    .background(Color.primaryGold)
                    .foregroundColor(.bgPrimary)
                    .cornerRadius(12)
                }
                .disabled(isLoading || email.isEmpty || password.isEmpty)
                
                // Demo Mode
                Button(action: onLoginSuccess) {
                    Text("Skip for Demo")
                        .font(.caption)
                        .foregroundColor(.textTertiary)
                }
            }
            .padding(24)
        }
    }
    
    func login() {
        isLoading = true
        // Simulate API call
        DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
            isLoading = false
            onLoginSuccess()
        }
    }
}

struct EgytourTextFieldStyle: TextFieldStyle {
    func _body(configuration: TextField<Self._Label>) -> some View {
        configuration
            .padding()
            .background(Color.bgSecondary)
            .cornerRadius(12)
            .foregroundColor(.textPrimary)
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .stroke(Color.border, lineWidth: 1)
            )
    }
}
