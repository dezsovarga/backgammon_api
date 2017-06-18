package com.dezso.varga.backgammon.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dezso.varga.backgammon.authentication.domain.RegisterRequest;

@RestController
@RequestMapping("account")
public class SignupController {
	
	@Autowired
	private AuthenticationService authenticationService;

	@RequestMapping(method=RequestMethod.POST,value="/register")
	public void signup(@RequestBody RegisterRequest registerRequest){
		authenticationService.sendConfirmationMail();
	}
}
