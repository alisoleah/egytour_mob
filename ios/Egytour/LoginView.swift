import SwiftUI

struct LoginView: View {
    @State private var email = ""
    @State private var password = ""
    @State private var passwordVisible = false

    var body: some View {
        ZStack {
            Circle()
                .fill(Theme.brandOrange.opacity(0.1))
                .frame(width: 400, height: 400)
                .offset(x: -100, y: -100)
                .blur(radius: 100)
            Circle()
                .fill(Theme.brandBlack.opacity(0.1))
                .frame(width: 300, height: 300)
                .offset(x: 200, y: 100)
                .blur(radius: 100)

            VStack {
                HStack {
                    Button(action: { /* Handle back press */ }) {
                    Image(systemName: "arrow.backward")
                        .font(.system(size: 20, weight: .semibold))
                        .foregroundColor(Theme.textMain)
                }
                .frame(width: 44, height: 44)
                .background(Color.white)
                .cornerRadius(22)
                .shadow(color: Color.black.opacity(0.1), radius: 5, x: 0, y: 2)

                Spacer()

                Text(NSLocalizedString("app_name", comment: "App Name"))
                    .font(.playfairDisplay(size: 24, weight: .bold))
                    .foregroundColor(Theme.brandOrange)

                Spacer()

                Spacer().frame(width: 44)
            }
            .padding(.horizontal)

            Spacer().frame(height: 48)

            VStack(alignment: .leading, spacing: 8) {
                Text(NSLocalizedString("explore", comment: "Explore"))
                    .font(.playfairDisplay(size: 36, weight: .black))
                Text(NSLocalizedString("the_unseen", comment: "The Unseen"))
                    .font(.playfairDisplay(size: 36, weight: .black))
                    .foregroundColor(Theme.brandOrange)
                Text(NSLocalizedString("unlock_exclusive_tours", comment: "Unlock exclusive tours"))
                    .font(.inter(size: 16, weight: .medium))
                    .foregroundColor(Theme.textSub)
            }
            .padding(.horizontal)

            Spacer().frame(height: 48)

            VStack(spacing: 24) {
                // Card Content
                VStack(spacing: 24) {
                    HStack {
                        Button(action: {}) {
                            Text(NSLocalizedString("log_in", comment: "Log In"))
                                .font(.inter(size: 14, weight: .bold))
                                .foregroundColor(Theme.brandOrange)
                                .padding(.vertical, 12)
                                .frame(maxWidth: .infinity)
                                .border(b: .brandOrange, width: 3)
                        }
                        Button(action: {}) {
                            Text(NSLocalizedString("register", comment: "Register"))
                                .font(.inter(size: 14, weight: .bold))
                                .foregroundColor(Theme.textSub)
                                .padding(.vertical, 12)
                                .frame(maxWidth: .infinity)
                        }
                    }
                    .background(
                        VStack {
                            Spacer()
                            Rectangle()
                                .frame(height: 1)
                                .foregroundColor(Theme.brandBorder)
                        }
                    )

                    VStack(spacing: 16) {
                        TextField(NSLocalizedString("email_address", comment: "Email Address"), text: $email)
                            .font(.inter(size: 16, weight: .medium))
                            .padding()
                            .background(Theme.brandBg)
                            .cornerRadius(12)
                            .overlay(
                                RoundedRectangle(cornerRadius: 12)
                                    .stroke(Theme.brandBorder, lineWidth: 1)
                            )
                            .keyboardType(.emailAddress)

                        HStack {
                            if passwordVisible {
                                TextField(NSLocalizedString("password", comment: "Password"), text: $password)
                                    .font(.inter(size: 16, weight: .medium))
                            } else {
                                SecureField(NSLocalizedString("password", comment: "Password"), text: $password)
                                    .font(.inter(size: 16, weight: .medium))
                            }
                            Button(action: { passwordVisible.toggle() }) {
                                Image(systemName: passwordVisible ? "eye.slash" : "eye")
                                    .foregroundColor(Theme.textSub)
                            }
                        }
                        .padding()
                        .background(Theme.brandBg)
                        .cornerRadius(12)
                        .overlay(
                            RoundedRectangle(cornerRadius: 12)
                                .stroke(Theme.brandBorder, lineWidth: 1)
                        )
                    }

                    Button(action: {}) {
                        Text(NSLocalizedString("forgot_password", comment: "Forgot Password?"))
                            .font(.inter(size: 14, weight: .semibold))
                            .foregroundColor(Theme.textSub)
                    }
                    .frame(maxWidth: .infinity, alignment: .trailing)

                    Button(action: { /* Handle login */ }) {
                        HStack {
                            Text(NSLocalizedString("access_account", comment: "Access Account"))
                                .font(.inter(size: 14, weight: .bold))
                            Image(systemName: "arrow.forward")
                        }
                        .foregroundColor(.white)
                        .padding()
                        .frame(maxWidth: .infinity)
                        .background(
                            LinearGradient(
                                gradient: Gradient(colors: [Theme.brandOrange, Theme.brandDarkOrange]),
                                startPoint: .leading,
                                endPoint: .trailing
                            )
                        )
                        .cornerRadius(16)
                        .shadow(color: Theme.brandOrange.opacity(0.3), radius: 10, y: 5)
                    }

                    HStack {
                        Rectangle().frame(height: 1).foregroundColor(Theme.brandBorder)
                        Text(NSLocalizedString("or_continue_with", comment: "Or continue with"))
                            .font(.inter(size: 12, weight: .bold))
                            .foregroundColor(Theme.textSub)
                            .padding(.horizontal)
                        Rectangle().frame(height: 1).foregroundColor(Theme.brandBorder)
                    }

                    HStack(spacing: 16) {
                        Button(action: { /* Handle Google login */ }) {
                            HStack {
                                GoogleLogo()
                                    .frame(width: 20, height: 20)
                                Text(NSLocalizedString("google", comment: "Google"))
                                    .font(.inter(size: 14, weight: .bold))
                                    .foregroundColor(Theme.textMain)
                            }
                            .padding()
                            .frame(maxWidth: .infinity)
                            .background(Color.white)
                            .cornerRadius(16)
                            .overlay(
                                RoundedRectangle(cornerRadius: 16)
                                    .stroke(Theme.brandBorder, lineWidth: 1)
                            )
                        }
                        Button(action: { /* Handle Apple login */ }) {
                            HStack {
                                Image(systemName: "applelogo")
                                    .font(.system(size: 20, weight: .bold))
                                    .foregroundColor(Theme.textMain)
                                Text(NSLocalizedString("apple", comment: "Apple"))
                                    .font(.inter(size: 14, weight: .bold))
                                    .foregroundColor(Theme.textMain)
                            }
                            .padding()
                            .frame(maxWidth: .infinity)
                            .background(Color.white)
                            .cornerRadius(16)
                            .overlay(
                                RoundedRectangle(cornerRadius: 16)
                                    .stroke(Theme.brandBorder, lineWidth: 1)
                            )
                        }
                    }
                }
                .padding()
                .background(Color.white)
                .cornerRadius(24)
                .shadow(color: Color.black.opacity(0.08), radius: 20, y: 10)
            }
            .padding(.horizontal)

            Spacer()

            Button(action: { /* Handle create account */ }) {
                HStack(spacing: 4) {
                    Text(NSLocalizedString("not_a_member", comment: "Not a member?"))
                        .font(.inter(size: 14, weight: .medium))
                        .foregroundColor(Theme.textSub)
                    Text(NSLocalizedString("create_account", comment: "Create Account"))
                        .font(.inter(size: 14, weight: .bold))
                        .foregroundColor(Theme.brandOrange)
                }
            }
        }
        .background(Theme.brandBg)
        .edgesIgnoringSafeArea(.all)
    }
}

extension View {
    func border(b: Color, width: CGFloat) -> some View {
        self.overlay(VStack {
            Spacer()
            Rectangle().frame(height: width).foregroundColor(b)
        })
    }
}
