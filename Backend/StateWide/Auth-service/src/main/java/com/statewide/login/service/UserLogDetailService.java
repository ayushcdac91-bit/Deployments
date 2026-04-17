package com.statewide.login.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.statewide.login.entity.UserLoginLogVO;
import com.statewide.login.entity.UserMasterVO;
import com.statewide.login.mapper.UserLogsMapper;
import com.statewide.login.repository.ChangePasswordRepository;
import com.statewide.login.utils.UserManagementBO;

@Service
public class UserLogDetailService {

    private final UserManagementBO userManagementBO;
    private final ChangePasswordRepository changePasswordRepository;
    private final UserLogsMapper userLogsMapper;

    public UserLogDetailService(UserManagementBO userManagementBO, ChangePasswordRepository changePasswordRepository,
            UserLogsMapper userLogsMapper) {
        this.userManagementBO = userManagementBO;
        this.changePasswordRepository = changePasswordRepository;
        this.userLogsMapper = userLogsMapper;
    }

    public Map<String, Object> initUserLogDetails(String username) {
        Map<String, Object> response = new HashMap<>();
        UserMasterVO fetchDetailsByUser = changePasswordRepository.fetchUserDetails("1", username);
        UserMasterVO voUser = new UserMasterVO();
        BeanUtils.copyProperties(fetchDetailsByUser, voUser);
        int num = 10;
        List<UserLoginLogVO> loginList = userManagementBO.getLogList(voUser, num, null, null);
        response.put("UserLogDetails", userLogsMapper.toUserLogDateResponse(loginList));
        response.put("status", "success");

        return response;
    }

    public Map<String, Object> allUserLogDetails(String userName, String fromDate,
            String toDate, int page,
            int size) {
        int num = 11;
        Map<String, Object> response = new HashMap<>();
        UserMasterVO fetchDetailsByUser = changePasswordRepository.fetchUserDetails("1", userName);
        UserMasterVO voUser = new UserMasterVO();
        BeanUtils.copyProperties(fetchDetailsByUser, voUser);
        List<UserLoginLogVO> loginList = userManagementBO.getLogList(voUser, num, fromDate, toDate);
        int totalElements = loginList.size();
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalElements);
        // List<UserLoginLogVO> pagedList = fromIndex >= totalElements ? List.of() :
        // loginList.subList(fromIndex, toIndex);
        List<UserLoginLogVO> pagedList = loginList.stream()
                .skip(fromIndex)
                .limit(toIndex - fromIndex)
                .toList();
        response.put("data", userLogsMapper.toUserLogDateResponse(pagedList));
        response.put("page", page);
        response.put("size", size);
        response.put("totalElements", totalElements);
        response.put("totalPages", (int) Math.ceil((double) totalElements / size));
        response.put("hasNext", toIndex < totalElements);
        response.put("status", "success");

        return response;
    }

}
