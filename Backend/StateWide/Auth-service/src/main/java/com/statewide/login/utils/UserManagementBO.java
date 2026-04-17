package com.statewide.login.utils;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import com.statewide.login.entity.ChangePasswordVO;
import com.statewide.login.entity.ForgotPassUserVO;
import com.statewide.login.entity.MenuMasterVO;
import com.statewide.login.entity.UserLoginLogVO;
import com.statewide.login.entity.UserMasterVO;
import com.statewide.login.exception.HISApplicationExecutionException;
import com.statewide.login.exception.HISDataAccessException;
import com.statewide.login.exception.HISException;
import com.statewide.login.repository.ChangePasswordRepository;
import com.statewide.login.repository.ChangeUserDetailsRepository;
import com.statewide.login.repository.ForgotPasswordRepository;
import com.statewide.login.repository.UserLogDetailRepository;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UserManagementBO {

    private final ChangeUserDetailsRepository changeUserDetailsRepository;
    private final UserLogDetailRepository userLogDetailRepository;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final ChangePasswordRepository changePasswordRepository;

    public UserManagementBO(ChangeUserDetailsRepository changeUserDetailsRepository,
            UserLogDetailRepository userLogDetailRepository, ForgotPasswordRepository forgotPasswordRepository,
            ChangePasswordRepository changePasswordRepository) {
        this.changeUserDetailsRepository = changeUserDetailsRepository;
        this.userLogDetailRepository = userLogDetailRepository;
        this.forgotPasswordRepository = forgotPasswordRepository;
        this.changePasswordRepository = changePasswordRepository;
    }

    /**
     * Updating User Login Details
     * 
     * @param voUser
     * @return voUser
     */
    public UserMasterVO changeLoginUserDetails(UserMasterVO voUser_p, List<MenuMasterVO> menuMasterVO_p) {
        try {
            // Updating User Login Details
            changeUserDetailsRepository.dmlUserDetail("4", voUser_p);

            MenuMasterVO voMenu = new MenuMasterVO();
            BeanUtils.copyProperties(voUser_p, voMenu);
            changeUserDetailsRepository.dmlMenuMasterDetail("2", voMenu);
            for (MenuMasterVO menuMaster : menuMasterVO_p) {
                System.out.println("user id====Bo" + menuMaster.getVarUserId());
                changeUserDetailsRepository.dmlMenuMasterDetail("1", menuMaster);
            }

        } catch (HISException e) {
            log.error("HISException in changeLoginUserDetails for user {}: {}", voUser_p.getVarUserName(),
                    e.getMessage());
            throw new HISException("UserManagementBO.changeUserDetails()::AuthServiceException -> " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected exception in changeLoginUserDetails for user {}: {}", voUser_p.getVarUserName(), e);
            throw new HISApplicationExecutionException(
                    "UserManagementBO.changeLoginUserDetails()::AuthServiceExecutionException -> "
                            + e.getMessage());
        }
        return voUser_p;
    }

    /**
     * Login date and time List
     * 
     * @param voUser_p
     * @return List<Entry>
     */
    public List<UserLoginLogVO> getLogList(UserMasterVO voUser_p, int n, String frDate, String toDate) {
        List<UserLoginLogVO> lstData = new ArrayList<UserLoginLogVO>();
        try {
            UserLoginLogVO voLoginLog = new UserLoginLogVO();
            BeanUtils.copyProperties(voUser_p, voLoginLog);

            if (n == 10)
                lstData = userLogDetailRepository.getUserLoginLog("2", voLoginLog, frDate, toDate);
            else
                lstData = userLogDetailRepository.getUserLoginLog("3", voLoginLog, frDate, toDate);
        } catch (HISDataAccessException e) {
            e.printStackTrace();
            throw new HISDataAccessException(
                    "UserManagementBO.getLogList()::HISDataAccessException -> " + e.getMessage());
        } catch (HISApplicationExecutionException e) {
            e.printStackTrace();
            throw new HISApplicationExecutionException(
                    "UserManagementBO.getLogList()::HISApplicationExecutionException -> "
                            + e.getMessage());
        } catch (HISException e) {
            throw new HISException("UserManagementBO.getLogList()::HISException -> " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new HISApplicationExecutionException(
                    "UserManagementBO.getLogList()::HISApplicationExecutionException -> "
                            + e.getMessage());
        }

        return lstData;
    }

    public ForgotPassUserVO resetForgottenUserPassword(String str_mode, ForgotPassUserVO forgotPassUserVO) {

        try {

            // Updating User Password
            forgotPasswordRepository.dmlUserDetail("5", forgotPassUserVO);

        } catch (HISDataAccessException e) {
            throw new HISDataAccessException(
                    "UserManagementBO.resetForgottenUserPassword()::HISDataAccessException -> " + e.getMessage());
        } catch (HISApplicationExecutionException e) {
            throw new HISApplicationExecutionException(
                    "UserManagementBO.resetForgottenUserPassword()::HISApplicationExecutionException -> "
                            + e.getMessage());
        } catch (HISException e) {
            throw new HISException("UserManagementBO.resetForgottenUserPassword()::HISException -> " + e.getMessage());
        } catch (Exception e) {
            throw new HISApplicationExecutionException(
                    "UserManagementBO.resetForgottenUserPassword()::HISApplicationExecutionException -> "
                            + e.getMessage());
        }
        return forgotPassUserVO;
    }

    /**
     * Updating User Password
     * 
     * @param voUser_p
     * @return voUser_p
     */
    public ChangePasswordVO changeUserPassword(ChangePasswordVO voUser_p) {

        try {

            // Updating User Password
            changePasswordRepository.changeUserPasswordDetail("2", voUser_p);
            voUser_p.setVarLoggedIn("Update User Password");

        } catch (HISException e) {
            throw new HISException("UserManagementBO.changeUserPassword()::HISException -> " + e.getMessage());
        } catch (Exception e) {
            throw new HISApplicationExecutionException(
                    "UserManagementBO.changeUserPassword()::HISApplicationExecutionException -> "
                            + e.getMessage());
        }

        return voUser_p;
    }

}
