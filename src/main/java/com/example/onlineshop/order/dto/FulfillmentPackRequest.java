package com.example.onlineshop.order.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class FulfillmentPackRequest {
    private Map<Long, Integer> packedQuantities;
}
