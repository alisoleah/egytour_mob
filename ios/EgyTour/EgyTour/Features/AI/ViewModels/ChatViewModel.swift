import Foundation
import Combine

class ChatViewModel: ObservableObject {
    @Published var messages: [ChatMessage] = []
    @Published var inputText: String = ""
    @Published var isLoading: Bool = false
    @Published var suggestedPrompts: [String] = [
        "Who actually built the Great Pyramid of Giza?",
        "What is the true story of the Sphinx's missing nose?",
        "What mysteries were found in King Tut's tomb?",
        "How did the Opet Festival connect Luxor and Karnak?"
    ]
    
    private let apiEndpoint = "http://localhost:3000/api/v1/ai/ask"
    private var cancellables = Set<AnyCancellable>()
    
    init() {
        seedInitialGreeting()
    }
    
    private func seedInitialGreeting() {
        let welcome = ChatMessage(
            text: "Ahlan wa sahlan! I am Dr. Zahi, your dedicated Chief Egyptologist guide. Whether you're standing before the Great Pyramid or curious about ancient hieroglyphs, ask me anything!",
            isUser: false,
            sources: [
                ChatSource(title: "EgyTour Verified Egyptologist Knowledge Base", period: "All Dynasties", confidence: 0.99)
            ],
            suggestedQuestions: [
                "Were the pyramids built by enslaved workers?",
                "What is inside the King's Chamber?",
                "What does the Dream Stele say?"
            ]
        )
        self.messages.append(welcome)
    }
    
