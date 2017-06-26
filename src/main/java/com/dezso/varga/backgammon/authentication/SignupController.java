package com.dezso.varga.backgammon.authentication;

import com.dezso.varga.backgammon.exeptions.MissingFieldsException;
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
		String confirmationToken = AuthUtils.getRegisterConfirmationToken(registerRequest);

//		Claims claims = Jwts.parser().setSigningKey("secretkey").parseClaimsJws(confirmationToken).getBody();

		authenticationService.sendConfirmationMail();
		return confirmationToken;
	}
}
