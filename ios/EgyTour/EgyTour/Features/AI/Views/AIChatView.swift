import SwiftUI

struct AIChatView: View {
    @StateObject private var viewModel = ChatViewModel()
    @FocusState private var isInputFocused: Bool
    
    var body: some View {
        VStack(spacing: 0) {
            // Header Bar
            ChatHeaderView()
            
            // Suggested Questions Quick Bar
            if !viewModel.suggestedPrompts.isEmpty {
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 8) {
                        ForEach(viewModel.suggestedPrompts, id: \.self) { prompt in
                            Button(action: {
                                viewModel.sendMessage(customQuery: prompt)
                            }) {
                                Text(prompt)
                                    .font(.caption)
                                    .fontWeight(.medium)
                                    .foregroundColor(.white)
                                    .padding(.horizontal, 14)
                                    .padding(.vertical, 8)
                                    .background(Color.white.opacity(0.1))
                                    .clipShape(Capsule())
                                    .overlay(
                                        Capsule().stroke(Color.primaryGold.opacity(0.4), lineWidth: 1)
                                    )
                            }
                        }
                    }
                    .padding(.horizontal, 16)
                    .padding(.vertical, 8)
                }
                .background(Color(.secondarySystemBackground))
            }
            
            // Messages Feed
            ScrollViewReader { proxy in
                ScrollView {
                    LazyVStack(spacing: 16) {
                        ForEach(viewModel.messages) { message in
                            ChatMessageRow(message: message, onSelectFollowUp: { followUp in
                                viewModel.sendMessage(customQuery: followUp)
                            })
                            .id(message.id)
                        }
                        
                        if viewModel.isLoading {
                            TypingIndicatorRow()
                                .id("loading")
                        }
                    }
                    .padding(.horizontal, 16)
                    .padding(.vertical, 16)
                }
                .onChange(of: viewModel.messages.count) { _ in
                    if let lastMsg = viewModel.messages.last {
                        withAnimation {
                            proxy.scrollTo(lastMsg.id, anchor: .bottom)
                        }
                    }
                }
                .onChange(of: viewModel.isLoading) { loading in
                    if loading {
                        withAnimation {
                            proxy.scrollTo("loading", anchor: .bottom)
                        }
                    }
                }
            }
            
            // Bottom Input Bar
            ChatInputBar(
                text: $viewModel.inputText,
                isLoading: viewModel.isLoading,
                onSend: {
                    viewModel.sendMessage()
                }
            )
        }
        .background(Color(.systemBackground).edgesIgnoringSafeArea(.all))
    }
}

// MARK: - Header
struct ChatHeaderView: View {
    var body: some View {
        HStack(spacing: 12) {
            ZStack(alignment: .bottomTrailing) {
                Circle()
                    .fill(Color.primaryGold.opacity(0.2))
                    .frame(width: 44, height: 44)
                    .overlay(
                        Text("𓂀")
                            .font(.title2)
                            .foregroundColor(.primaryGold)
                    )
                    .overlay(
                        Circle().stroke(Color.primaryGold, lineWidth: 1.5)
                    )
                
                Circle()
                    .fill(Color.green)
                    .frame(width: 12, height: 12)
                    .overlay(
                        Circle().stroke(Color(.systemBackground), lineWidth: 2)
                    )
            }
            
            VStack(alignment: .leading, spacing: 2) {
                HStack(spacing: 6) {
                    Text("Dr. Zahi")
                        .font(.headline)
                        .fontWeight(.bold)
                    
                    Image(systemName: "checkmark.seal.fill")
                        .font(.caption)
                        .foregroundColor(.primaryGold)
                }
                
                Text("Chief Egyptologist Guide • Verified")
                    .font(.caption2)
                    .foregroundColor(.secondary)
            }
            
            Spacer()
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
        .background(Color(.systemBackground))
        .overlay(
            Divider().opacity(0.5), alignment: .bottom
        )
    }
}

// MARK: - Message Row
struct ChatMessageRow: View {
    let message: ChatMessage
    var onSelectFollowUp: (String) -> Void
    
