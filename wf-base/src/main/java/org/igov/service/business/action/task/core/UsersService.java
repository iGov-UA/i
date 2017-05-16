/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.action.task.core;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.identity.User;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.igov.service.business.document.DocumentStepService;
import org.igov.service.conf.AttachmetService;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Сервис получения списка пользователей, если указан id групы
 *
 * @author inna
 */
@Component("usersService")
@Service
public class UsersService {

    private static final Logger LOG = LoggerFactory.getLogger(UsersService.class);

    
    @Autowired
    private IdentityService identityService;
    
    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    private AttachmetService oAttachmetService;

    public List<Map<String, String>> getUsersByGroup(String sID_Group) {
    	
    	List<Map<String, String>> amsUsers = new ArrayList<>(); // для возвращения результата, ибо возникает JsonMappingException и NullPointerException при записи картинки
        List<User> aoUsers = sID_Group != null ?
                identityService.createUserQuery().memberOfGroup(sID_Group).list() :
                identityService.createUserQuery().list();

        for (User oUser : aoUsers) {
            Map<String, String> mUserInfo = new LinkedHashMap();

            mUserInfo.put("sLogin", oUser.getId() == null ? "" : oUser.getId());
           // mUserInfo.put("sPassword", oUser.getPassword() == null ? "" : oUser.getPassword());
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
	
    public List<String> getUsersLoginByGroup(String sID_Group) {
    	
    	List<String> aUsers = new ArrayList<>(); // для возвращения результата, ибо возникает JsonMappingException и NullPointerException при записи картинки
        List<User> aoUsers = sID_Group != null ?
                identityService.createUserQuery().memberOfGroup(sID_Group).list() :
                identityService.createUserQuery().list();
        for (User oUser : aoUsers) {
            aUsers.add(oUser.getId());
        }
        return aUsers;
    }
    
    
    public List<String> getUsersEmailByGroup(String sID_Group) {
    	
    	List<String> aUsersEmail = new ArrayList<>(); 
        List<User> aoUsers = sID_Group != null ?
                identityService.createUserQuery().memberOfGroup(sID_Group).list() :
                identityService.createUserQuery().list();
        for (User oUser : aoUsers) {
        	aUsersEmail.add(oUser.getEmail());
        }
        return aUsersEmail;
    }
    
    
    public List<String> getUsersEmailFromTable(String snID_Process_Activiti, String sID_Table, String sID_FieldTable) throws Exception {
        List<String> sEmail = new LinkedList();
        try {

            if (sID_FieldTable == null) {
                sID_FieldTable = "sEmail";
            }

            String sValue = (String) runtimeService.getVariable(snID_Process_Activiti, sID_Table);

            if (sValue!=null&&sValue.startsWith("{")) {// TABLE
                JSONParser parser = new JSONParser();

                org.json.simple.JSONObject oTableJSONObject = (org.json.simple.JSONObject) parser.parse(sValue);

                InputStream oAttachmet_InputStream = oAttachmetService.getAttachment(null, null,
                        (String) oTableJSONObject.get("sKey"), (String) oTableJSONObject.get("sID_StorageType"))
                        .getInputStream();

                org.json.simple.JSONObject oJSONObject = (org.json.simple.JSONObject) parser
                        .parse(IOUtils.toString(oAttachmet_InputStream, "UTF-8"));
                LOG.info("oTableJSONObject in listener: " + oJSONObject.toJSONString());

                org.json.simple.JSONArray aJsonRow = (org.json.simple.JSONArray) oJSONObject.get("aRow");

                if (aJsonRow != null) {
                    for (int i = 0; i < aJsonRow.size(); i++) {
                        org.json.simple.JSONObject oJsonField = (org.json.simple.JSONObject) aJsonRow.get(i);
                        LOG.info("oJsonField in getUsersEmailByGroupFromTable is {}", oJsonField);
                        if (oJsonField != null) {
                            org.json.simple.JSONArray aJsonField = (org.json.simple.JSONArray) oJsonField.get("aField");
                            LOG.info("aJsonField in getUsersEmailByGroupFromTable is {}", aJsonField);
                            if (aJsonField != null) {
                                for (int j = 0; j < aJsonField.size(); j++) {
                                    org.json.simple.JSONObject oJsonMap = (org.json.simple.JSONObject) aJsonField
                                            .get(j);
                                    LOG.info("oJsonMap in getUsersEmailByGroupFromTable is {}", oJsonMap);
                                    if (oJsonMap != null) {
                                        Object oId = oJsonMap.get("id");
                                        if (((String) oId).equals(sID_FieldTable)) {
                                            Object oValue = oJsonMap.get("value");
                                            if (oValue != null) {
                                                LOG.info("oValue in getUsersEmailByGroupFromTable is {}", oValue);
                                                sEmail.add((String) oValue);
                                            } else {
                                                LOG.info("oValue in getUsersEmailByGroupFromTable is null");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    LOG.info("JSON array is null in getUsersEmailByGroupFromTable is null");
                }
            } else {
            	sEmail.add(sValue);
            }
        } catch (Exception oException) {
            LOG.error("ERROR:" + oException.getMessage() + " (" + "snID_Process_Activiti=" + snID_Process_Activiti + ""
                    + ",sID_Table=" + sID_Table + ")", oException);
            throw oException;
        }
        return sEmail;
    }

}
