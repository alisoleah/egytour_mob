import { FastifyPluginAsync } from 'fastify';
import { z } from 'zod';
import { RAGService } from '../services/rag.service';

const askSchema = z.object({
    question: z.string().min(2, 'Question must be at least 2 characters').max(1000),
    poiId: z.string().optional(),
    conversationId: z.string().optional(),
    language: z.string().optional().default('en'),
});

const aiRoutes: FastifyPluginAsync = async (fastify) => {
    const ragService = new RAGService();

    // Ask Egyptologist AI Q&A Endpoint
    fastify.post('/ask', async (request, reply) => {
        try {
            const body = askSchema.parse(request.body);
            const result = await ragService.askQuestion(body.question, body.poiId);

            return reply.status(200).send({
                success: true,
                answer: result.answer,
                sources: result.sources,
                confidence: result.confidence,
                suggestedQuestions: result.suggestedQuestions,
                relatedPoiId: result.relatedPoiId,
                timestamp: new Date().toISOString(),
            });
        } catch (error) {
            if (error instanceof z.ZodError) {
                return reply.status(400).send({
                    success: false,
                    error: 'Validation Error',
                    details: error.errors,
                });
            }

            request.log.error(error);
            return reply.status(500).send({
                success: false,
                error: 'Failed to process question with Egyptologist AI',
            });
        }
    });

    // Suggested Popular Discovery Prompts
    fastify.get('/suggested-prompts', async () => {
        return {
            success: true,
            prompts: [
                'Who actually built the Great Pyramid of Giza?',
                'What is the true story behind the Sphinx\'s missing nose?',
                'What mysteries were uncovered in Tutankhamun\'s tomb?',
                'How did the sacred Opet Festival connect Luxor and Karnak temples?',
                'How were the 50-ton granite blocks hauled across the Nile?'
            ]
        };
    });
};

export default aiRoutes;
