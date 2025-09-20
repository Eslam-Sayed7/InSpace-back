package com.InSpace.Api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${EMAIL_HOST}")
    private String host;
    @Value("${EMAIL_USERNAME}")
    private String from;
    @Value("${EMAIL_PASSWORD}")
    private String servPass;
    @Value("${EMAIL_PORT}")
    private int port;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(from);
        mailSender.setPassword(servPass);

        Properties props = mailSender.getJavaMailProperties();
        props.put("spring.mail.properties.mail.smtp.auth", "true");
        props.put("spring.mail.properties.mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", host);

        mailSender.setJavaMailProperties(props);
        return mailSender;
    }
}
