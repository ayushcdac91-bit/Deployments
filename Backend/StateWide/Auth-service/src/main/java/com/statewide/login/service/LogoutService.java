package com.statewide.login.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.statewide.login.entity.UserLoginLogVO;
import com.statewide.login.entity.UserMasterVO;
import com.statewide.login.repository.LoginRepository;
import com.statewide.login.requestdto.LoginUserRequestDTO;
import com.statewide.login.utils.DateHelperMethods;
import com.statewide.login.utils.NetworkUtils;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LogoutService {
    private final LoginRepository loginRepository;

    public LogoutService(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    public void logoutLogDetails(String userName) {

        LoginUserRequestDTO request = new LoginUserRequestDTO();
        request.setVarUserName(userName);
        List<UserMasterVO> fetchUser = loginRepository.getUserDetail("1", request);
        if (fetchUser == null || fetchUser.isEmpty()) {
            log.warn("No user found for logout: {}", userName);
            return;
        }
        UserMasterVO user = fetchUser.get(0);
        UserLoginLogVO voUserLog = new UserLoginLogVO();
        // BeanUtils.copyProperties(user, voUserLog);
        voUserLog.setVarUserId(user.getVarUserId());
        voUserLog.setVarUserName(user.getVarUserName());
        voUserLog.setVarHospitalCode(user.getVarHospitalCode());
        voUserLog.setVarSeatId(user.getVarUserSeatId());
        voUserLog.setVarIPAddress(NetworkUtils.getIpAddress());
        voUserLog.setVarMacAddress("0.0.0.0");
        voUserLog.setVarAppHost(NetworkUtils.getHostName());
        voUserLog.setVarUserLogoutDate(DateHelperMethods.getDateString(System.currentTimeMillis()));

        loginRepository.dmlUserLoginLog("3", voUserLog);

    }

}
