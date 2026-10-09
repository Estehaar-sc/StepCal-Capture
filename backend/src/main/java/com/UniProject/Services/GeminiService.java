package com.UniProject.Services;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private static final Logger logger =
            LoggerFactory.getLogger(GeminiService.class);

    private static final String MODEL = "gemini-3.5-flash-lite";

    private final Client client;

    public GeminiService() {
        String apiKey = System.getenv("GEMINI_API_KEY");

        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalStateException(
                    "GEMINI_API_KEY environment variable is not configured."
            );
        }

        this.client = Client.builder()
                .apiKey(apiKey.trim())
                .build();
    }

    public String sendMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return "Please enter a message.";
        }

        try {
            GenerateContentResponse response =
                    client.models.generateContent(
                            MODEL,
                            message.trim(),
                            null
                    );

            String answer = response.text();

            if (answer == null || answer.trim().isEmpty()) {
                return "I couldn't generate a response right now. Please try again.";
            }

            return answer.trim();

        } catch (Exception e) {
            String error = e.getMessage();

            if (error != null) {
                String lowerError = error.toLowerCase();

                if (lowerError.contains("429")
                        || lowerError.contains("quota")
                        || lowerError.contains("rate limit")
                        || lowerError.contains("resource exhausted")) {
                    return "You've reached the chat limit for now. Please try again later.";
                }
            }

            logger.warn("Gemini request failed; error details omitted.");

            return "StepCal Coach is temporarily unavailable. Please try again later.";
        }
    }
}