    func sendMessage(customQuery: String? = nil, poiId: String? = nil) {
        let question = (customQuery ?? inputText).trimmingCharacters(in: .whitespacesAndNewlines)
        guard !question.isEmpty else { return }
        
        // 1. Append user message
        let userMsg = ChatMessage(text: question, isUser: true)
        messages.append(userMsg)
        
        if customQuery == nil {
            inputText = ""
        }
        
        isLoading = true
        
        // 2. Query Fastify Backend API
        guard let url = URL(string: apiEndpoint) else {
            fallbackOfflineAnswer(for: question, poiId: poiId)
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.timeoutInterval = 10
        
        let body: [String: Any] = [
            "question": question,
            "poiId": poiId as Any
        ]
        
        do {
            request.httpBody = try JSONSerialization.data(withJSONObject: body)
        } catch {
            fallbackOfflineAnswer(for: question, poiId: poiId)
            return
        }
        
        URLSession.shared.dataTask(with: request) { [weak self] data, response, error in
            DispatchQueue.main.async {
                guard let self = self else { return }
                self.isLoading = false
                
                if let data = data,
                   let json = try? JSONSerialization.jsonObject(with: data) as? [String: Any],
                   let answer = json["answer"] as? String {
                    
                    var sources: [ChatSource] = []
                    if let rawSources = json["sources"] as? [[String: Any]] {
                        for s in rawSources {
                            let title = s["title"] as? String ?? "Historical Archive"
                            let period = s["period"] as? String
                            let conf = s["confidence"] as? Double
                            sources.append(ChatSource(title: title, period: period, confidence: conf))
                        }
                    }
                    
                    let followUps = json["suggestedQuestions"] as? [String] ?? []
                    let relatedPoi = json["relatedPoiId"] as? String
                    
                    let aiMsg = ChatMessage(
                        text: answer,
                        isUser: false,
                        sources: sources,
                        suggestedQuestions: followUps,
                        relatedPoiId: relatedPoi
                    )
                    self.messages.append(aiMsg)
                } else {
                    // Fallback to intelligent offline Egyptologist response
                    self.fallbackOfflineAnswer(for: question, poiId: poiId)
                }
            }
        }.resume()
    }
    
    private func fallbackOfflineAnswer(for question: String, poiId: String?) {
        self.isLoading = false
        let qLower = question.toLowerCase()
        
        var answerText = ""
        var sources: [ChatSource] = []
        var followUps: [String] = []
        var relatedPoi: String? = nil
        
        if qLower.contains("worker") || qLower.contains("slave") || qLower.contains("built") || qLower.contains("pyramid") || qLower.contains("khufu") {
            answerText = "The Great Pyramid of Khufu (c. 2560 BCE) was built by an organized workforce of roughly 20,000–30,000 skilled Egyptian craftsmen and seasonal workers, not enslaved people. \n\nExcavations at the Giza Workers' Village revealed bakeries, medical treatment for broken bones, and nearby honorable tombs for workers who died during construction."
            sources = [
                ChatSource(title: "Excavations at the Giza Workers' Village (Hawass & Lehner)", period: "4th Dynasty", confidence: 0.98),
                ChatSource(title: "Supreme Council of Antiquities Field Records", period: "Old Kingdom", confidence: 0.95)
            ]
            followUps = [
                "What did the pyramid builders eat?",
                "How were the 50-ton granite blocks moved?",
                "What is inside the Grand Gallery?"
            ]
            relatedPoi = "giza-1"
        } else if qLower.contains("sphinx") || qLower.contains("nose") {
            answerText = "Carved out of natural limestone bedrock under Pharaoh Khafre, the Great Sphinx combines a lion's strength with royal human intellect. \n\nThe missing nose was not shot off by Napoleon! Historical sketches from 1737 prove it was already gone. Historical records by 15th-century Arab historian al-Maqrizi state it was chiselled away in 1378 CE by Muhammad Sa'im al-Dahr."
            sources = [
                ChatSource(title: "The Dream Stele of Thutmose IV", period: "18th Dynasty", confidence: 0.96),
                ChatSource(title: "Al-Maqrizi's Historical Topography (1442 CE)", period: "Islamic Cairo Era", confidence: 0.93)
            ]
            followUps = [
                "What is written on the Dream Stele?",
                "Why is the body a lion?",
                "Are there secret chambers underneath?"
            ]
            relatedPoi = "giza-2"
        } else if qLower.contains("tut") || qLower.contains("tomb") || qLower.contains("carter") || qLower.contains("curse") {
            answerText = "Tomb KV62 was uncovered nearly intact on November 4, 1922, by Howard Carter. It held 5,398 treasures, including Tutankhamun's iconic 11-kg solid gold mask.\n\nModern scientific CT scans revealed young Tut died around age 19 from a severe compound leg fracture combined with chronic malaria, dispelling popular murder theories."
            sources = [
                ChatSource(title: "Carter Expedition Field Logs (1922)", period: "New Kingdom", confidence: 0.97),
                ChatSource(title: "Egyptian Mummy Project DNA Analysis", period: "Modern Science", confidence: 0.95)
            ]
            followUps = [
                "Where is the gold mask displayed now?",
                "Was there really a curse of the pharaohs?",
                "Who were King Tut's parents?"
            ]
        } else {
            answerText = "Ancient Egyptian civilization spanned over three millennia of remarkable achievement in astronomy, medicine, engineering, and monumental architecture. Across the Old, Middle, and New Kingdoms, monuments like Giza, Karnak, and Luxor reflected their profound devotion to cosmic order (Ma'at) and eternity."
            sources = [
                ChatSource(title: "Ministry of Tourism and Antiquities Archive", period: "Pharaonic Eras", confidence: 0.94)
            ]
            followUps = [
                "Who was Egypt's greatest female pharaoh?",
                "What was the Opet Festival in Luxor?",
                "How were hieroglyphs deciphered?"
            ]
        }
        
        let aiMsg = ChatMessage(
            text: answerText,
            isUser: false,
            sources: sources,
            suggestedQuestions: followUps,
            relatedPoiId: relatedPoi
        )
        messages.append(aiMsg)
    }
}

extension String {
    func toLowerCase() -> String {
        return self.lowercased()
    }
}
