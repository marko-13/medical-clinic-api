package com.proj.medicalClinic.service;

import com.proj.medicalClinic.model.AppUser;
import org.springframework.mail.MailException;

import javax.mail.MessagingException;

public interface EmailService {

     void sendNotificaitionAsync(AppUser user, String msg) throws MailException, InterruptedException, MessagingException;
     void sendNotificaitionAsync(AppUser user, String msg, String subject) throws MailException, InterruptedException, MessagingException;
}
