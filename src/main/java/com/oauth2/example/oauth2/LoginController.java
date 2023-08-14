package com.oauth2.example.oauth2;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
public class LoginController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${client.registration.google.client.id}")
    private String clientId;
    @Value("${client.registration.google.client.secret}")
    private String clientSecret;
    @Value("${frontend.redirect.uri}")
    private String frontendRedirectUri;
    @Autowired
    private JwtService jwtService;

    @GetMapping(path = "login")
    public ResponseEntity<String> singIn(@RequestParam(name = "code") String googleCode) throws IOException, GeneralSecurityException {
        String idToken = sendAuthRequestToGoogle(googleCode);
        String jwtToken = jwtService.generateJwtToken(idToken);
        return new ResponseEntity<>(jwtToken, HttpStatus.OK);
    }

    private String sendAuthRequestToGoogle(String googleCode) throws JsonProcessingException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
        paramsMap.add("client_id", clientId);
        paramsMap.add("client_secret", clientSecret);
        paramsMap.add("grant_type", "authorization_code");
        paramsMap.add("code", googleCode); // the code from frontend app
        paramsMap.add("redirect_uri", frontendRedirectUri); // this redirect_uri must exactly match the redirect_uri used by frontend to get the Auth code

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(paramsMap, httpHeaders);

        ResponseEntity<String> response = restTemplate.postForEntity("https://oauth2.googleapis.com/token", request, String.class);
        return objectMapper.readValue(response.getBody(), GoogleResponseEntity.class).getIdToken();
    }


}
