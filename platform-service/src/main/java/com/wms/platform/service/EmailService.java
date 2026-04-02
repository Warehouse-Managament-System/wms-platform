package com.wms.platform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

  private final JavaMailSender mailSender;

  @Value("${spring.mail.username:}")
  private String fromAddress;

  @Value("${wms.email.enabled:false}")
  private boolean emailEnabled;

  @Async
  public void send(String to, String subject, String body) {
    if (!emailEnabled || fromAddress == null || fromAddress.isBlank()) {
      log.info("Email disabled — would send to={}, subject={}", to, subject);
      return;
    }

    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setFrom(fromAddress);
      message.setTo(to);
      message.setSubject(subject);
      message.setText(body);
      mailSender.send(message);
      log.info("Email sent to={}, subject={}", to, subject);
    } catch (MailException e) {
      log.error("Failed to send email to={}, subject={}: {}", to, subject, e.getMessage());
    }
  }
}
