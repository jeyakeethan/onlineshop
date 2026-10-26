package com.example.onlineshop.support.dto;

import com.example.onlineshop.support.entity.Ticket;
import com.example.onlineshop.support.entity.TicketMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter @Setter @NoArgsConstructor
public class TicketDetailDTO extends TicketDTO {
    private List<TicketMessageDTO> messages;

    public TicketDetailDTO(Ticket ticket, List<TicketMessage> messages) {
        super(ticket);
        this.messages = messages.stream().map(TicketMessageDTO::new).toList();
    }
}
