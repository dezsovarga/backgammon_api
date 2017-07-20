package com.dezso.varga.backgammon.authentication;

import com.dezso.varga.backgammon.authentication.domain.Account;
import com.dezso.varga.backgammon.authentication.repository.AccountRepository;
import com.dezso.varga.backgammon.exeptions.BgException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.dezso.varga.backgammon.authentication.domain.RegisterRequest;
import java.util.Date;
@RestController
@RequestMapping("account")
public class AuthenticationController {
	
	@Autowired
	private AuthenticationService authenticationService;
	@Autowired
	private AccountRepository accountRepository;

	@RequestMapping(method=RequestMethod.POST,value="/register")
	public String signup(@RequestBody RegisterRequest registerRequest) throws Exception{
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
		String confirmationToken = AuthUtils.generateRegisterConfirmationToken(registerRequest);
		authenticationService.sendConfirmationMail();
		return confirmationToken;
	}

	@RequestMapping(method=RequestMethod.GET, value="register/confirm")
	public String confirm(@RequestHeader (value="Authorization") String confirmToken) throws Exception {
		Account account = AuthUtils.validateConfirmToken(confirmToken);
		accountRepository.save(account);
		return "ok";
	}

	@RequestMapping(method=RequestMethod.POST, value="/login")
	public String login(@RequestHeader (value="Authorization", required=false) String authHeader) throws Exception {
		Account credentials = AuthUtils.extractAccountFromBasicToken(authHeader);
		Account account = accountRepository.findByEmail(credentials.getEmail());
		if (account == null) {
			throw new BgException("User email not found.", HttpStatus.UNAUTHORIZED.value());
		}
		if (!credentials.getPassword().equals(account.getPassword())) {
			throw new BgException("Invalid credentials. Please check your email and password.",
					HttpStatus.UNAUTHORIZED.value());
		}
		return AuthUtils.generateBearerToken(account);
	}
}
