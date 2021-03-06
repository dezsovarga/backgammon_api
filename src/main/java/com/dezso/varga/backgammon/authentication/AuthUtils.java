package com.dezso.varga.backgammon.authentication;

import com.dezso.varga.backgammon.authentication.domain.Account;
import com.dezso.varga.backgammon.authentication.domain.RegisterRequest;
import com.dezso.varga.backgammon.authentication.domain.Role;
import com.dezso.varga.backgammon.exeptions.BgException;
import com.dezso.varga.backgammon.exeptions.ConfirmTokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpStatus;

import java.util.*;

/**
 * Created by dezso on 26.06.2017.
 */
public class AuthUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();
    static final long CONFIRMATION_EXPIRATION_TIME = 86_400_000; // 1 day
    static final long LOGIN_EXPIRATION_TIME =14 * 86_400_000; // 14 day

//    static final long EXPIRATION_TIME = 1_000; // 1 sec
    private static final String SECRET_KEY = "secretkey";
    public static final String BASIC_TOKEN = "Basic ";
    public static final String BASIC_AUTH_ENCODER_SEPARATOR = ":";

    public static String generateRegisterConfirmationToken(RegisterRequest registerRequest) throws Exception{
        String email = registerRequest.getAccount().getEmail();
        Date expirationTime = new Date(System.currentTimeMillis() + CONFIRMATION_EXPIRATION_TIME);
        String request = objectMapper.writeValueAsString(registerRequest);
        String jwtToken = Jwts.builder().claim("perm",request)
                .setExpiration(expirationTime)
                .setSubject(email).claim("roles", "user")
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
        Map<String, String> fields = new HashMap<>();
        fields.put("token", jwtToken);
        fields.put("username", email);
        return Base64.encodeBase64URLSafeString(objectMapper.writeValueAsBytes(fields));
    }

    public static String generateBearerToken(Account account) throws Exception{
        String email = account.getEmail();
        Date expirationTime = new Date(System.currentTimeMillis() + LOGIN_EXPIRATION_TIME);
        String jwtToken = Jwts.builder()
                .setExpiration(expirationTime)
                .setSubject(email).claim("roles", "user")
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
        Map<String, String> fields = new HashMap<>();
        fields.put("token", jwtToken);
        fields.put("username", email);
        return Base64.encodeBase64URLSafeString(objectMapper.writeValueAsBytes(fields));
    }

    public static Account validateConfirmToken(String confirmToken) throws Exception {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(confirmToken).getBody();
        } catch (Exception ex) {
            throw new ConfirmTokenExpiredException("Confirmation token expired or invalid", HttpStatus.PRECONDITION_FAILED.value());
        }

        RegisterRequest initialRequest = objectMapper.readValue(claims.get("perm").toString(), RegisterRequest.class);
        Set roles = new HashSet();
        roles.add(new Role(claims.get("roles").toString()));
        Account account = new Account(initialRequest);
        account.setRoles(roles);
        return account;
    }

    public static Account extractAccountFromBasicToken(String authHeader) throws Exception{
        if (authHeader == null || authHeader.isEmpty()) {
            throw new BgException("Missing authorization header", HttpStatus.UNAUTHORIZED.value());
        }

        if (!authHeader.startsWith(BASIC_TOKEN)) {
            throw new BgException("Invalid authorization header", HttpStatus.UNAUTHORIZED.value());
        }

        String[] split = authHeader.split(BASIC_TOKEN);
        if (split.length == 2) {
            String decoded = new String(org.apache.commons.codec.binary.Base64.decodeBase64(split[1]));
            String[] userInfo = decoded.split(BASIC_AUTH_ENCODER_SEPARATOR);
            if (userInfo.length == 2) {
                Account account = new Account();
                account.setEmail(userInfo[0]);
                account.setPassword(userInfo[1]);
                return account;
            } else {
                throw new BgException("Invalid authorization header", HttpStatus.UNAUTHORIZED.value());
            }
        }
        throw new BgException("Invalid authorization header", HttpStatus.UNAUTHORIZED.value());
    }
}
