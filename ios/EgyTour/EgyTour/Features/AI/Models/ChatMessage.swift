import Foundation

struct ChatSource: Identifiable, Codable, Equatable {
    var id: String { title }
    let title: String
    let period: String?
    let confidence: Double?
}

struct ChatMessage: Identifiable, Equatable {
    let id: UUID
    let text: String
    let isUser: Bool
    let timestamp: Date
    let sources: [ChatSource]
    let suggestedQuestions: [String]
    let relatedPoiId: String?
    
    init(
        id: UUID = UUID(),
        text: String,
        isUser: Bool,
        timestamp: Date = Date(),
        sources: [ChatSource] = [],
        suggestedQuestions: [String] = [],
        relatedPoiId: String? = nil
    ) {
        self.id = id
        self.text = text
        self.isUser = isUser
        self.timestamp = timestamp
        self.sources = sources
        self.suggestedQuestions = suggestedQuestions
        self.relatedPoiId = relatedPoiId
    }
}
