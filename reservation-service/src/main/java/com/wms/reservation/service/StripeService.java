package com.wms.reservation.service;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import com.wms.common.exception.StripePaymentException;
import com.wms.reservation.entity.Invoice;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

  @Value("${stripe.api-key}")
  private String apiKey;

  @PostConstruct
  void init() {
    Stripe.apiKey = apiKey;
  }

  public String createCheckoutSession(Invoice invoice, String successUrl, String cancelUrl) {
    try {
      SessionCreateParams params =
          SessionCreateParams.builder()
              .setMode(SessionCreateParams.Mode.PAYMENT)
              .addLineItem(
                  SessionCreateParams.LineItem.builder()
                      .setPriceData(
                          SessionCreateParams.LineItem.PriceData.builder()
                              .setCurrency(invoice.getCurrency().toLowerCase())
                              .setUnitAmount(invoice.getAmount().movePointRight(2).longValueExact())
                              .setProductData(
                                  SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                      .setName("Invoice #" + invoice.getId())
                                      .build())
                              .build())
                      .setQuantity(1L)
                      .build())
              .putMetadata("invoiceId", invoice.getId().toString())
              .setSuccessUrl(successUrl)
              .setCancelUrl(cancelUrl)
              .build();

      Session session = Session.create(params);
      return session.getUrl();
    } catch (StripeException e) {
      throw new StripePaymentException("Failed to create Stripe checkout session", e);
    }
  }

  public Event constructEvent(String payload, String sigHeader, String webhookSecret) {
    try {
      return Webhook.constructEvent(payload, sigHeader, webhookSecret);
    } catch (SignatureVerificationException e) {
      throw new StripePaymentException("Invalid Stripe webhook signature", e);
    }
  }
}
