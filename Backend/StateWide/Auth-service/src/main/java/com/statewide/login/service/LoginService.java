package com.statewide.login.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import com.statewide.login.entity.UserMasterVO;
import com.statewide.login.repository.LoginRepository;
import com.statewide.login.requestdto.LoginUserRequestDTO;
import com.statewide.login.utils.LoginPasswordUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LoginService {

    private final LoginRepository loginRepository;
    private final LoginPasswordUtil loginPasswordUtil;

    public LoginService(LoginRepository loginRepository, LoginPasswordUtil loginPasswordUtil) {
        this.loginRepository = loginRepository;
        this.loginPasswordUtil = loginPasswordUtil;
    }

    public Map<String, Object> login(LoginUserRequestDTO request) {

        Map<String, Object> response = new HashMap<>();
        List<UserMasterVO> fetchUser = loginRepository.getUserDetail("1", request);
        UserMasterVO user = fetchUser.get(0);
        // log.info("UserMasterVO ---" + fetchUser);

        if (fetchUser == null || fetchUser.isEmpty()) {
            throw new BadCredentialsException("Invalid User Name/Password!");

        }

        if ("1".equals(user.getVarLock())) {
            response.put("status", HttpStatus.UNAUTHORIZED.value());
            response.put("message", "User is Locked!! Contact System Administrator!");
            return response;
        }

        if (!loginPasswordUtil.checkPassword(request.getVarPassword(), user.getVarPassword())) {
            log.info("Inside LoginService  password Not match ");
            /**
             * Handles unsuccessful login attempts and locks the account if needed.
             */
            return loginPasswordUtil.handleUnsuccessfulLogin(request, user);
        } else {
            /**
             * Handles successful login: generates JWT token and prepares the user response.
             */
            log.info("Inside LoginService  password  match ");
            return loginPasswordUtil.handleSuccessfulLogin(user, response);
        }

    }

    public UserMasterVO fetchUserDetails(String strMode, String username) {
        LoginUserRequestDTO request = new LoginUserRequestDTO();
        request.setVarUserName(username);
        List<UserMasterVO> fetchDetailsByUser = loginRepository.getUserDetail("1", request);
        return fetchDetailsByUser.get(0);
    }

}
