package com.crm.crm_lite.service;

import com.crm.crm_lite.model.ChatRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.util.*;

@Service
public class ChatService {

    @Value("${groq.api.key:}")
    private String groqApiKey;

    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";

    private static final String SYSTEM_PROMPT =
            "You are a smart CRM assistant for CRM Lite. Be concise and helpful.";

    private final RestTemplate restTemplate;

    public ChatService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(3000);
        this.restTemplate = new RestTemplate(factory);
    }

    public String chat(ChatRequest chatRequest) {

        if (groqApiKey == null || groqApiKey.isBlank()) {
            return "Chat unavailable: API key missing.";
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + groqApiKey);

            // ✅ Build messages properly
            List<Map<String, Object>> safeMessages = new ArrayList<>();

            // System message (ONLY ONCE)
            Map<String, Object> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", SYSTEM_PROMPT);
            safeMessages.add(systemMsg);

            // User messages
            if (chatRequest.getMessages() != null) {
                for (Map<String, String> msg : chatRequest.getMessages()) {
                    if (msg.get("role") != null && msg.get("content") != null) {
                        Map<String, Object> m = new HashMap<>();
                        m.put("role", msg.get("role"));
                        m.put("content", msg.get("content"));
                        safeMessages.add(m);
                    }
                }
            }

            // ✅ Build request body
            Map<String, Object> body = new HashMap<>();
            body.put("model", "llama-3.1-8b-instant");
            body.put("messages", safeMessages);
            body.put("max_tokens", 80);
            body.put("temperature", 0.7);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(GROQ_URL, entity, Map.class);

            if (response.getBody() == null) {
                return "AI returned empty response.";
            }

            List<Map<String, Object>> choices =
                    (List<Map<String, Object>>) response.getBody().get("choices");

            if (choices == null || choices.isEmpty()) {
                return "No response from AI.";
            }

            Map<String, Object> message =
                    (Map<String, Object>) choices.get(0).get("message");

            return message != null
                    ? (String) message.get("content")
                    : "Empty AI response.";

        } catch (HttpClientErrorException e) {
            return "Chat error: " + e.getStatusCode();
        } catch (Exception e) {
            return "⚠️ AI unavailable or slow.";
        }
    }
}