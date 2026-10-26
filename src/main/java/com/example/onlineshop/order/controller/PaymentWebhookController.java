package com.example.onlineshop.order.controller;

import com.example.onlineshop.order.dto.PaymentWebhookResultDTO;
import com.example.onlineshop.order.service.PaymentWebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments/webhooks")
@RequiredArgsConstructor
public class PaymentWebhookController {
    private final PaymentWebhookService paymentWebhookService;

    @PostMapping("/{provider}")
    public PaymentWebhookResultDTO receive(
            @PathVariable String provider,
            @RequestHeader(value = "X-Payment-Signature", required = false) String signature,
            @RequestBody String rawBody) {
        return paymentWebhookService.process(provider, signature, rawBody);
    }
}
