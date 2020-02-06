package com.proj.medicalClinic.service.implementation;

import com.proj.medicalClinic.model.AppUser;
import com.proj.medicalClinic.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailServiceImpl implements EmailService {

//    @Autowired
//    private JavaMailSender javaMailSender;
    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername("isa.psw.tim17@gmail.com");
        mailSender.setPassword("krokodil123");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    /*
     * Koriscenje klase za ocitavanje vrednosti iz application.properties fajla
     */
    @Autowired
    private Environment env;

    @Override
    public void sendNotificaitionAsync(AppUser user, String msg) throws MailException, MessagingException {

        MimeMessage mimi = getJavaMailSender().createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimi, "utf-8");
        String htmlMsg = "<h3>" + "Hello " + user.getName() + " " + user.getLastName() + ",<br></br>" + msg + "</h3>";

        helper.setText(htmlMsg, true); // Use this or above line.
        helper.setTo(user.getEmail());
        helper.setSubject("Account activation");

        helper.setFrom("isa.psw.tim17@gmail.com");

        getJavaMailSender().send(mimi);

        System.out.println("Email poslat!");

    }

    @Override
    public void sendNotificaitionAsync(AppUser user, String msg, String subject) throws MailException, MessagingException {

        MimeMessage mimi = getJavaMailSender().createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimi, "utf-8");
        String htmlMsg = "<h3>" + "Hello " + user.getName() + " " + user.getLastName() + ",<br></br>" + msg + "</h3>";

        helper.setText(htmlMsg, true); // Use this or above line.
        helper.setTo(user.getEmail());
        helper.setSubject(subject);
        helper.setFrom("isa.psw.tim17@gmail.com");
        getJavaMailSender().send(mimi);


        System.out.println("Email poslat!");

    }
}
