package kg.leaf.notepad.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String senderEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationCode(String recipient, String code) {
        if (senderEmail == null || senderEmail.isBlank()) {
            throw new IllegalStateException("Не настроен Gmail. Укажи GMAIL_USERNAME и GMAIL_APP_PASSWORD.");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(recipient);
        message.setSubject("Код подтверждения — Лист");
        message.setText("Здравствуйте!\n\nВаш код подтверждения: " + code
                + "\n\nКод действует 10 минут. Если вы не регистрировались, просто проигнорируйте письмо.");

        mailSender.send(message);
    }
}
