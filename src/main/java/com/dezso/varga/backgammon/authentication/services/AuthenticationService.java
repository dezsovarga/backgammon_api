package com.dezso.varga.backgammon.authentication.services;

import javax.mail.internet.MimeMessage;

import com.dezso.varga.backgammon.authentication.AuthUtils;
import com.dezso.varga.backgammon.authentication.domain.Account;
import com.dezso.varga.backgammon.authentication.domain.RegisterRequest;
import com.dezso.varga.backgammon.authentication.repositories.AccountRepository;
import com.dezso.varga.backgammon.exeptions.AuthExeption;
import com.dezso.varga.backgammon.exeptions.BgException;
import com.dezso.varga.backgammon.exeptions.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
	
	@Autowired
	private JavaMailSender sender;
    @Autowired
    private AccountRepository accountRepository;

    public Account login(String authHeader) throws Exception{
        Account credentials = AuthUtils.extractAccountFromBasicToken(authHeader);
        Account account = accountRepository.findByEmail(credentials.getEmail());
        if (account == null) {
            throw new AuthExeption("User email not found.", HttpStatus.UNAUTHORIZED.value());
        }
        if (!credentials.getPassword().equals(account.getPassword())) {
            throw new AuthExeption("Invalid credentials. Please check your email and password.",
                    HttpStatus.UNAUTHORIZED.value());
        }
        return account;
    }

    public Account saveAccount(String confirmToken) throws Exception{
        Account account = AuthUtils.validateConfirmToken(confirmToken);
        Account existingAccount = accountRepository.findByEmail(account.getEmail());
        if (existingAccount == null) {
            account.setActive(true);
            accountRepository.save(account);
        } else {
            throw new UserAlreadyExistsException("User already verified", HttpStatus.CONFLICT.value());
        }
        return account;
    }

    public String getConfirmationToken(RegisterRequest registerRequest) throws Exception{
        if (registerRequest == null
                || registerRequest.getAccount().getFirstName() == null
                || registerRequest.getAccount().getFirstName().trim().isEmpty()
                || registerRequest.getAccount().getLastName() == null
                || registerRequest.getAccount().getLastName().trim().isEmpty()
                || registerRequest.getAccount().getEmail() == null
                || registerRequest.getAccount().getEmail().trim().isEmpty()
                || registerRequest.getAccount().getPassword() == null
                || registerRequest.getAccount().getPassword().trim().isEmpty()) {
            throw new BgException("Missing or invalid mandatory fields at registration",
                    HttpStatus.PRECONDITION_FAILED.value());
        }
        Account existingAccount = accountRepository.findByEmail(registerRequest.getAccount().getEmail().trim());
        if (existingAccount != null) {
            throw new UserAlreadyExistsException("Account already exists", HttpStatus.CONFLICT.value());
        }

        return AuthUtils.generateRegisterConfirmationToken(registerRequest);
    }

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
