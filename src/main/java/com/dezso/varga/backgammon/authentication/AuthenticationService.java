package com.dezso.varga.backgammon.authentication;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
	
	@Autowired
	private JavaMailSender sender;
	
	public String sendConfirmationMail() {
		try {
//            sendEmail();
            return "Email Sent!";
        }catch(Exception ex) {
		    ex.printStackTrace();
            return "Error in sending email: "+ex;
        }
	}
	
	private void sendEmail() throws Exception{
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        
        helper.setTo("email@gil.com");
        helper.setText("How are you?");
        helper.setSubject("Hi");
        
        sender.send(message);
    }
}
