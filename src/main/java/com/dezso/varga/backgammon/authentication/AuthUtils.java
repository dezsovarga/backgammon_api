package com.dezso.varga.backgammon.authentication;

import com.dezso.varga.backgammon.authentication.domain.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.tomcat.util.codec.binary.Base64;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dezso on 26.06.2017.
 */
public class AuthUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();
    static final long EXPIRATION_TIME = 86_400_000; // 1 day

    public static String getRegisterConfirmationToken(RegisterRequest registerRequest) throws Exception{
        String email = registerRequest.getAccount().getEmail();
        Date expirationTime = new Date(System.currentTimeMillis() + EXPIRATION_TIME);
        String jwtToken = Jwts.builder().claim("perm","invitation")
                .setExpiration(expirationTime)
                .setSubject(email).claim("roles", "user")
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, "secretkey")
                .compact();
        Map<String, String> fields = new HashMap<>();
        fields.put("token", jwtToken);
        fields.put("username", registerRequest.getAccount().getEmail());
        return Base64.encodeBase64URLSafeString(objectMapper.writeValueAsBytes(fields));
    }
}
