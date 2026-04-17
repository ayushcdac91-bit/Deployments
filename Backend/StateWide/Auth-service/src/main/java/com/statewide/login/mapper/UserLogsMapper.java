package com.statewide.login.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import com.statewide.login.entity.UserLoginLogVO;
import com.statewide.login.responsedto.UserLogDateResponse;

@Mapper(componentModel = "spring")
public interface UserLogsMapper {
    List<UserLogDateResponse> toUserLogDateResponse(List<UserLoginLogVO> loginList);
}
