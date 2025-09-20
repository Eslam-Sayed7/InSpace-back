package com.InSpace.Api.services.impl;

import com.InSpace.Api.services.EmailService;
import com.InSpace.Api.services.dto.Email.EmailFormateDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;

    @Value("${EMAIL_USERNAME}")
    private String fromEmail;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    @Override
    public void sendEmail(EmailFormateDto emailDto) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject(emailDto.getSubject());
            message.setFrom(fromEmail);
            message.setTo(emailDto.getTo());
            message.setText(emailDto.getEmailBody());
            javaMailSender.send(message);
        } catch (MailException exception) {
            throw new RuntimeException("Failed to send email", exception);
        }
    }
}