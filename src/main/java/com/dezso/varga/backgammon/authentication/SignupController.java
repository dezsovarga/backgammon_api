package com.dezso.varga.backgammon.authentication;

import com.dezso.varga.backgammon.exeptions.MissingFieldsException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.dezso.varga.backgammon.authentication.domain.RegisterRequest;
import java.util.Date;

@RestController
@RequestMapping("account")
public class SignupController {
	
	@Autowired
	private AuthenticationService authenticationService;
	static final long EXPIRATION_TIME = 86_400_000; // 1 day

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
		String email = registerRequest.getAccount().getEmail();
		Date expirationTime = new Date(System.currentTimeMillis() + EXPIRATION_TIME);
		String jwtToken = Jwts.builder().claim("perm","invitation")
										.setExpiration(expirationTime)
										.setSubject(email).claim("roles", "user")
										.setIssuedAt(new Date())
										.signWith(SignatureAlgorithm.HS256, "secretkey")
										.compact();

//		Claims claims = Jwts.parser().setSigningKey("secretkey").parseClaimsJws(jwtToken).getBody();

		authenticationService.sendConfirmationMail();
		return jwtToken;
	}
}
