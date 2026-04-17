package com.statewide.login.service;

import java.util.HashMap;
import java.util.Map;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.statewide.login.entity.ForgotPassUserVO;
import com.statewide.login.repository.ForgotPasswordRepository;
import com.statewide.login.requestdto.ForgotPasswordRequest;
import com.statewide.login.utils.ForgotPasswordRedisOtp;
import com.statewide.login.utils.LoginFeatureUtil;
import com.statewide.login.utils.UserManagementBO;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ForgotPasswordService {

    private final ForgotPasswordRepository forgotPasswordRepository;
    private final LoginFeatureUtil loginFeatureUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ForgotPasswordRedisOtp forgotPasswordRedisOtp;
    private final PasswordEncoder passwordEncoder;
    private final UserManagementBO userManagementBO;

    public ForgotPasswordService(ForgotPasswordRepository forgotPasswordRepository, LoginFeatureUtil loginFeatureUtil,
            RedisTemplate<String, Object> redisTemplate, ForgotPasswordRedisOtp forgotPasswordRedisOtp,
            PasswordEncoder passwordEncoder, UserManagementBO userManagementBO) {
        this.forgotPasswordRepository = forgotPasswordRepository;
        this.loginFeatureUtil = loginFeatureUtil;
        this.redisTemplate = redisTemplate;
        this.forgotPasswordRedisOtp = forgotPasswordRedisOtp;
        this.passwordEncoder = passwordEncoder;
        this.userManagementBO = userManagementBO;
    }

    public Map<String, Object> fetchUserDetailsByUserName(String varUserName) {
        Map<String, Object> response = new HashMap<>();
        ForgotPassUserVO fetchUser = forgotPasswordRepository.fetchUserDetailsByMobileNumber("1", varUserName);
        log.info("Fetch user by username -----" + fetchUser);
        if (fetchUser == null) {
            response.put("status", "FAIL");
            response.put("message", "Invalid Username");
            return response;
        }

        response.put("status", "SUCCESS");
        response.put("message", "Username verified");
        response.put("UserName", fetchUser.getVarUserName());
        response.put("UserId", fetchUser.getVarUserId());
        response.put("MobileNumber", fetchUser.getVarMobileNumber());
        response.put("Emailid", fetchUser.getVarEmailId());

        return response;
    }

    // Send Mobile OTP FOR Forgot Password
    public Map<String, Object> sendMobileOtpForForgotPassword(String varUserName, String varMobileNumber) {
        Map<String, Object> response = new HashMap<>();
        ForgotPassUserVO fetchUser = forgotPasswordRepository.fetchUserDetailsByMobileNumber("1", varUserName);
        if (fetchUser == null) {
            response.put("status", "FAIL");
            response.put("message", "Invalid Username");
            return response;
        }
        if (!fetchUser.getVarMobileNumber().equals(varMobileNumber)) {
            response.put("status", "FAIL");
            response.put("message", "Invalid Mobile Number");
            return response;
        }

        String otpResponse = loginFeatureUtil.sendMobileOtpForForgotPassword(varUserName, varMobileNumber);
        log.info("OTP Response ---" + otpResponse);
        response.put("OTP Status", "SUCCESS");
        response.put("UserName", fetchUser.getVarUserName());
        response.put("MobileNumber", fetchUser.getVarMobileNumber());
        response.put("otpResponse", otpResponse);
        forgotPasswordRedisOtp.storeOtp(fetchUser.getVarMobileNumber(), otpResponse);
        return response;
    }

    // Verify OTP FOR Email AND Mobile
    public boolean verifyMobileOtpForgetPassword(String varUserName, String varMobileNumber, String inputOtp,
            String varEmailId) {

        if (varMobileNumber != null) {
            String mobileStoredOtp = (String) redisTemplate.opsForValue().get("OTP_MOBILE" + varMobileNumber);
            log.info("Stored OTP for mobile {}: {}", varMobileNumber, mobileStoredOtp);
            if (mobileStoredOtp != null && mobileStoredOtp.equals(inputOtp)) {
                // Optional: delete OTP after verification

                return true;
            }

        }
        if (varEmailId != null && !varEmailId.isEmpty()) {
            String emailStoredOtp = (String) redisTemplate.opsForValue().get("OTP_EMAIL" + varEmailId);
            log.info("Stored OTP for email {}: {}", varEmailId, emailStoredOtp);
            if (emailStoredOtp != null && emailStoredOtp.equals(inputOtp)) {
                // Optional: delete OTP after verification

                return true;
            }
        }

        return false;
    }

    // Reset Forgot Password
    public Map<String, Object> resetForgottenPassword(String varUserName, String varNewPassword,
            String varConfirmPassword) {
        Map<String, Object> response = new HashMap<>();
        boolean flg = true;
        String count = "0";
        if (varNewPassword == null ||
                !varNewPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$")) {
            response.put("message", "Invalid password format.");
        }

        if (varConfirmPassword == null || !varConfirmPassword.equals(varNewPassword)) {
            response.put("message", "Passwords and Confirmed Password do not match.");
        }
        if (varNewPassword.toUpperCase().contains(varUserName.toUpperCase())) {
            throw new IllegalArgumentException("Password must not contain username");
            // response.put("message", "Password must not contain username");
        }
        String[] commom_seq = { "CDAC", "PASSWORD", "QWERTY" };
        for (String seq : commom_seq) {
            if (varNewPassword.toUpperCase().contains(seq)) {
                // throw new IllegalArgumentException(
                // "Password must not contain known common sequence like '" + seq + "'.");
                response.put("message", "Password must not contain known common sequence like '" + seq + "'.");

            }
        }

        if (flg) {
            // Match NewPassword to OldPassword
            ForgotPassUserVO fetchUser = forgotPasswordRepository.fetchUserDetailsByMobileNumber("1", varUserName);
            count = forgotPasswordRepository.checkUserPasswordDetail("1", fetchUser.getVarHospitalCode(),
                    fetchUser.getVarUserId(), fetchUser.getVarUserSeatId(), varNewPassword);
            log.info("Count for  comapre db password" + count);
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            log.info("VarUserNewPassword --" + varNewPassword + "vardbhashedpassword--" + fetchUser.getVarPassword());
            if (fetchUser.getVarPassword() != null && encoder.matches(varNewPassword, fetchUser.getVarPassword())) {
                flg = false;
                response.put("message", "You have already used this password. Please choose a different one.");
            } else {
                // Final password update
                log.info("else part excuation");
                String hashedPassword = passwordEncoder.encode(varNewPassword);
                ForgotPassUserVO forgotPassUserVO = new ForgotPassUserVO();
                forgotPassUserVO.setVarHospitalCode(fetchUser.getVarHospitalCode());
                forgotPassUserVO.setVarUserId(fetchUser.getVarUserId());
                forgotPassUserVO.setVarUserSeatId(fetchUser.getVarUserSeatId());
                forgotPassUserVO.setVarUserName(fetchUser.getVarUserName());
                forgotPassUserVO.setVarNewPassword(hashedPassword);
                // forgotPasswordRepository.resetForgottenUserPasswordDetail("5", FVO);
                userManagementBO.resetForgottenUserPassword("5", forgotPassUserVO);
                response.put("Status", "success");
                response.put("message", "Password Reset Successfully");
            }
        }

        return response;
    }

    // Send Email OTP FOR Forgot Password
    public Map<String, Object> sendEmailOtpForForgotPassword(ForgotPasswordRequest request) {
        Map<String, Object> response = new HashMap<>();
        ForgotPassUserVO fetchUser = forgotPasswordRepository.fetchUserDetailsByMobileNumber("1",
                request.getVarUserName());
        if (fetchUser == null) {
            response.put("status", "FAIL");
            response.put("message", "Invalid Username");
            return response;
        }

        if (!fetchUser.getVarEmailId().equals(request.getVarEmailId())) {
            response.put("status", "FAIL");
            response.put("message", "Invalid Email Id");
            return response;
        }
        String otpResponse = loginFeatureUtil.sendEmailForgotPassword(request.getVarUserName(),
                request.getVarEmailId());
        log.info("OTP Email Response ---" + otpResponse);
        response.put("OTP Email Status", "SUCCESS");
        response.put("UserName", fetchUser.getVarUserName());
        response.put("MobileNumber", fetchUser.getVarMobileNumber());
        response.put("OTPEmailResponse", otpResponse);
        forgotPasswordRedisOtp.storeOtpEmail(fetchUser.getVarEmailId(), otpResponse);
        return response;
    }

}
