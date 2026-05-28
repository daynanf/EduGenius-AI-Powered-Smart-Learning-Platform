// FILE: src/main/java/com/edugenius/ai/AIService.java
package com.edugenius.ai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import com.google.gson.Gson;
import com.edugenius.model.AIQuestion;
import com.edugenius.model.AIReviewResponse;

public class AIService {
    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL_NAME = "llama-3.3-70b-versatile";
    private static final String API_KEY = "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";

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    private static String sendGroqRequest(String systemPrompt, String userPrompt) throws Exception {
        if (API_KEY == null || API_KEY.trim().isEmpty()) {
            throw new IllegalStateException("Missing API Key! Please set the GROQ_API_KEY environment variable.");
        }

        // Deep escape mechanism to prevent structural layout failures from embedded double quotes
        String escapedSystem = gson.toJson(systemPrompt);
        String escapedUser = gson.toJson(userPrompt);

        String jsonPayload = "{"
                + "\"model\": \"" + MODEL_NAME + "\","
                + "\"messages\": ["
                + "  {\"role\": \"system\", \"content\": " + escapedSystem + "},"
                + "  {\"role\": \"user\", \"content\": " + escapedUser + "}"
                + "],"
                + "\"temperature\": 0.3"
                + "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GROQ_API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Groq API Error! HTTP Status: " + response.statusCode() + " | Details: " + response.body());
        }

        GroqResponse wrapper = gson.fromJson(response.body(), GroqResponse.class);
        String cleanContent = wrapper.choices[0].message.content.trim();

        // Self-healing parsing filter to strip away unrequested markdown wraps
        if (cleanContent.startsWith("```json")) {
            cleanContent = cleanContent.substring(7);
        } else if (cleanContent.startsWith("```")) {
            cleanContent = cleanContent.substring(3);
        }
        if (cleanContent.endsWith("```")) {
            cleanContent = cleanContent.substring(0, cleanContent.length() - 3);
        }

        return cleanContent.trim();
    }

    // PILLAR 1: QUIZ GENERATOR
    public static List<AIQuestion> generateQuiz(String topic, String difficulty) throws Exception {
        String systemPrompt = "You are an academic evaluation engine. Generate an array of 5 multiple-choice questions. "
                + "Respond ONLY with a valid raw JSON array matching this schema: "
                + "[{\"questionText\": \"string\", \"options\": [\"Option 1\", \"Option 2\", \"Option 3\", \"Option 4\"], \"correctOption\": \"A\"}]";
        
        String userPrompt = "Generate a " + difficulty + " difficulty quiz on the topic: " + topic;
        String jsonResult = sendGroqRequest(systemPrompt, userPrompt);
        
        return Arrays.asList(gson.fromJson(jsonResult, AIQuestion[].class));
    }

    // PILLAR 2: ANSWER REVIEWER
    public static AIReviewResponse reviewAnswer(String question, String selected, String correct) throws Exception {
        String systemPrompt = "You are an educational tutor. Analyze the student's selected choice versus the correct answer. "
                + "Respond ONLY with a valid raw JSON object matching this schema: "
                + "{\"isCorrect\": true/false, \"explanation\": \"Detailed logic summary\", \"correctionTip\": \"Actionable study tip\"}";

        String userPrompt = "Question: " + question + "\nSelected: " + selected + "\nCorrect Answer: " + correct;
        String jsonResult = sendGroqRequest(systemPrompt, userPrompt);
        
        return gson.fromJson(jsonResult, AIReviewResponse.class);
    }

    // PILLAR 3: PATH RECOMMENDER
    public static String generateStudyPlan(String weakTopicsSummary) throws Exception {
        String systemPrompt = "You are an academic advisor. Analyze the user's weak topics and return a highly structured weekly study plan. "
                + "You may write this output directly in clean, readable Markdown text format with clear bullet points.";
        
        return sendGroqRequest(systemPrompt, "Construct a weekly remediation strategy for these weak sections: " + weakTopicsSummary);
    }

    // Internal mapping templates
    private static class GroqResponse { Choice[] choices; }
    private static class Choice { Message message; }
    private static class Message { String content; }
}