    var body: some View {
        HStack(alignment: .top, spacing: 10) {
            if message.isUser {
                Spacer(minLength: 40)
                
                Text(message.text)
                    .font(.body)
                    .foregroundColor(.white)
                    .padding(.horizontal, 16)
                    .padding(.vertical, 12)
                    .background(Color.primaryGold)
                    .clipShape(RoundedRectangle(cornerRadius: 18))
            } else {
                VStack(alignment: .leading, spacing: 10) {
                    Text(message.text)
                        .font(.body)
                        .foregroundColor(.primary)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 12)
                        .background(Color(.secondarySystemBackground))
                        .clipShape(RoundedRectangle(cornerRadius: 18))
                    
                    // Sources Citation Pill
                    if !message.sources.isEmpty {
                        VStack(alignment: .leading, spacing: 4) {
                            ForEach(message.sources) { source in
                                HStack(spacing: 6) {
                                    Image(systemName: "scroll.fill")
                                        .font(.caption2)
                                        .foregroundColor(.primaryGold)
                                    Text("Source: \(source.title)")
                                        .font(.caption2)
                                        .foregroundColor(.secondary)
                                }
                            }
                        }
                        .padding(.leading, 8)
                    }
                    
                    // Interactive Suggested Follow-Up Chips
                    if !message.suggestedQuestions.isEmpty {
                        VStack(alignment: .leading, spacing: 6) {
                            Text("Suggested Follow-Ups:")
                                .font(.caption2)
                                .fontWeight(.semibold)
                                .foregroundColor(.secondary)
                            
                            ForEach(message.suggestedQuestions, id: \.self) { question in
                                Button(action: {
                                    onSelectFollowUp(question)
                                }) {
                                    HStack {
                                        Image(systemName: "sparkles")
                                            .font(.caption2)
                                            .foregroundColor(.primaryGold)
                                        Text(question)
                                            .font(.caption)
                                            .foregroundColor(.primary)
                                            .multilineTextAlignment(.leading)
                                        Spacer()
                                        Image(systemName: "arrow.up.right")
                                            .font(.caption2)
                                            .foregroundColor(.secondary)
                                    }
                                    .padding(.horizontal, 12)
                                    .padding(.vertical, 8)
                                    .background(Color(.tertiarySystemBackground))
                                    .cornerRadius(10)
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 10)
                                            .stroke(Color.primaryGold.opacity(0.3), lineWidth: 1)
                                    )
                                }
                            }
                        }
                        .padding(.top, 4)
                        .padding(.leading, 4)
                    }
                }
                
                Spacer(minLength: 40)
            }
        }
    }
}

// MARK: - Typing Indicator
struct TypingIndicatorRow: View {
    @State private var phase = 0.0
    
    var body: some View {
        HStack {
            HStack(spacing: 6) {
                ForEach(0..<3) { index in
                    Circle()
                        .fill(Color.primaryGold)
                        .frame(width: 8, height: 8)
                        .scaleEffect(1.0 + (sin(phase + Double(index) * 0.8) * 0.3))
                }
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 12)
            .background(Color(.secondarySystemBackground))
            .clipShape(Capsule())
            .onAppear {
                withAnimation(.easeInOut(duration: 0.6).repeatForever(autoreverses: true)) {
                    phase = .pi * 2
                }
            }
            
            Spacer()
        }
    }
}

// MARK: - Bottom Input Bar
struct ChatInputBar: View {
    @Binding var text: String
    let isLoading: Bool
    var onSend: () -> Void
    
    var body: some View {
        HStack(spacing: 12) {
            TextField("Ask about pyramids, pharaohs, gods...", text: $text)
                .padding(.horizontal, 16)
                .padding(.vertical, 12)
                .background(Color(.secondarySystemBackground))
                .cornerRadius(22)
                .onSubmit(onSend)
            
            Button(action: onSend) {
                ZStack {
                    Circle()
                        .fill(Color.primaryGold)
                        .frame(width: 44, height: 44)
                    
                    if isLoading {
                        ProgressView()
                            .progressViewStyle(CircularProgressViewStyle(tint: .white))
                    } else {
                        Image(systemName: "paperplane.fill")
                            .font(.system(size: 18))
                            .foregroundColor(.white)
                    }
                }
            }
            .disabled(text.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty || isLoading)
            .opacity(text.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty ? 0.6 : 1.0)
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 10)
        .background(Color(.systemBackground))
        .overlay(
            Divider().opacity(0.5), alignment: .top
        )
    }
}
