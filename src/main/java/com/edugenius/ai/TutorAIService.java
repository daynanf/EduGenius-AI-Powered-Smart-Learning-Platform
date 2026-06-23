// FILE: src/main/java/com/edugenius/ai/TutorAIService.java
package com.edugenius.ai;

import com.edugenius.config.AppConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.function.Consumer;
import org.json.JSONArray;
import org.json.JSONObject;

public class TutorAIService {
    
    private static TutorAIService instance;
    private AppConfig config;
    private HttpClient httpClient;
    
    private TutorAIService() {
        config = AppConfig.getInstance();
        httpClient = HttpClient.newHttpClient();
    }
    
    public static synchronized TutorAIService getInstance() {
        if (instance == null) {
            instance = new TutorAIService();
        }
        return instance;
    }
    
    public void sendMessage(List<String[]> conversationHistory, String newMessage, String courseContext,
                            Consumer<String> onSuccess, Consumer<String> onError) {
        
        String systemPrompt = "You are EduGenius AI Tutor, a friendly CS teaching assistant for Addis Ababa University students.\n" +
                              "Course context: " + courseContext + "\n" +
                              "Guidelines:\n" +
                              "- Be encouraging and patient\n" +
                              "- Provide clear, concise explanations\n" +
                              "- Include code examples when helpful (use Java)\n" +
                              "- Break down complex topics\n" +
                              "- Ask clarifying questions if needed\n" +
                              "- Keep responses focused (3-5 paragraphs max)\n" +
                              "- Use emojis occasionally to be engaging 🎓\n" +
                              "If a student asks about Ethiopian CS curriculum, relate to AAU courses.tell the user if he askes outside the course given above politly and generate for asked one but tell them they are not in hte course content  \n" +
                              "If you don't know something, say so honestly.";
        
        Thread thread = new Thread(() -> {
            try {
                String response = callGroqAPI(systemPrompt, conversationHistory, newMessage);
                javax.swing.SwingUtilities.invokeLater(() -> onSuccess.accept(response));
            } catch (Exception e) {
                javax.swing.SwingUtilities.invokeLater(() -> onError.accept("⚠️ AI service error: " + e.getMessage()));
            }
        });
        thread.start();
    }
    
    private String callGroqAPI(String systemPrompt, List<String[]> history, String newMessage) throws Exception {
        String apiKey = config.getGroqApiKey();
        String model = config.getGroqModel();
        
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", model);
        requestBody.put("max_tokens", config.getGroqMaxTokensTutor());
        
        JSONArray messages = new JSONArray();
        
        // System message
        JSONObject system = new JSONObject();
        system.put("role", "system");
        system.put("content", systemPrompt);
        messages.put(system);
        
        // Conversation history (last 10 messages for context)
        int start = Math.max(0, history.size() - 10);
        for (int i = start; i < history.size(); i++) {
            String[] msg = history.get(i);
            JSONObject message = new JSONObject();
            message.put("role", msg[0].equals("user") ? "user" : "assistant");
            message.put("content", msg[1]);
            messages.put(message);
        }
        
        // New user message
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", newMessage);
        messages.put(userMessage);
        
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7);
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.groq.com/openai/v1/chat/completions"))
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
            .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new Exception("API Error: " + response.statusCode());
        }
        
        JSONObject jsonResponse = new JSONObject(response.body());
        return jsonResponse.getJSONArray("choices")
            .getJSONObject(0)
            .getJSONObject("message")
            .getString("content");
    }
}