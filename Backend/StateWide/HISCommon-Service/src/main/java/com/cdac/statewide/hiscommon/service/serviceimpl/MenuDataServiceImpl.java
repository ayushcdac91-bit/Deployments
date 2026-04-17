package com.cdac.statewide.hiscommon.service.serviceimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.cdac.statewide.hiscommon.service.entity.MenuMasterVO;
import com.cdac.statewide.hiscommon.service.repository.MenuDataRepository;
import com.cdac.statewide.hiscommon.service.service.MenuDataService;

import lombok.extern.slf4j.Slf4j;

import com.cdac.statewide.hiscommon.service.dto.requestdto.MenuDataRequest;

@Service
@Slf4j
public class MenuDataServiceImpl implements MenuDataService {
    private final MenuDataRepository menuDataRepository;
    private ArrayList lstUserMenus;
    private HashMap mapContextMenus;
    private HashMap mapUserAllowedMenus;
    private Map<String, Object> mapMenusHirarchy;

    public MenuDataServiceImpl(MenuDataRepository menuDataRepository) {
        this.menuDataRepository = menuDataRepository;
    }

    public Map<String, Object> getMenuData(MenuDataRequest menuDataRequest) {

        Map<String, Object> response = new HashMap<>();

        MenuMasterVO menuMasterVO = new MenuMasterVO();
        menuMasterVO.setVarHospitalCode(menuDataRequest.getVarHospitalCode());
        menuMasterVO.setVarUserId(menuDataRequest.getVarUserId());
        menuMasterVO.setVarUserSeatId(menuDataRequest.getVarUserSeatId());

        // Getting User Menu Detail
        List<MenuMasterVO> listMenus = menuDataRepository.getUserMenuDetail("1", menuMasterVO);

        // mapData.put("keyUserMenuList", listMenus);

        // Getting UserUserAllowedMenuList
        List<MenuMasterVO> listAllowedMenus = menuDataRepository.getUserMenuDetail("4", menuMasterVO);
        // log.info("MenuMasterVO data " + lstAllowedMenus);

        // mapData.put("keyUserAllowedMenuList", listAllowedMenus);

        // Getting System Date and Time
        List<MenuMasterVO> listSystemDate = menuDataRepository.getSystemDate("2");

        // log.info("voUserSysDateTime data " + voUserSysDateTime);

        // mapData.put("keySystemDateTime", listSystemDate);

        // Getting User Favorite Menu List
        List<MenuMasterVO> listFavoriteMenus = menuDataRepository.getUserMenuDetail("2", menuMasterVO);
        // log.info("lstFavoriteMenus data " + lstFavoriteMenus);

        // mapData.put("keyUserFavoriteMenuList", listFavoriteMenus);

        // Get the Back Date day end flag details
        String checkBackDateEndFlag = menuDataRepository.checkBackDateDayEnd("1", menuMasterVO);
        // log.info("checkBackDateEndFlag::---" + checkBackDateEndFlag);

        // mapData.put("keyCashCollectionAllowed", checkBackDateEndFlag);
        Map<String, Object> menuHierarchy = setMenusMap(listMenus, listAllowedMenus);
        // response.put("menuHierarchy", menuHierarchy);
        if (menuHierarchy.isEmpty()) {
            log.warn("No valid menu data found {}.");
            response.put("error", "No valid menu data found.");
            response.put("status", HttpStatus.NOT_FOUND.value());
        } else {
            log.info("Menu data fetched successfully | count={}", menuHierarchy.size());
            response.put("menuHierarchy", menuHierarchy);
            response.put("status", HttpStatus.OK.value());

        }
        return response;

    }

    // Added By Hemant Sir For Menu Data
    private Map<String, Object> setMenusMap(
            List<MenuMasterVO> lstMenu,
            List<MenuMasterVO> lstAllowedMenuURL) {
        if (lstMenu == null || lstMenu.isEmpty()) {
            return Collections.emptyMap();
        }

        this.lstUserMenus = new ArrayList<>();
        this.mapContextMenus = new HashMap<>();
        this.mapUserAllowedMenus = new HashMap<>();

        Map<String, Object> mp = new LinkedHashMap<>();

        for (MenuMasterVO voMenu : lstMenu) {
            String[] arrMenu = voMenu.getVarMenuName().split("#");

            Map<String, Object> mpMenuBase = mp;

            for (int i = 0; i < arrMenu.length - 1; i++) {
                String menuName = arrMenu[i];

                Object obj = mpMenuBase.get(menuName);

                if (obj == null) {
                    Map<String, Object> child = new LinkedHashMap<>();
                    mpMenuBase.put(menuName, child);
                    mpMenuBase = child;
                } else if (obj instanceof Map) {
                    mpMenuBase = (Map<String, Object>) obj;
                }
            }

            mpMenuBase.put(arrMenu[arrMenu.length - 1], voMenu.getVarURL());

        }

        this.mapMenusHirarchy = mp;
        return mp;
    }

    private String getContext(String strURI) {
        String strContext = null;
        if (strURI != null || strURI != "") {
            if (strURI.indexOf("/") >= 0)
                strContext = strURI.substring(0, strURI.indexOf("/", 2));

        }
        return strContext;
    }

}
