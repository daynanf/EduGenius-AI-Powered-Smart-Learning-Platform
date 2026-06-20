// FILE: src/main/java/com/edugenius/ai/QuizAIService.java
package com.edugenius.ai;

import com.edugenius.config.AppConfig;
import com.edugenius.models.QuizQuestion;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * AI Service for Quiz Generation using Groq API
 * Uses LLaMA 3.3 70B model
 */
public class QuizAIService {
    
    private static QuizAIService instance;
    private AppConfig config;
    private HttpClient httpClient;
    
    private QuizAIService() {
        config = AppConfig.getInstance();
        httpClient = HttpClient.newHttpClient();
    }
    
    public static synchronized QuizAIService getInstance() {
        if (instance == null) {
            instance = new QuizAIService();
        }
        return instance;
    }
    
    /**
     * Generate quiz questions asynchronously
     */
    public void generateQuiz(String topic, String difficulty, int questionCount, 
                             Consumer<List<QuizQuestion>> onSuccess, 
                             Consumer<String> onError) {
        
        String prompt = buildQuizPrompt(topic, difficulty, questionCount);
        
        Thread thread = new Thread(() -> {
            try {
                String response = callGroqAPI(prompt);
                List<QuizQuestion> questions = parseQuizResponse(response, difficulty);
                javax.swing.SwingUtilities.invokeLater(() -> onSuccess.accept(questions));
            } catch (Exception e) {
                javax.swing.SwingUtilities.invokeLater(() -> onError.accept(e.getMessage()));
            }
        });
        thread.start();
    }
    
    private String buildQuizPrompt(String topic, String difficulty, int questionCount) {
        return "You are an expert CS quiz creator for AAU (Addis Ababa University) students.\n" +
               "Generate " + questionCount + " " + difficulty + " difficulty multiple choice questions about: " + topic + "\n\n" +
               "Each question must have:\n" +
               "- A clear question text\n" +
               "- 4 options labeled A, B, C, D\n" +
               "- The correct answer letter\n" +
               "- A brief explanation of why it's correct\n\n" +
               "Respond ONLY with a valid JSON array. No other text. Format exactly like this:\n" +
               "[\n" +
               "  {\n" +
               "    \"question\": \"What is the time complexity of binary search?\",\n" +
               "    \"options\": {\"A\": \"O(1)\", \"B\": \"O(log n)\", \"C\": \"O(n)\", \"D\": \"O(n log n)\"},\n" +
               "    \"correct\": \"B\",\n" +
               "    \"explanation\": \"Binary search divides the search space in half each time, resulting in logarithmic time complexity.\"\n" +
               "  }\n" +
               "]\n\n" +
               "Make questions relevant to Ethiopian CS curriculum. Include practical programming examples when appropriate.";
    }
    
    private String callGroqAPI(String prompt) throws Exception {
        String apiKey = config.getGroqApiKey();
        String model = config.getGroqModel();
        
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", model);
        requestBody.put("max_tokens", config.getGroqMaxTokensQuiz());
        
        JSONArray messages = new JSONArray();
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are an expert quiz creator. Always respond with valid JSON only.");
        messages.put(systemMessage);
        
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
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
            throw new Exception("API Error: " + response.statusCode() + " - " + response.body());
        }
        
        JSONObject jsonResponse = new JSONObject(response.body());
        String content = jsonResponse.getJSONArray("choices")
            .getJSONObject(0)
            .getJSONObject("message")
            .getString("content");
        
        // Clean up the response - remove markdown code blocks if present
        content = content.replace("```json", "").replace("```", "").trim();
        
        return content;
    }
    
    private List<QuizQuestion> parseQuizResponse(String response, String difficulty) {
        List<QuizQuestion> questions = new ArrayList<>();
        
        try {
            JSONArray jsonArray = new JSONArray(response);
            
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject q = jsonArray.getJSONObject(i);
                
                String questionText = q.getString("question");
                JSONObject options = q.getJSONObject("options");
                String correct = q.getString("correct");
                String explanation = q.optString("explanation", "Great job! Keep learning!");
                
                questions.add(new QuizQuestion(
                    i + 1,
                    questionText,
                    options.optString("A", ""),
                    options.optString("B", ""),
                    options.optString("C", ""),
                    options.optString("D", ""),
                    correct,
                    explanation,
                    difficulty
                ));
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to parse quiz response: " + e.getMessage());
            // Return sample questions as fallback
            return getSampleQuestions(difficulty);
        }
        
        return questions;
    }
    
    private List<QuizQuestion> getSampleQuestions(String difficulty) {
        List<QuizQuestion> samples = new ArrayList<>();
        samples.add(new QuizQuestion(1, 
            "What is the primary purpose of Object-Oriented Programming?",
            "To make code run faster", "To organize code into reusable objects", 
            "To eliminate all bugs", "To make programs smaller",
            "B", "OOP helps organize code into objects that contain both data and methods.", difficulty));
        samples.add(new QuizQuestion(2,
            "Which keyword is used to create a subclass in Java?",
            "super", "this", "extends", "implements",
            "C", "The 'extends' keyword is used to create a subclass that inherits from a parent class.", difficulty));
        return samples;
    }
    
    /**
     * Get AI explanation for a student's answer
     */
    public void getAnswerExplanation(String questionText, String correctAnswer, 
                                      String studentAnswer, boolean isCorrect,
                                      Consumer<String> onSuccess, Consumer<String> onError) {
        String prompt = "Question: " + questionText + "\n" +
                        "Correct Answer: " + correctAnswer + "\n" +
                        "Student Answer: " + studentAnswer + "\n" +
                        "The student got this " + (isCorrect ? "CORRECT" : "WRONG") + ".\n" +
                        "Provide a brief, encouraging explanation (2-3 sentences) teaching the concept.";
        
        Thread thread = new Thread(() -> {
            try {
                String response = callGroqAPIForExplanation(prompt);
                javax.swing.SwingUtilities.invokeLater(() -> onSuccess.accept(response));
            } catch (Exception e) {
                javax.swing.SwingUtilities.invokeLater(() -> onError.accept(e.getMessage()));
            }
        });
        thread.start();
    }
    
    private String callGroqAPIForExplanation(String prompt) throws Exception {
        String apiKey = config.getGroqApiKey();
        String model = config.getGroqModel();
        
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", model);
        requestBody.put("max_tokens", 300);
        
        JSONArray messages = new JSONArray();
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.put(userMessage);
        
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.5);
        
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