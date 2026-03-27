package com.wms.reservation.controller;

import com.wms.reservation.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
public class StripeWebhookController {

  private final PaymentService paymentService;

  @PostMapping("/stripe")
  public ResponseEntity<Void> handleStripeWebhook(
      @RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
    paymentService.handleStripeWebhook(payload, sigHeader);
    return ResponseEntity.ok().build();
  }
}
