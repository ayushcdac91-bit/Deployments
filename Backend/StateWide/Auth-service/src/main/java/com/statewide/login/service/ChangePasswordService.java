package com.statewide.login.service;

import java.util.HashMap;
import java.util.Map;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.statewide.login.entity.ChangePasswordVO;
import com.statewide.login.entity.UserMasterVO;
import com.statewide.login.repository.ChangePasswordRepository;
import com.statewide.login.requestdto.ChangePasswordRequest;
import com.statewide.login.utils.ChangePasswordUtil;
import com.statewide.login.utils.LoginPasswordUtil;
import com.statewide.login.utils.UserManagementBO;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChangePasswordService {

    private final ChangePasswordRepository changePasswordRepository;
    private final LoginPasswordUtil passwordUtil;
    private final PasswordEncoder passwordEncoder;
    private final ChangePasswordUtil changePasswordUtil;
    private final UserManagementBO userManagementBO;

    public ChangePasswordService(ChangePasswordRepository changePasswordRepository, LoginPasswordUtil passwordUtil,
            PasswordEncoder passwordEncoder, ChangePasswordUtil changePasswordUtil, UserManagementBO userManagementBO) {
        this.changePasswordRepository = changePasswordRepository;
        this.passwordUtil = passwordUtil;
        this.passwordEncoder = passwordEncoder;
        this.changePasswordUtil = changePasswordUtil;
        this.userManagementBO = userManagementBO;
    }

    public Map<String, Object> changePassword(String varUserName, ChangePasswordRequest request) {
        Map<String, Object> response = new HashMap<>();
        UserMasterVO fetchDetailsByUser = changePasswordRepository.fetchUserDetails("1", varUserName);
        log.info("ChangePasswordService----" + fetchDetailsByUser);

        // String count = "0";
        // if (!loginVO.getVarPassword().equals(request.getVarOldPassword()))
        log.info("varoldpassword-----vardbpassword--" + request.getVarOldPassword() + "-"
                + fetchDetailsByUser.getVarPassword());
        // Validate Old Password
        if (!passwordUtil.checkPassword(request.getVarOldPassword(), fetchDetailsByUser.getVarPassword())) {
            response.put("status", "FAILED");
            response.put("message", "Old Password is Wrong!");
            return response;
        }
        // Validate New Password Format
        String newPassword = request.getVarNewPassword();
        if (newPassword == null || newPassword.isEmpty() ||
                !newPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$")) {

            response.put("status", "FAILED");
            response.put("message", "Invalid New Password!  Please try again!");
            return response;
        }
        // Confirm Password
        if (request.getVarConfirmPassword() == null || !newPassword.equals(request.getVarConfirmPassword())) {
            response.put("status", "FAILED");
            response.put("message", "Confirm Password does not match!");
            return response;
        }
        // Should not contain username
        if (newPassword.toUpperCase().contains(varUserName.toUpperCase())) {
            response.put("status", "FAILED");
            response.put("message", "Password must not contain User Name!");
            return response;
        }
        // Should not have common sequences
        if (containsCommonSequences(newPassword)) {
            response.put("status", "FAILED");
            response.put("message", "Password must not contain common sequences like CDAC, PASSWORD, QWERTY");
            return response;
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Check reused password
        if (encoder.matches(newPassword, fetchDetailsByUser.getVarPassword())) {
            response.put("status", "FAILED");
            response.put("message", "You have already used this password. Choose a different one.");
            return response;
        }

        // Password strength check (your custom method)
        Map<String, Object> strength = changePasswordUtil.checkPassStrength(newPassword,
                fetchDetailsByUser.getVarUserName());
        log.info("Strength password --- " + strength);

        if (!(boolean) strength.get("valid")) {
            response.put("status", "FAILED");
            response.put("message", strength.get("message"));
            return response;
        }
        // Prepare VO for update
        ChangePasswordVO changePassVO = new ChangePasswordVO();
        changePassVO.setVarUserId(fetchDetailsByUser.getVarUserId());
        changePassVO.setVarUserSeatId(fetchDetailsByUser.getVarUserSeatId());
        changePassVO.setVarHospitalCode(fetchDetailsByUser.getVarHospitalCode());
        changePassVO.setVarNewPassword(passwordEncoder.encode(request.getVarNewPassword()));
        changePassVO.setVarOldPassword(fetchDetailsByUser.getVarPassword());
        changePassVO.setVarUserName(fetchDetailsByUser.getVarUserName());

        // changePasswordRepository.changeUserPasswordDetail("2", changePassVO);
        userManagementBO.changeUserPassword(changePassVO);

        response.put("status", "SUCCESS");
        response.put("message", "User Password Changed Successfully!");

        return response;
    }

    private boolean containsCommonSequences(String password) {
        String[] commonSeq = { "CDAC", "PASSWORD", "QWERTY" };
        String upperPass = password.toUpperCase();
        for (String seq : commonSeq) {
            if (upperPass.contains(seq)) {
                return true;
            }
        }
        return false;
    }

}
