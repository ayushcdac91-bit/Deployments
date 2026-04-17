package com.cdac.statewide.hisregistration.service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cdac.statewide.hisregistration.service.entity.UserMasterVO;
import com.cdac.statewide.hisregistration.service.service.AuthClient;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/his/reg")
@Slf4j
public class PatientRegistrationController {
    private final AuthClient authClient;

    public PatientRegistrationController(AuthClient authClient) {
        this.authClient = authClient;
    }

    @GetMapping("/user")
    public UserMasterVO fetchUserFromAuth(@RequestParam String username) {
        log.info("Inside User  from Auth Service: {}", username);
        UserMasterVO user = authClient.getUserDetails(username);

        log.info("User received from Auth Service: {}", user);

        return user;
    }
}
