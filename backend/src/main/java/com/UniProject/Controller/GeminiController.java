package com.UniProject.Controller;

import com.UniProject.Services.GeminiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/ai")
public class GeminiController {

    private final GeminiService geminiService;

    public GeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(
            @RequestBody Map<String, String> request) {

        String message = request.get("message");

        String response = geminiService.sendMessage(message);

        Map<String, String> result = new HashMap<>();
        result.put("response", response);

        return ResponseEntity.ok(result);
    }
}