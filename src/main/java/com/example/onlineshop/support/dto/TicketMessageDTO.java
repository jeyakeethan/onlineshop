package com.example.onlineshop.support.dto;

import com.example.onlineshop.support.entity.TicketMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
public class TicketMessageDTO {
    private Long messageId;
    private String authorType;
    private boolean internalNote;
    private String message;
    private LocalDateTime createdAt;

    public TicketMessageDTO(TicketMessage message) {
        this.messageId = message.getMessageId();
        this.authorType = message.getAuthorType();
        this.internalNote = message.isInternalNote();
        this.message = message.getMessage();
        this.createdAt = message.getCreatedAt();
    }
}
