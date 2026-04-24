package com.crm.crm_lite.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatRequest {

    // FIX: default to empty list so addAll() never throws NPE
    private List<Map<String, String>> messages = new ArrayList<>();

    public ChatRequest() {}

    public List<Map<String, String>> getMessages() { return messages; }
    public void setMessages(List<Map<String, String>> messages) {
        this.messages = messages != null ? messages : new ArrayList<>();
    }
}