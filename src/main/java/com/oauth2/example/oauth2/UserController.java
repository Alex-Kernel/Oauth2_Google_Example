package com.oauth2.example.oauth2;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    private static final List<User> users = List.of(
            new User("1", "John", "Smith"),
            new User("2", "Tom", "Gray"),
            new User("3", "Nathan", "Wilson"),
            new User("4", "Jennifer", "Brown")
    );

    @GetMapping("users")
    public ResponseEntity<List<User>> getUsers() {
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

}
