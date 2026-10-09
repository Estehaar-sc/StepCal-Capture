package com.UniProject.Services;

import com.UniProject.DTO.ChatMessageDto;
import com.UniProject.Entities.ChatMessage;
import com.UniProject.Repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    public void saveMessage(ChatMessageDto message) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender(message.getSender());
        chatMessage.setContent(message.getContent());
        chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessageDto> getAllMessages() {
        List<ChatMessage> chatMessageList =
                (List<ChatMessage>) chatMessageRepository.findAll();

        List<ChatMessageDto> messages = new ArrayList<>();

        for (ChatMessage cm : chatMessageList) {
            ChatMessageDto message = new ChatMessageDto();
            message.setSender(cm.getSender());
            message.setContent(cm.getContent());
            messages.add(message);
        }

        return messages;
    }
}