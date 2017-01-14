/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.action.task.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Сервис получения списка пользователей, если указан id групы
 *
 * @author inna
 */
@Service
public class UsersService {

    private static final Log LOG = LogFactory.getLog(UsersService.class);
    
    @Autowired
    private IdentityService identityService;

    public List<Map<String, String>> getUsersByGroup(String sID_Group) {
    	
    	List<Map<String, String>> amsUsers = new ArrayList<>(); // для возвращения результата, ибо возникает JsonMappingException и NullPointerException при записи картинки
        List<User> aoUsers = sID_Group != null ?
                identityService.createUserQuery().memberOfGroup(sID_Group).list() :
                identityService.createUserQuery().list();

        for (User oUser : aoUsers) {
            Map<String, String> mUserInfo = new LinkedHashMap();

            mUserInfo.put("sLogin", oUser.getId() == null ? "" : oUser.getId());
            mUserInfo.put("sPassword", oUser.getPassword() == null ? "" : oUser.getPassword());
            mUserInfo.put("sFirstName", oUser.getFirstName() == null ? "" : oUser.getFirstName());
            mUserInfo.put("sLastName", oUser.getLastName() == null ? "" : oUser.getLastName());
            mUserInfo.put("sEmail", oUser.getEmail() == null ? "" : oUser.getEmail());
             mUserInfo.put("FirstName", oUser.getFirstName() == null ? "" : oUser.getFirstName());
             mUserInfo.put("LastName", oUser.getLastName() == null ? "" : oUser.getLastName());
             mUserInfo.put("Email", oUser.getEmail() == null ? "" : oUser.getEmail());
            mUserInfo.put("Picture", null); // Временно ставим картинку null, позже будет изменение на Base64 или ссылка
            amsUsers.add(mUserInfo);
        }
    	
		return amsUsers;

    }


}
