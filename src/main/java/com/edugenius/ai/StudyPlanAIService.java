// FILE: src/main/java/com/edugenius/ai/StudyPlanAIService.java
package com.edugenius.ai;

import com.edugenius.config.AppConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;
import org.json.JSONArray;
import org.json.JSONObject;

public class StudyPlanAIService {
    
    private static StudyPlanAIService instance;
    private AppConfig config;
    private HttpClient httpClient;
    
    private StudyPlanAIService() {
        config = AppConfig.getInstance();
        httpClient = HttpClient.newHttpClient();
    }
    
    public static synchronized StudyPlanAIService getInstance() {
        if (instance == null) {
            instance = new StudyPlanAIService();
        }
        return instance;
    }
    
    public void generateStudyPlan(String userPrompt, String courseName, 
                                   Consumer<String> onSuccess, Consumer<String> onError) {
        String systemPrompt = "You are an expert study plan creator for AAU CS students. " +
                              "Create a detailed, actionable weekly study plan. " +
                              "Include specific topics, daily tasks, estimated hours, and resources. and also if the user asks  other topics rather that the given one tell him  about hte given one  polity but also give the requsted servies  " +
                              "Format with clear headings using emojis for each week and day.";
        
        String fullPrompt = "Course: " + courseName + "\nStudent request: " + userPrompt + 
                            "\nGenerate a focused 2-4 week study plan.";
        
        Thread thread = new Thread(() -> {
            try {
                String response = callGroqAPI(systemPrompt, fullPrompt);
                javax.swing.SwingUtilities.invokeLater(() -> onSuccess.accept(response));
            } catch (Exception e) {
                javax.swing.SwingUtilities.invokeLater(() -> onError.accept(e.getMessage()));
            }
        });
        thread.start();
    }
    
    private String callGroqAPI(String systemPrompt, String userMessage) throws Exception {
        String apiKey = config.getGroqApiKey();
        String model = config.getGroqModel();
        
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", model);
        requestBody.put("max_tokens", config.getGroqMaxTokensPlan());
        
        JSONArray messages = new JSONArray();
        
        JSONObject system = new JSONObject();
        system.put("role", "system");
        system.put("content", systemPrompt);
        messages.put(system);
        
        JSONObject user = new JSONObject();
        user.put("role", "user");
        user.put("content", userMessage);
        messages.put(user);
        
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7);
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.groq.com/openai/v1/chat/completions"))
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
            .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        JSONObject jsonResponse = new JSONObject(response.body());
        return jsonResponse.getJSONArray("choices")
            .getJSONObject(0)
            .getJSONObject("message")
            .getString("content");
    }
}