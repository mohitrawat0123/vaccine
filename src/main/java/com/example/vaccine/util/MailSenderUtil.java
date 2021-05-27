package com.example.vaccine.util;

import com.example.vaccine.dto.MailRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.List;

@Service
public class MailSenderUtil {

    @Autowired
    private JavaMailSender mailSender;

    public void sendMail(MailRequestDTO mailRequestDTO) {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mailMsg = new MimeMessageHelper(mimeMessage);
        try {
            List<String> emails = mailRequestDTO.getTo();
            mailMsg.setFrom(mailRequestDTO.getFrom());
            mailMsg.setTo(Arrays.copyOf(emails.toArray(), emails.size(), String[].class));
            mailMsg.setSubject(mailRequestDTO.getSubject());
            mailMsg.setText(mailRequestDTO.getBody());
        }catch (Exception ignored){
            System.out.println("Exception occurred while sending mail - "+ ignored.getMessage());
        }
        mailSender.send(mimeMessage);
    }

/*
    @PostConstruct
    public void init() {

        // Assuming you are sending email from localhost
        String host = "localhost";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.smtp.socketFactory.fallback", "false");
        properties.setProperty("mail.smtp.port", "465");
        properties.setProperty("mail.smtp.socketFactory.port", "465");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.debug", "true");
        properties.setProperty("mail.store.protocol", "pop3");
        properties.setProperty("mail.transport.protocol", "smtp");

        // Get the default Session object.
        session = Session.getDefaultInstance(properties);

    }

*/


}
