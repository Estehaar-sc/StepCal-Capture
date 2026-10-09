package com.UniProject.DTO;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ChatMessageDto {
    private String sender;
    private String content;
}
