package com.example.skillsharing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendDelayNotification(String recipientEmail, String delayReason) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject("Job Delay Alert");
        message.setText("Your job is delayed. Reason: " + delayReason);
        mailSender.send(message);
    }
}
