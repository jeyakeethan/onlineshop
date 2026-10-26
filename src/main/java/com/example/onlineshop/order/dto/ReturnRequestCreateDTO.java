package com.example.onlineshop.order.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReturnRequestCreateDTO {
    private String reason;
    private List<ReturnItemRequestDTO> items;
}
