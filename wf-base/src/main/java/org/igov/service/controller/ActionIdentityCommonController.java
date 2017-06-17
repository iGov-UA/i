package org.igov.service.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.igov.service.business.action.task.core.UsersService;
import org.igov.service.exception.CommonServiceException;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Controller
@Api(tags = { "ActionIdentityCommonController" })
@RequestMapping(value = "/action/identity")
public class ActionIdentityCommonController {

    private static final Logger log = LoggerFactory.getLogger(ActionIdentityCommonController.class);

    @Autowired
    private IdentityService identityService;

    @Autowired
    private TaskService taskService;
    
    @Autowired
    private UsersService usersService;

    /**
     * Добавление/обновление пользователя.
     *
     * @param sLogin       строка текст, логин пользователя для определения наличия пользователя в базе
     * //@param sPassword    строка текст, логин пользователя для определения наличия пользователя в базе
     * //@param sName        строка текст, имя пользователя
     * //@param sDescription строка текст, фамилия пользователя
     * //@param sEmail       строка текст, имейл пользователя, опциональный параметр
     */
    @ApiOperation(value = "Добавление/обновление пользователя. Если пользователь с указаным логином "
            + "существует, - то происходит перезапись существующих данных указанными."
            + "Если же пользователь с указанным логином не найден, - будет создана новая запись.")
    @RequestMapping(value = "/setUser", method = { /*RequestMethod.GET,*/ RequestMethod.POST })
    @ResponseBody
    public void setUser(
            @ApiParam(value = "строка текст, логин пользователя для определения наличия пользователя в базе", required = true) @RequestParam(value = "sLogin", required = true) String sLogin,
            //@ApiParam(value = "строка текст, пароль для пользователя", required = true) @RequestParam(value = "sPassword", required = true) String sPassword,
            //@ApiParam(value = "строка текст, имя пользователя", required = true) @RequestParam(value = "sName", required = true) String sName,
            //@ApiParam(value = "строка текст, фамилия пользователя", required = true) @RequestParam(value = "sDescription", required = true) String sDescription,
            //@ApiParam(value = "строка текст, имейл пользователя, опциональный параметр", required = false) @RequestParam(value = "sEmail", required = false) String sEmail,
            @ApiParam(value = "JSON-объект с параметрами: " +
                    "sPassword - (обязательный при создании нового пользователя) строка текст, логин пользователя для определения наличия пользователя в базе; " +
                    "sName - (обязательный) строка текст, имя пользователя; " +
                    "sDescription - (обязательный) строка текст, фамилия пользователя; " +
                    "sEmail - строка текст, имейл пользователя, опциональный параметр", required = true) @RequestBody String body)
            throws Exception {

        log.info("Method setUser startred");
        String sPassword = null;
        String sName = null;
        String sDescription = null;
        String sEmail = null;

        if(body != null){
            Map<String, Object> mBody;
            try {
                mBody = (Map<String, Object>) JSONValue.parse(body);
            } catch (Exception e){
                throw new IllegalArgumentException("Error parse JSON body: " + e.getMessage());
            }
            if(mBody != null){
                if (mBody.containsKey("sPassword")) {
                    sPassword = (String) mBody.get("sPassword");
                }
                if (mBody.containsKey("sName")) {
                    sName = (String) mBody.get("sName");
                } else {
                    throw new Exception("The sName in RequestBody is not defined");
                }
                if (mBody.containsKey("sDescription")) {
                    sDescription = (String) mBody.get("sDescription");
                } else {
                    throw new Exception("The sDescription in RequestBody is not defined");
                }
                if (mBody.containsKey("sEmail")) {
                    sEmail = (String) mBody.get("sEmail");
                }
            }
        }

        User oUser = identityService.createUserQuery().userId(sLogin).singleResult();
        if (oUser == null) {
            log.info("Creating new user");
            oUser = identityService.newUser(sLogin);
            if(sPassword == null || sPassword.equals("")){
                throw new Exception("The password for new User is not defined");
            } else {
                oUser.setPassword(sPassword);
            }
        }
        oUser.setFirstName(sName);
        oUser.setLastName(sDescription);
        if (sEmail != null) {
            oUser.setEmail(sEmail);
        }
        log.info("Saving user to database");
        identityService.saveUser(oUser);
    }

