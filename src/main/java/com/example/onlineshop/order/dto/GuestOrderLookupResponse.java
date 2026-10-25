package com.example.onlineshop.order.dto;

import lombok.Getter;

@Getter
public class GuestOrderLookupResponse {
    private final String message;

    public GuestOrderLookupResponse() {
        this.message = "If an eligible guest order exists, verification instructions have been sent.";
    }
}
