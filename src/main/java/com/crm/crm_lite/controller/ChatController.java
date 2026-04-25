package com.crm.crm_lite.controller;

import com.crm.crm_lite.model.ChatRequest;
import com.crm.crm_lite.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody ChatRequest request) {

        if (request == null || request.getMessages() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("reply", "Invalid request"));
        }

        String reply = chatService.chat(request);
        return ResponseEntity.ok(Map.of("reply", reply));
    }
}