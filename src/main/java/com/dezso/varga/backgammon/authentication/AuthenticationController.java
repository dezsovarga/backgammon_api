package com.dezso.varga.backgammon.authentication;

import com.dezso.varga.backgammon.authentication.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.dezso.varga.backgammon.authentication.domain.RegisterRequest;

@RestController
@RequestMapping("account")
public class AuthenticationController {
	
	@Autowired
	private AuthenticationService authenticationService;

	@RequestMapping(method=RequestMethod.POST,value="/register")
	public String signup(@RequestBody RegisterRequest registerRequest) throws Exception{
		authenticationService.sendConfirmationMail();
		return authenticationService.getConfirmationToken(registerRequest);
	}

	@RequestMapping(method=RequestMethod.GET, value="register/confirm")
	public String confirm(@RequestHeader (value="Authorization") String confirmToken) throws Exception {

		return AuthUtils.generateBearerToken(authenticationService.saveAccount(confirmToken));
	}

	@RequestMapping(method=RequestMethod.POST, value="/login")
	public String login(@RequestHeader (value="Authorization", required=false) String authHeader) throws Exception {

		return AuthUtils.generateBearerToken(authenticationService.login(authHeader));
	}
}
