package com.statewide.login.mapper;

import org.mapstruct.Mapper;
import com.statewide.login.entity.UserMasterVO;
import com.statewide.login.responsedto.LoginUserResponseDTO;

@Mapper(componentModel = "spring")
public interface UserMapper {
    LoginUserResponseDTO toLoginUserResponse(UserMasterVO userMasterVO);
}
