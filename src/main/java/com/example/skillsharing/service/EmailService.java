package com.example.skillsharing.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	private final JavaMailSender mailSender;

	public EmailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void sendEmail(String to, String subject, String content) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");

			message.setContent(content, "text/html");
			helper.setTo(to);
			helper.setSubject(subject);

			mailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
			System.out.println("Failed to send email: " + e.getMessage());
		}
	}
}
