package com.example.vaccine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Programmatic configuration of SMTP client.
 * @author : mohitrawat0123
 */
@Configuration
public class SMTPConfig {

//	@Bean
//	public JavaMailSenderImpl javaMailSenderImpl(){
//		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//		mailSender.setHost("smtp.gmail.com");
//		mailSender.setPort(587);
//		//Set gmail email id
//		mailSender.setUsername("your_email_id");
//		//Set gmail email password
//		mailSender.setPassword("your_password");
//		Properties prop = mailSender.getJavaMailProperties();
//		prop.put("mail.transport.protocol", "smtp");
//		prop.put("mail.smtp.auth", "true");
//		prop.put("mail.smtp.starttls.enable", "true");
//		prop.put("mail.debug", "true");
//		return mailSender;
//	}

}
