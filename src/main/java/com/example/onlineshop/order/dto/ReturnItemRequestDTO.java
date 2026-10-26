package com.example.onlineshop.order.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReturnItemRequestDTO {
    private Long orderItemId;
    private Integer quantity;
    private String reason;
}
