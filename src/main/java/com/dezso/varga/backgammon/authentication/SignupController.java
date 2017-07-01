package com.dezso.varga.backgammon.authentication;

import com.dezso.varga.backgammon.authentication.domain.Account;
import com.dezso.varga.backgammon.authentication.repository.AccountRepository;
import com.dezso.varga.backgammon.exeptions.MissingFieldsException;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.dezso.varga.backgammon.authentication.domain.RegisterRequest;

@RestController
@RequestMapping("account")
public class SignupController {
	
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
			throw new MissingFieldsException("Missing or invalid mandatory fields at registration");
		}
		String confirmationToken = AuthUtils.generateRegisterConfirmationToken(registerRequest);

//		Claims claims = Jwts.parser().setSigningKey("secretkey").parseClaimsJws(confirmationToken).getBody();

		authenticationService.sendConfirmationMail();
		return confirmationToken;
	}

	@RequestMapping(method=RequestMethod.GET, value="register/confirm")
	public String confirm(@RequestHeader (value="Authorization") String confirmToken) throws Exception {
		Account account = AuthUtils.validateConfirmToken(confirmToken);
		accountRepository.save(account);
		return "ok";
	}
}
