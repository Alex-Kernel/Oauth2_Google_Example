package com.oauth2.example.oauth2;


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class JwtService {

    @Value("${web.token.secret}")
    private String webTokenSecret;

    private GoogleIdTokenVerifier googleIdTokenVerifier;

    public JwtService(@Value("${client.registration.google.client.id}") String clientId) {
        this.googleIdTokenVerifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(List.of(clientId)).build();
    }

    private String verifyGoogleIdTokenAndGetEmail(String idToken) throws GeneralSecurityException, IOException {
        GoogleIdToken googleIdToken = googleIdTokenVerifier.verify(idToken);
        if (idToken != null) {
            GoogleIdToken.Payload payload = googleIdToken.getPayload();
            return payload.getEmail();
        }
        return null;
    }

    public String generateJwtToken(String idToken) throws GeneralSecurityException, IOException {
        String subject = verifyGoogleIdTokenAndGetEmail(idToken);
        if (subject == null) {
            throw new RuntimeException("Could not retrieve user email");
        }
        Date issueDate = new Date();
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(issueDate)
                .setExpiration(new Date(issueDate.getTime() + TimeUnit.HOURS.toMillis(12)))
                .setClaims(new HashMap<>() {{put("user_role", "USER");}})
                .signWith(getJwtSecretKey(webTokenSecret))
                .compact();
    }

    public Authentication verifyToken(String authToken) {
        Claims claims = Jwts.parserBuilder().setSigningKey(getJwtSecretKey(webTokenSecret)).build().parseClaimsJws(authToken).getBody();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(String.valueOf(claims.get("user_role")));
        return new UsernamePasswordAuthenticationToken(claims.getSubject(), authToken, List.of(authority));
    }

    private Key getJwtSecretKey(String jwtSecret) {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

}
