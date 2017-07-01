package com.dezso.varga.backgammon.authentication;

import com.dezso.varga.backgammon.authentication.domain.Account;
import com.dezso.varga.backgammon.authentication.domain.RegisterRequest;
import com.dezso.varga.backgammon.authentication.domain.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.tomcat.util.codec.binary.Base64;

import java.util.*;

/**
 * Created by dezso on 26.06.2017.
 */
public class AuthUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();
    static final long EXPIRATION_TIME = 86_400_000; // 1 day
//    static final long EXPIRATION_TIME = 1_000; // 1 sec
    private static final String SECRET_KEY = "secretkey";

    public static String generateRegisterConfirmationToken(RegisterRequest registerRequest) throws Exception{
        String email = registerRequest.getAccount().getEmail();
        Date expirationTime = new Date(System.currentTimeMillis() + EXPIRATION_TIME);
        String request = objectMapper.writeValueAsString(registerRequest);
        String jwtToken = Jwts.builder().claim("perm",request)
                .setExpiration(expirationTime)
                .setSubject(email).claim("roles", "user")
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
        Map<String, String> fields = new HashMap<>();
        fields.put("token", jwtToken);
        fields.put("username", registerRequest.getAccount().getEmail());
        return Base64.encodeBase64URLSafeString(objectMapper.writeValueAsBytes(fields));
    }

    public static Account validateConfirmToken(String confirmToken) throws Exception {
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(confirmToken).getBody();
        RegisterRequest initialRequest = objectMapper.readValue(claims.get("perm").toString(), RegisterRequest.class);
        Set roles = new HashSet();
        roles.add(new Role(claims.get("roles").toString()));
        Account account = new Account(initialRequest);
        account.setRoles(roles);
        return account;
    }
}
