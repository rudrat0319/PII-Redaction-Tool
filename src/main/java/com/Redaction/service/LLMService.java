package com.Redaction.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LLMService {

    private final Client client;
    private final String model;

    public LLMService(
            @Value("${spring.ai.google.genai.api-key}") String apiKey,
            @Value("${spring.ai.google.genai.model}") String model
    ) {
        this.client = Client.builder()
                .apiKey(apiKey)
                .build();

        this.model = model;
    }

    public String generateSummary(String text) {

        String prompt = "Summarize the following redacted document in 3–4 lines. "
                + "ONLY describe what PII was removed. "
                + "Do NOT guess or reconstruct hidden PII.\n\n"
                + text;

        try {
            GenerateContentResponse response =
                    client.models.generateContent(model, prompt, null);

            return response.text();
        } catch (Exception e) {
            return "Summary generation failed: " + e.getMessage();
        }
    }
}