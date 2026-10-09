package com.UniProject.Repository;

import com.UniProject.Entities.ChatMessage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface ChatMessageRepository extends CrudRepository<ChatMessage,Long> {
}