    /**
     * Добавление/обновление групы.
     *
     * @param sID   строка, которая содержит число, id групы
     * @param sName строка текст, название групы
     */
    @ApiOperation(value = "Добавление/обновление групы. Если група с указаным id "
            + "существует, - то происходит перезапись существующих данных указанными."
            + "Если же група с указанным id не найдена, - будет создана новая запись.")
    @RequestMapping(value = "/setGroup", method = { RequestMethod.GET })
    @ResponseBody
    public void setGroup(
            @ApiParam(value = "строка, которая содержит число, id групы", required = true) @RequestParam(value = "sID", required = true) String sID,
            @ApiParam(value = "строка текст, название групы", required = true) @RequestParam(value = "sName", required = true) String sName)
            throws Exception {

        log.info("Method setGroup startred");
        Group oGroup = identityService.createGroupQuery().groupId(sID).singleResult();
        if (oGroup == null) {
            log.info("Creating new group");
            oGroup = identityService.newGroup(sID);
        }
        oGroup.setName(sName);
        log.info("Saving to database");
        identityService.saveGroup(oGroup);
    }

    /**
     * Возвращает список груп, если указан логин пользователя, - выводит все его групы, иначе, по умолчанию - все групы.
     *
     * @param sLogin строка текст, логин пользователя, опциональный параметр
     */
    @ApiOperation(value =
            "Возвращает список груп, если указан логин пользователя, - выводит все его групы, иначе, по умолчанию"
                    + " возвращает все существующие групы.")
    @RequestMapping(value = "/getGroups", method = RequestMethod.GET)
    @ResponseBody
    public List<Group> getGroups(
            @ApiParam(value = "строка текст, логин пользователя, опциональный параметр", required = false) @RequestParam(value = "sLogin", required = false) String sLogin) {

        log.info("Method getGroups startred");
        return sLogin != null ?
                identityService.createGroupQuery().groupMember(sLogin).list() :
                identityService.createGroupQuery().list();
    }

    /**
     * Возвращает список пользователей, если указан id групы, - выводит всех ее пользователей, иначе, по умолчанию - всех пользователей.
     *
     * @param sID_Group строка, которая содержит число, id групы, опциональный параметр
     */
    @ApiOperation(value =
            "Возвращает список пользователей, если указан id групы, - выводит всех ее пользователей, иначе, по умолчанию"
                    + " возвращает всех существующих пользователей.")
    @RequestMapping(value = "/getUsers", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, String>> getUsers(
            @ApiParam(value = "строка, которая содержит число, id групы, опциональный параметр", required = false) @RequestParam(value = "sID_Group", required = false) String sID_Group) {

        log.info("Method getUsers startred");
        List<Map<String, String>> amsUsers = null;
        try {
        	amsUsers = usersService.getUsersByGroup(sID_Group);
    		
    	} catch (Exception e) {
    		log.error("FAIL: ", e);
        }

        return amsUsers;
    }

    /**
     * Удаляет пользователя с указанным логином
     *
     * @param sLogin строка текст, логин пользователя, которого необходимо удалить
     */
    @ApiOperation(value = "Удаляет пользователя с указанным логином")
    @RequestMapping(value = "/removeUser", method = RequestMethod.DELETE)
    @ResponseBody
    public void removeUser(
            @ApiParam(value = "строка текст, логин пользователя, которого необходимо удалить", required = true) @RequestParam(value = "sLogin", required = true) String sLogin)
            throws Exception {

        identityService.deleteUser(sLogin);
    }

    /**
     * Удаляет групу с указаным id. Если група содержит пользователей, - будет выброшена ошибка
     * которая будет содержать данные о списке пользователей в этой групе. Если же група имеет задание (таску)
     * то при попытке ее удалить будет получена ошибка, которая будет содержать данные о списке доступных заданий.
     *
     * @param sID строка, которая содержит число, id групы, которую необходимо удалить
     */
    @ApiOperation(value = "Удаляет групу с указаным id. Если група содержит пользователей, - будет выброшена ошибка "
            + "которая будет содержать данные о списке пользователей в этой групе. Если же група имеет задание (таску) "
            + "то при попытке ее удалить будет получена ошибка, которая будет содержать данные о списке доступных заданий.")
    @RequestMapping(value = "/removeGroup", method = RequestMethod.DELETE)
    @ResponseBody
    public void removeGroup(
            @ApiParam(value = "строка, которая содержит число, id групы", required = true) @RequestParam(value = "sID", required = true) String sID)
            throws CommonServiceException {

        log.info("Method removeGroup startred");
        List<User> aoUsers = identityService.createUserQuery().memberOfGroup(sID).list();
        if (aoUsers.size() != 0) {
            List<String> asLogins = new ArrayList<>();
            aoUsers.forEach(u -> asLogins.add(u.getId()));
            log.warn("Can not remove group { } because it contains users { }", sID, asLogins);
            throw new CommonServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Can not remove group " + sID +
                    " because it contains users " + asLogins);
        }
        List<Task> aoTasks = taskService.createTaskQuery().taskCandidateGroup(sID).list();
        if (aoTasks.size() != 0) {
            List<String> asTasks = new ArrayList<>();
            aoTasks.forEach(t -> asTasks.add(t.getId()));
            log.warn("Can not remove group { } because it has accessible tasks { }", sID, aoTasks);
            throw new CommonServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Can not remove group " + sID + " because it has accessible tasks " + aoTasks);
        }

        identityService.deleteGroup(sID);
    }

