package com.example.smartjournaling.backend.controller;

import org.springframework.web.bind.annotation.*;

import com.example.smartjournaling.backend.service.UserService;

@RestController
@RequestMapping("/user")

public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String email, @RequestParam String displayName, @RequestParam String password) {
        return service.signup(email, displayName, password);
    }

   @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password) {
        return service.login(email, password);
    }

}
