package com.UniProject.config;

import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
public class CustomWebSocketEventHandler extends AbstractWebSocketHandler {

    private final Map<String, WebSocketSession> sessions;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(
            WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
    }

    @Override
    protected void handleTextMessage(
            WebSocketSession session, TextMessage message) throws IOException {
        for (WebSocketSession s : sessions.values()) {
            if (s.isOpen()) {
                s.sendMessage(message);
            }
        }
    }
}