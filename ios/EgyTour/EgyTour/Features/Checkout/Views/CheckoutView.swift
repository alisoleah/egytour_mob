import SwiftUI

enum PaymentMethod: String, CaseIterable, Identifiable {
    case creditCard = "Credit Card"
    case applePay = "Apple Pay"
    case mobileWallet = "Mobile Wallet"
    
    var id: String { rawValue }
    
    var subtitle: String {
        switch self {
        case .creditCard: return "Visa, Mastercard, Meeza"
        case .applePay: return "Fast & Secure Touch ID / Face ID"
        case .mobileWallet: return "Vodafone Cash, Etisalat, Orange"
        }
    }
    
    var iconName: String {
        switch self {
        case .creditCard: return "creditcard.fill"
        case .applePay: return "applelogo"
        case .mobileWallet: return "wallet.pass.fill"
        }
    }
}

struct CheckoutView: View {
    var passTitle: String = "Giza Necropolis & Plateau"
    var passSubtitle: String = "Full Monument & Audio Pass"
    var priceEgp: Double = 450.00
    var onDismiss: () -> Void = {}
    var onPaymentSuccess: () -> Void = {}
    
    @State private var selectedMethod: PaymentMethod = .creditCard
    @State private var cardNumber = "4111 2222 3333 4444"
    @State private var expiryDate = "12/28"
    @State private var cvv = "888"
    @State private var cardholderName = "Omar Sherif"
    @State private var isProcessing = false
    
