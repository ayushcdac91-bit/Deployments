package com.cdac.statewide.hisregistration.service.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cdac.statewide.hisregistration.service.entity.UserMasterVO;

@FeignClient(name = "auth-service", url = "http://localhost:8082")
public interface AuthClient {
    @GetMapping("/auth/user/details")
    UserMasterVO getUserDetails(@RequestParam("username") String username);
}
