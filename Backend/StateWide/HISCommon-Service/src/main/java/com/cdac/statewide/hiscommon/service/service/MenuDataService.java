package com.cdac.statewide.hiscommon.service.service;

import java.util.Map;

import com.cdac.statewide.hiscommon.service.dto.requestdto.MenuDataRequest;

public interface MenuDataService {
    public Map<String, Object> getMenuData(MenuDataRequest menuDataRequest);
}