    var body: some View {
        NavigationView {
            ZStack(alignment: .bottom) {
                Color(hex: "1A1A1A").ignoresSafeArea()
                
                ScrollView {
                    VStack(spacing: 20) {
                        // Order Summary Card
                        orderSummaryCard
                        
                        // Payment Method Selector
                        paymentMethodsSection
                        
                        // Card Details Form
                        if selectedMethod == .creditCard {
                            cardDetailsSection
                        }
                        
                        Spacer().frame(height: 120)
                    }
                    .padding(16)
                }
                
                // Sticky Checkout CTA
                stickyPaymentFooter
            }
            .navigationTitle("Checkout")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button(action: onDismiss) {
                        Image(systemName: "xmark")
                            .foregroundColor(.white)
                            .font(.system(size: 14, weight: .semibold))
                    }
                }
            }
        }
    }
    
    private var orderSummaryCard: some View {
        HStack(spacing: 14) {
            VStack(alignment: .leading, spacing: 4) {
                Text("ORDER SUMMARY")
                    .font(.system(size: 10, weight: .bold))
                    .foregroundColor(.primaryGold)
                    .tracking(1.2)
                
                Text(passTitle)
                    .font(.system(size: 17, weight: .bold))
                    .foregroundColor(.white)
                
                Text(passSubtitle)
                    .font(.system(size: 13))
                    .foregroundColor(.white.opacity(0.7))
                
                Spacer().frame(height: 4)
                
                Text("EGP \(String(format: "%.2f", priceEgp))")
                    .font(.system(size: 18, weight: .bold))
                    .foregroundColor(.primaryGold)
            }
            
            Spacer()
            
            Image("great_pyramid")
                .resizable()
                .scaledToFill()
                .frame(width: 76, height: 76)
                .cornerRadius(12)
                .clipped()
        }
        .padding(16)
        .background(Color(hex: "262626"))
        .cornerRadius(16)
        .overlay(
            RoundedRectangle(cornerRadius: 16)
                .stroke(Color.white.opacity(0.1), lineWidth: 1)
        )
    }
    
    private var paymentMethodsSection: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Payment Method")
                .font(.system(size: 17, weight: .bold))
                .foregroundColor(.white)
            
            VStack(spacing: 10) {
                ForEach(PaymentMethod.allCases) { method in
                    Button(action: { selectedMethod = method }) {
                        HStack(spacing: 14) {
                            Image(systemName: method.iconName)
                                .font(.system(size: 22))
                                .foregroundColor(selectedMethod == method ? .primaryGold : .white.opacity(0.7))
                                .frame(width: 30)
                            
                            VStack(alignment: .leading, spacing: 2) {
                                Text(method.rawValue)
                                    .font(.system(size: 15, weight: .semibold))
                                    .foregroundColor(.white)
                                
                                Text(method.subtitle)
                                    .font(.system(size: 12))
                                    .foregroundColor(.white.opacity(0.6))
                            }
                            
                            Spacer()
                            
                            ZStack {
                                Circle()
                                    .stroke(selectedMethod == method ? Color.primaryGold : Color.white.opacity(0.3), lineWidth: 1.5)
                                    .frame(width: 20, height: 20)
                                
                                if selectedMethod == method {
                                    Circle()
                                        .fill(Color.primaryGold)
                                        .frame(width: 12, height: 12)
                                }
                            }
                        }
                        .padding(14)
                        .background(Color(hex: "262626"))
                        .cornerRadius(12)
                        .overlay(
                            RoundedRectangle(cornerRadius: 12)
                                .stroke(selectedMethod == method ? Color.primaryGold : Color.white.opacity(0.08), lineWidth: selectedMethod == method ? 1.5 : 1)
                        )
                    }
                    .buttonStyle(PlainButtonStyle())
                }
            }
        }
    }
    
    private var cardDetailsSection: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack {
                Text("Card Details")
                    .font(.system(size: 17, weight: .bold))
                    .foregroundColor(.white)
                
                Spacer()
                
                HStack(spacing: 4) {
                    Image(systemName: "lock.fill")
                        .font(.system(size: 10))
                        .foregroundColor(Color(hex: "4CAF50"))
                    Text("SSL Secure")
                        .font(.system(size: 11, weight: .medium))
                        .foregroundColor(Color(hex: "4CAF50"))
                }
                .padding(.horizontal, 8)
                .padding(.vertical, 4)
                .background(Color(hex: "4CAF50").opacity(0.15))
                .cornerRadius(4)
            }
            
            VStack(spacing: 12) {
                // Card Number
                VStack(alignment: .leading, spacing: 6) {
                    Text("Card Number")
                        .font(.system(size: 12, weight: .medium))
                        .foregroundColor(.white.opacity(0.7))
                    
                    HStack {
                        TextField("0000 0000 0000 0000", text: $cardNumber)
                            .foregroundColor(.white)
                        Image(systemName: "creditcard")
                            .foregroundColor(.white.opacity(0.5))
                    }
                    .padding(14)
                    .background(Color(hex: "1F1F1F"))
                    .cornerRadius(10)
                    .overlay(RoundedRectangle(cornerRadius: 10).stroke(Color.white.opacity(0.1), lineWidth: 1))
                }
                
                HStack(spacing: 12) {
                    // Expiry
                    VStack(alignment: .leading, spacing: 6) {
                        Text("Expiry (MM/YY)")
                            .font(.system(size: 12, weight: .medium))
                            .foregroundColor(.white.opacity(0.7))
                        
                        TextField("MM/YY", text: $expiryDate)
                            .foregroundColor(.white)
                            .padding(14)
                            .background(Color(hex: "1F1F1F"))
                            .cornerRadius(10)
                            .overlay(RoundedRectangle(cornerRadius: 10).stroke(Color.white.opacity(0.1), lineWidth: 1))
                    }
                    
                    // CVV
                    VStack(alignment: .leading, spacing: 6) {
                        Text("CVV")
                            .font(.system(size: 12, weight: .medium))
                            .foregroundColor(.white.opacity(0.7))
                        
                        HStack {
                            SecureField("123", text: $cvv)
                                .foregroundColor(.white)
                            Image(systemName: "lock")
                                .foregroundColor(.white.opacity(0.5))
                        }
                        .padding(14)
                        .background(Color(hex: "1F1F1F"))
                        .cornerRadius(10)
                        .overlay(RoundedRectangle(cornerRadius: 10).stroke(Color.white.opacity(0.1), lineWidth: 1))
                    }
                }
                
                // Name
                VStack(alignment: .leading, spacing: 6) {
                    Text("Cardholder Name")
                        .font(.system(size: 12, weight: .medium))
                        .foregroundColor(.white.opacity(0.7))
                    
                    TextField("e.g. Ahmed Hassan", text: $cardholderName)
                        .foregroundColor(.white)
                        .padding(14)
                        .background(Color(hex: "1F1F1F"))
                        .cornerRadius(10)
                        .overlay(RoundedRectangle(cornerRadius: 10).stroke(Color.white.opacity(0.1), lineWidth: 1))
                }
            }
        }
    }
    
    private var stickyPaymentFooter: some View {
        VStack(spacing: 8) {
            Button(action: {
                isProcessing = true
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.8) {
                    isProcessing = false
                    onPaymentSuccess()
                }
            }) {
                HStack {
                    if isProcessing {
                        ProgressView()
                            .progressViewStyle(CircularProgressViewStyle(tint: .black))
                    } else {
                        Text("Pay EGP \(String(format: "%.2f", priceEgp))")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(.black)
                        
                        Image(systemName: "arrow.right")
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(.black)
                    }
                }
                .frame(maxWidth: .infinity)
                .frame(height: 52)
                .background(Color.primaryGold)
                .cornerRadius(14)
            }
            .disabled(isProcessing)
            
            Text("By clicking Pay, you agree to our Terms of Service.")
                .font(.system(size: 11))
                .foregroundColor(.white.opacity(0.5))
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 14)
        .background(Color(hex: "1A1A1A").edgesIgnoringSafeArea(.bottom))
        .overlay(
            Rectangle()
                .frame(height: 1)
                .foregroundColor(Color.white.opacity(0.08)),
            alignment: .top
        )
    }
}
