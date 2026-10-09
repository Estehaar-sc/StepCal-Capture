package com.UniProject.Controller;

import com.UniProject.DTO.ChatMessageDto;
import com.UniProject.DTO.DtoImpl;
import com.UniProject.DTO.UserDto;
import com.UniProject.Services.ChatService;
import com.UniProject.Services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private static final Logger logger =
            LoggerFactory.getLogger(ChatController.class);

    private final Map<String, WebSocketSession> sessions;
    private final UserService userService;
    private final ChatService chatService;
    private final DtoImpl dto;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/send-message")
    public ResponseEntity<Void> sendMessageToAll(
            @RequestBody ChatMessageDto message,
            HttpServletRequest request) throws JsonProcessingException {

        String email = (String) request.getAttribute("email");

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDto user = userService.getUser(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        message.setSender(user.getFirst_name());

        String jsonMessage = objectMapper.writeValueAsString(message);
        chatService.saveMessage(message);

        TextMessage textMessage = new TextMessage(jsonMessage);

        for (WebSocketSession session : sessions.values()) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(textMessage);
                }
            } catch (Exception e) {
                logger.warn("Unable to deliver a chat message to a WebSocket session.");
            }
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/get-messages")
    public ResponseEntity<List<ChatMessageDto>> showAllMessage() {
        List<ChatMessageDto> messages = chatService.getAllMessages();
        return ResponseEntity.ok(messages);
    }
}