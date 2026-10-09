package com.UniProject.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailService {

    @Autowired
    JavaMailSender mailSender;

    public int sendVerEmail(String toMail){
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom("test.company12369@gmail.com");
        message.setTo(toMail);
        message.setSubject("Verify your mail");
        Random random=new Random();
        int verCode = (int) (Math.random() * (9999 - 1000 + 1)) + 1000;
        message.setText("Verification code is: "+verCode);
        mailSender.send(message);
        return verCode;
    }
}
