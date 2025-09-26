package com.example.productmanager.service.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException; 
import jakarta.mail.internet.MimeMessage;

import java.io.File;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender; 

    @Value("${app.mail.sender-from:noreply@example.com}") 
    private String senderFrom;

    /**
     * Envoie un email simple en texte brut.
     * @param to L'adresse du destinataire.
     * @param subject Le sujet de l'email.
     * @param text Le contenu de l'email.
     */
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderFrom);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        try {
            mailSender.send(message);
            System.out.println("Email simple envoyé à " + to + " avec le sujet : " + subject);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email simple à " + to + ": " + e.getMessage());
            // Ici, on devrait logger cette erreur ou déclencher une alerte critique
        }
    }

    /**
     * Envoie un email avec contenu HTML.
     * @param to L'adresse du destinataire.
     * @param subject Le sujet de l'email.
     * @param htmlContent Le contenu HTML de l'email.
     * @throws MessagingException si une erreur survient lors de la création du message MIME.
     */
    public void sendHtmlMessage(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8"); // true pour support multipart

        helper.setFrom(senderFrom);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // true indique que le contenu est du HTML

        try {
            mailSender.send(mimeMessage);
            System.out.println("Email HTML envoyé à " + to + " avec le sujet : " + subject);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email HTML à " + to + ": " + e.getMessage());
            // Logger l'erreur ici
        }
    }

   
}