    /**
     * Добавляет пользователя как члена групы
     *
     * @param sID_Group строка текст, айди групы, в которую нужно добавить пользователя
     * @param sLogin    строка текст, логин пользователя, которого необходимо добавить
     */
    @ApiOperation(value = "Добавляет пользователя как члена групы")
    @RequestMapping(value = "/setUserGroup", method = RequestMethod.POST)
    @ResponseBody
    public void setUserGroup(
            @ApiParam(value = "строка текст, айди групы, в которую нужно добавить пользователя", required = true) @RequestParam(value = "sID_Group", required = true) String sID_Group,
            @ApiParam(value = "строка текст, логин пользователя, которого необходимо добавить", required = true) @RequestParam(value = "sLogin", required = true) String sLogin)
            throws Exception {
        if(!StringUtils.isAnyEmpty(sID_Group,sLogin))
        {
            log.info("Group id and user login are not empty");
            identityService.createMembership(sLogin, sID_Group);
            log.info("Membership for user "+sLogin+" in group "+sID_Group+" created");
        }
    }

    /**
     * Удаляет членство пользователя в групе
     *
     * @param sID_Group строка текст, айди групы, из которой необходимо удалить юзера
     * @param sLogin    строка текст, логин пользователя, которого необходимо удалить
     */
    @ApiOperation(value = "Удаляет членство пользователя в групе")
    @RequestMapping(value = "/removeUserGroup", method = RequestMethod.DELETE)
    @ResponseBody
    public void removeUserGroup(
            @ApiParam(value = "строка текст, айди групы, из которой необходимо удалить юзера", required = true) @RequestParam(value = "sID_Group", required = true) String sID_Group,
            @ApiParam(value = "строка текст, логин пользователя, которого необходимо удалить", required = true) @RequestParam(value = "sLogin", required = true) String sLogin)
            throws Exception {
        if(!StringUtils.isAnyEmpty(sID_Group,sLogin))
        {
            log.info("Group id and user login are not empty");
            identityService.deleteMembership(sLogin, sID_Group);
            log.info("Membership for user "+sLogin+" in group "+sID_Group+" removed");
        }
    }
    
    @ApiOperation(value = "Получение списка email юзеров по ИД группы", notes = "##### Пример:\n"
            + "https://alpha.test.region.igov.org.ua/wf/service/action/identity/getUsersEmailByGroup?sID_Group=GrekD \n")
    @RequestMapping(value = "/getUsersEmailByGroup", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getUsersEmailByGroup(@ApiParam(value = "ИД группы", required = true) @RequestParam(value = "sID_Group", required = true) String sID_Group){
    	
    	List<String> usersByGroup = usersService.getUsersEmailByGroup(sID_Group);
		return usersByGroup;
    	
    }
    
    
    @ApiOperation(value = "Получение списка email юзеров которые были выбраны в таблице", notes = "##### Пример:\n"
            + "https://alpha.test.region.igov.org.ua/wf/service/action/identity/getUsersEmailFromTable?snID_Process_Activiti=1&sID_Table=sTableViewed&sID_FieldTable=sEmail \n")
    @RequestMapping(value = "/getUsersEmailFromTable", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getUsersEmailFromTable(
    		@ApiParam(value = "ид процесса", required = true) @RequestParam(value = "snID_Process_Activiti", required = true) String snID_Process_Activiti,
    		@ApiParam(value = "ид таблицы (переменная из БП)", required = true) @RequestParam(value = "sID_Table", required = true) String sID_Table,
    		@ApiParam(value = "ид столбца таблицы (с какого столбца вытянуть емайлы)", required = true) @RequestParam(value = "sID_FieldTable", required = true) String sID_FieldTable) throws Exception{
    	
    	List<String> usersEmailFromTable = usersService.getUsersEmailFromTable(snID_Process_Activiti,sID_Table,sID_FieldTable);
		return usersEmailFromTable;
    	
    }

}