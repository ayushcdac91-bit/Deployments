package com.statewide.login.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.statewide.login.config.HISSOConfig;
import com.statewide.login.entity.UserLoginLogVO;
import com.statewide.login.entity.UserMasterVO;
import com.statewide.login.mapper.UserMapper;
import com.statewide.login.repository.LoginRepository;
import com.statewide.login.requestdto.LoginUserRequestDTO;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LoginPasswordUtil {

    private final PasswordEncoder passwordEncoder;
    private final LoginRepository loginRepository;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public LoginPasswordUtil(PasswordEncoder passwordEncoder, LoginRepository loginRepository, JwtService jwtService,
            UserMapper userMapper) {
        this.passwordEncoder = passwordEncoder;
        this.loginRepository = loginRepository;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
    }

    public boolean checkPassword(String rawPassword, String storedHash) {
        // log.info("Paaword Util Component class" + rawPassword);
        String hashedPassword = passwordEncoder.encode(rawPassword);
        log.info("RAW hashedPassword:   " + hashedPassword);
        log.info("Stored Hashed Password Util Component class" + storedHash);
        return passwordEncoder.matches(rawPassword, storedHash);

    }

    /**
     * Handles successful login: generates JWT token and prepares the user response.
     */
    public Map<String, Object> handleSuccessfulLogin(UserMasterVO user, Map<String, Object> response) {
        log.info("outside else password match----");
        UserLoginLogVO voUserLog = new UserLoginLogVO();
        BeanUtils.copyProperties(user, voUserLog);
        voUserLog.setVarSeatId(user.getVarUserSeatId());
        voUserLog.setVarIPAddress(NetworkUtils.getIpAddress());
        voUserLog.setVarMacAddress("0.0.0.0");
        voUserLog.setVarAppHost(NetworkUtils.getHostName());
        voUserLog.setVarUserLoginDate(DateHelperMethods.getDateString(System.currentTimeMillis()));
        user.setVarIPAddress(voUserLog.getVarIPAddress());
        // Inserting/Update User successful Log
        loginRepository.dmlUserLoginLog("2", voUserLog);
        String jwtToken = jwtService.generateAccessToken(user.getVarUserName());
        // log.info("Jwt Token::" + jwtToken);
        response.put("jwtToken", jwtToken);
        response.put("UserData", userMapper.toLoginUserResponse(user));
        response.put("message", "Login successful");

        return response;
    }

    /**
     * Handles unsuccessful login attempts and locks the account if needed.
     */
    public Map<String, Object> handleUnsuccessfulLogin(LoginUserRequestDTO request, UserMasterVO user) {
        log.info("Inside Unsuccessfull Login request {}  usermastervo {}" + request + user);
        Map<String, Object> response = new HashMap<>();
        if (user.getVarLock().equals("1")) {

            response.put("status", HttpStatus.UNAUTHORIZED);
            response.put("message", "User is Locked!! Contact System Administrator!");
            return response;
        }

        UserLoginLogVO voUserUnsuccessLoginLog = new UserLoginLogVO();
        BeanUtils.copyProperties(request, voUserUnsuccessLoginLog);
        String ipAddress = NetworkUtils.getIpAddress();
        voUserUnsuccessLoginLog.setVarIPAddress(ipAddress);
        voUserUnsuccessLoginLog.setVarMacAddress("0.0.0.0");

        // Inserting/Update User Unsuccessful Log
        loginRepository.dmlUserLoginLog("1", voUserUnsuccessLoginLog);

        // Getting Unsuccessful Count
        List<UserLoginLogVO> lstUnsuccessLoginLog = loginRepository.getUserLoginLog("1",
                voUserUnsuccessLoginLog, null, null);
        log.info("lstUnsuccessLoginLog------" + lstUnsuccessLoginLog);

        int nUnsuccessLoginCount = 0;
        if (lstUnsuccessLoginLog != null && lstUnsuccessLoginLog.size() > 0
                && lstUnsuccessLoginLog.get(0) != null) {
            UserLoginLogVO voLog = lstUnsuccessLoginLog.get(0);
            log.info("voLog----:::" + voLog.getVarUnsuccessfulCount());
            if (voLog != null && voLog.getVarUnsuccessfulCount() != null) {
                nUnsuccessLoginCount = Integer.parseInt(voLog.getVarUnsuccessfulCount());
            }
        }

        // Check for Max Unsuccessful Login Count exceeds
        if (nUnsuccessLoginCount >= HISSOConfig.LOGIN_LOCK_AFTER_UNSUCCESSFUL_LOGIN_COUNT) {

            // Lock User with Given User Name and IP Address
            UserMasterVO userMasterVO = new UserMasterVO();
            BeanUtils.copyProperties(request, userMasterVO);
            loginRepository.dmlUserDetail("1", userMasterVO);

            // Update Unsuccessful Login Log after Lock
            // Here deleting all Log of User ???
            // loginObj.updateUnsuccessfulLoginLog(request.getParameter("uid"));

        }
        response.put("status", HttpStatus.UNAUTHORIZED);
        response.put("message", "Invalid Password!! User will be locked after "
                + HISSOConfig.LOGIN_LOCK_AFTER_UNSUCCESSFUL_LOGIN_COUNT + " Unsuccessful Attempts!");

        return response;
    }

}
