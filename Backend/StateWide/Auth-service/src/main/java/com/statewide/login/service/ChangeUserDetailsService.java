package com.statewide.login.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.statewide.login.config.HISSOConfig;
import com.statewide.login.entity.MenuMasterVO;
import com.statewide.login.entity.QuestionVO;
import com.statewide.login.entity.UserMasterVO;
import com.statewide.login.repository.ChangePasswordRepository;
import com.statewide.login.repository.ChangeUserDetailsRepository;
import com.statewide.login.requestdto.ChangeUserDetailsRequest;
import com.statewide.login.requestdto.Entry;
import com.statewide.login.utils.LoginPasswordUtil;
import com.statewide.login.utils.UserManagementBO;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChangeUserDetailsService {

    private final ChangePasswordRepository changePasswordRepository;
    private final LoginPasswordUtil passwordUtil;
    private final ChangeUserDetailsRepository changeUserDetailsRepository;
    private final UserManagementBO userManagementBO;

    public ChangeUserDetailsService(ChangePasswordRepository changePasswordRepository, LoginPasswordUtil passwordUtil,
            ChangeUserDetailsRepository changeUserDetailsRepository, UserManagementBO userManagementBO) {
        this.changePasswordRepository = changePasswordRepository;
        this.passwordUtil = passwordUtil;
        this.changeUserDetailsRepository = changeUserDetailsRepository;
        this.userManagementBO = userManagementBO;
    }

    protected Map mapMenusHirarchy;
    protected Map<String, List<MenuMasterVO>> mapContextMenus;
    protected List<MenuMasterVO> lstUserMenus;
    protected Map<String, List<String>> mapUserAllowedMenus;
    protected List<String> lstContextMenusURL;

    public Map<String, Object> validatePassword(String username, ChangeUserDetailsRequest changeUserDetailsRequest) {
        UserMasterVO fetchDetailsByUser = changePasswordRepository.fetchUserDetails("1", username);
        // log.info("Fetch user data for change user details---:::" +
        // fetchDetailsByUser);

        boolean matchPass = passwordUtil.checkPassword(changeUserDetailsRequest.getVarPassword(),
                fetchDetailsByUser.getVarPassword());
        Map<String, Object> response = new HashMap<>();
        UserMasterVO voUser = new UserMasterVO();
        BeanUtils.copyProperties(fetchDetailsByUser, voUser);
        if (matchPass) {
            // Getting Question for changeUserDetails
            List<QuestionVO> lstQuestions = changeUserDetailsRepository.getQuestionList("1", voUser);

            // Getting User Menu Detail and Set in Menu Map
            List<MenuMasterVO> lstMenus = changeUserDetailsRepository.fetchUserMenuDetail("1", voUser);
            // Getting User Allowed Menu Detail and Set in Menu Map
            List<MenuMasterVO> lstAllowedMenus = changeUserDetailsRepository.fetchUserMenuDetail("4", voUser);
            // Getting Favorite Menu List
            List<MenuMasterVO> lstFavoriteMenus = changeUserDetailsRepository.fetchUserMenuDetail("2", voUser);
            List<Entry> lstFavourites = new ArrayList<Entry>();
            if (lstFavoriteMenus != null) {
                for (MenuMasterVO v : lstFavoriteMenus) {
                    Entry objEnt = new Entry();
                    objEnt.setLabel(v.getVarMenuName());
                    objEnt.setValue(v.getVarMenuId());
                    lstFavourites.add(objEnt);
                }

            }
            setMenusMap(lstMenus, lstAllowedMenus);
            List<Entry> lstDefMenus = new ArrayList<Entry>();
            HashSet<String> lstModules = new HashSet<String>();
            List<Entry> lstModuleNames = new ArrayList<Entry>();

            lstMenus = getMenuList();
            if (lstMenus != null) {
                for (MenuMasterVO v : lstMenus) {
                    Entry objEnt = new Entry();
                    objEnt.setLabel(v.getVarModuleName() + "->" + v.getVarMenuName());
                    objEnt.setValue(v.getVarMenuId());
                    lstDefMenus.add(objEnt);

                    String str = new String();
                    str = v.getVarModuleName();
                    lstModules.add(str);
                }
                for (String str : lstModules) {
                    Entry objEn = new Entry();
                    objEn.setLabel(str);
                    objEn.setValue(str);
                    lstModuleNames.add(objEn);
                }
            }

            // log.info("lstmenu-----" + lstMenus);
            response.put("isValid", matchPass);
            response.put("userId", fetchDetailsByUser.getVarUserId());
            response.put("username", fetchDetailsByUser.getVarUserName());
            response.put("MobileNumber", fetchDetailsByUser.getVarMobileNumber());
            response.put("email", fetchDetailsByUser.getVarEmailId());
            response.put("HintAnswer", fetchDetailsByUser.getVarHintAnswer());
            response.put("Questions List", lstQuestions);
            response.put(HISSOConfig.KEY_MENU_LIST, lstDefMenus);
            response.put(HISSOConfig.KEY_MODULE_LIST, lstModuleNames);
            response.put(HISSOConfig.KEY_MODULE_MENU_LIST, new ArrayList<Entry>());
            response.put(HISSOConfig.KEY_FAVOURITE_LIST, lstFavourites);

        } else {
            response.put("message", "Password is Wrong!");
            response.put("isNotValid", matchPass);
        }

        return response;

    }

    public Map<String, Object> saveChangeUserDetails(String username,
            ChangeUserDetailsRequest changeUserDetailsRequest) {

        UserMasterVO fetchDetailsByUser = changePasswordRepository.fetchUserDetails("1", username);
        UserMasterVO voUserFinal = new UserMasterVO();
        BeanUtils.copyProperties(fetchDetailsByUser, voUserFinal);
        Map<String, Object> response = new HashMap<>();
        voUserFinal.setVarUserName(username);
        voUserFinal.setVarQuestionId(changeUserDetailsRequest.getVarQuestionId());
        voUserFinal.setVarHintAnswer(changeUserDetailsRequest.getVarHintAnswer());
        voUserFinal.setVarMobileNumber(changeUserDetailsRequest.getVarMobileNumber());
        voUserFinal.setVarEmailId(changeUserDetailsRequest.getVarEmailId());
        voUserFinal.setVarMenuId(changeUserDetailsRequest.getVarMenuId());

        // String strFavMenu[] = changeUserDetailsRequest.getVarFavMenuId();
        String[] strFavMenu = Optional.ofNullable(changeUserDetailsRequest.getVarFavMenuId())
                .orElse(new String[0]);
        List<MenuMasterVO> lstNewFav = new ArrayList<MenuMasterVO>();

        for (String menuId : strFavMenu) {
            int i = 0;
            MenuMasterVO voMenu = new MenuMasterVO();
            voMenu.setVarUserId(voUserFinal.getVarUserId());
            voMenu.setVarHospitalCode(voUserFinal.getVarHospitalCode());
            voMenu.setVarDisplayOrder(Integer.toString(i));
            voMenu.setVarSeatId(voUserFinal.getVarSeatId());
            voMenu.setVarMenuId(menuId);
            lstNewFav.add(voMenu);
            i++;
        }
        userManagementBO.changeLoginUserDetails(voUserFinal, lstNewFav);
        response.put("message", "User Login details Changed Successfully! Re-login to see changes.");
        return response;
    }

    private boolean setMenusMap(List<MenuMasterVO> lstMenu, List<MenuMasterVO> lstAllowedMenuURL) {
        if (lstMenu != null && lstMenu.size() > 0) {
            this.lstUserMenus = new ArrayList<MenuMasterVO>();
            this.mapContextMenus = new HashMap<String, List<MenuMasterVO>>();
            this.mapUserAllowedMenus = new HashMap<String, List<String>>();

            Map mp = new LinkedHashMap();
            for (MenuMasterVO voMenu : lstMenu) {
                String arrMenu[] = voMenu.getVarMenuName().split("#");

                Map mpMenuBase = mp;
                for (int i = 0; i < (arrMenu.length - 1); i++) {
                    String menuName = arrMenu[i];
                    Object obj = mpMenuBase.get(menuName);
                    if (obj == null) {
                        mpMenuBase.put(menuName, new LinkedHashMap());
                        mpMenuBase = (Map) mpMenuBase.get(menuName);
                    } else if (obj instanceof Map) {
                        mpMenuBase = (Map) obj;
                    } else if (obj instanceof String) {
                        String menuURL = (String) obj;
                        mpMenuBase.put(menuName, new LinkedHashMap());
                        mpMenuBase = (Map) mpMenuBase.get(menuName);
                        mpMenuBase.put(menuName, menuURL);
                    }
                }

                // mpMenuBase.put(arrMenu[arrMenu.length - 1], voMenu.getVarURL());
                mpMenuBase.put(arrMenu[arrMenu.length - 1], voMenu.getVarURL() + "#" + voMenu.getVarMenuId());

                // Adding to Menu List
                MenuMasterVO voMenuComplete = new MenuMasterVO();
                voMenuComplete.setVarMenuId(voMenu.getVarMenuId());
                voMenuComplete.setVarMenuName(arrMenu[arrMenu.length - 1]);
                voMenuComplete.setVarURL(voMenu.getVarURL());
                voMenuComplete.setVarModuleName(arrMenu[0]);
                voMenuComplete.setVarMenuContext(getContext(voMenu.getVarURL()));
                voMenuComplete.setVarMenuLevel(Integer.toString(arrMenu.length));
                this.lstUserMenus.add(voMenuComplete);

                // Adding to Context Wise Menu Map
                if (this.mapContextMenus.get(voMenuComplete.getVarMenuContext()) == null)
                    this.mapContextMenus.put(voMenuComplete.getVarMenuContext(), new ArrayList<MenuMasterVO>());
                this.mapContextMenus.get(voMenuComplete.getVarMenuContext()).add(voMenuComplete);

                // Adding to Menu Id Wise Allowed URLs List
                for (MenuMasterVO voMenuAllowed : lstAllowedMenuURL) {
                    List<String> lstURLs = this.mapUserAllowedMenus.get(voMenuAllowed.getVarMenuId());
                    if (lstURLs == null)
                        lstURLs = new ArrayList<String>();
                    lstURLs.add(voMenuAllowed.getVarURL());
                    this.mapUserAllowedMenus.put(voMenuAllowed.getVarMenuId(), lstURLs);
                }
            }

            if (mp.size() > 0) {
                this.mapMenusHirarchy = mp;
                return true;
            } else
                return false;
        } else
            return false;
    }

    private String getContext(String strURI) {
        String strContext = null;
        if (strURI != null || strURI != "") {
            if (strURI.indexOf("/") >= 0)
                strContext = strURI.substring(0, strURI.indexOf("/", 2));

        }
        return strContext;
    }

    public List<MenuMasterVO> getMenuList() {
        return lstUserMenus;
    }

}
