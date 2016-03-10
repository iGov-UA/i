package org.igov.service.controller;

import io.swagger.annotations.*;
import org.activiti.engine.ProcessEngines;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.mail.EmailException;
import org.igov.io.db.kv.temp.exception.RecordInmemoryException;
import org.igov.model.access.vo.AccessRightVO;
import org.igov.model.action.task.core.entity.LoginResponse;
import org.igov.model.action.task.core.entity.LoginResponseI;
import org.igov.model.action.task.core.entity.LogoutResponse;
import org.igov.model.action.task.core.entity.LogoutResponseI;
import org.igov.service.business.access.AccessService;
import org.igov.service.exception.AccessServiceException;
import org.igov.service.exception.CommonServiceException;
import org.igov.service.exception.HandlerBeanValidationException;
import org.igov.util.JSON.JsonRestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.internet.AddressException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * User: goodg_000
 * Date: 06.10.2015
 * Time: 22:57
 */
@Controller
@Api(tags = { "AccessCommonController — Доступ общий (права доступа к сервисам)"})
@RequestMapping(value = "/access")
public class AccessCommonController {

    private static final Logger LOG = LoggerFactory.getLogger(AccessCommonController.class);
    
    @Autowired
    private AccessService oAccessService;

    /**
     * Логин пользователя в систему. Возращает признак успеха/неудачи входа.
     * true - Пользователь авторизирован
     * false - Имя пользователя или пароль некорректны
     *
     * @param login    - Логин пользователя
     * @param password - Пароль пользователя
     * @return {"session":"true"} -- Пользователь авторизирован
     * OR  {"session":"false"}- Имя пользователя или пароль некорректны
     * @throws AccessServiceException
     */
    @ApiOperation(value = "Логин пользователя", notes = "##### Response:\n"
       + "\n```json\n" 
       + "  {\"session\":\"true\"}\n"
       + "\n```\n" 
       + "Пример:\n"
       + "https://test.region.igov.org.ua/wf/access/login?sLogin=kermit&sPassword=kermit\n")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Возращает признак успеха/неудачи входа:\n "
            + "- **true** - Пользователь авторизирован; \n"
            + "- **false** - Имя пользователя или пароль некорректны. \n") })
    @RequestMapping(value = { "/login", "/login-v2" }, method = RequestMethod.POST)
    public
    @ResponseBody
    LoginResponseI login(
    		@ApiParam(value = "Строка логин пользователя", required = true) @RequestParam(value = "sLogin") String login,
    		@ApiParam(value = "Строка пароль пользователя", required = true) @RequestParam(value = "sPassword") String password, HttpServletRequest request)
            throws AccessServiceException {
        if (ProcessEngines.getDefaultProcessEngine().getIdentityService().checkPassword(login, password)) {
            request.getSession(true);
            return new LoginResponse(Boolean.TRUE.toString());
        } else {
            throw new AccessServiceException(AccessServiceException.Error.LOGIN_ERROR, "Login or password invalid");
        }
    }

    /**
     * Логаут пользователя (наличие cookie JSESSIONID):
     */
    @ApiOperation(value = " Аутентификация пользователя. Логаут пользователя (наличие cookie JSESSIONID)", notes = "##### \n"
     + "Response:\n"
     + "\n```json\n" 
     + "  {\"session\":\"97AE7CA414A5DA85749FE379CC843796\"}\n"
     + "\n```\n")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Возращает JSESSIONID") })
    @RequestMapping(value = "/logout", method = { RequestMethod.DELETE, RequestMethod.POST })
    public
    @ResponseBody
    LogoutResponseI logout(HttpServletRequest request) throws AccessServiceException {
        HttpSession session = request.getSession();
        if (session.isNew()) {
            throw new AccessServiceException(AccessServiceException.Error.LOGOUT_ERROR,
                    "Client doesn't have a valid server session");
        } else {
            session.invalidate();
            return new LogoutResponse(session.getId());
        }
    }
    
    /**
     * Возврат списка сервисов доступных пользователю
     * 
     * @param sLogin — Строка имя пользователя
     */
    @ApiOperation(value = "Возврат списка сервисов доступных пользователю", notes = "##### "
      + "Request:\n"
      + "\n```\n"
      + " sLogin=TestLogin\n"
      + "\n```\n"
      + "Response:\n"
      + "\n```json\n" 
      + "  [\n"
      + "    \"TestService\"\n"
      + "  ]\n"
      + "\n```\n"
      + "Пример:\n"
      + "https://test.region.igov.org.ua/wf/service/access/getAccessServiceLoginRight?sLogin=TestLogin")
    @RequestMapping(value = "/getAccessServiceLoginRight", method = RequestMethod.GET)
    public ResponseEntity getAccessServiceLoginRight(@ApiParam(value = "Строка логин пользователя", required = true) @RequestParam(value = "sLogin") String sLogin) {
        return JsonRestUtils.toJsonResponse(oAccessService.getAccessibleServices(sLogin));
    }

    /**
     * Сохранение разрешения на доступ к сервису для пользователя
     * 
     * @param sLogin — Строка имя пользователя
     * @param sService — Строка название сервиса
     * @param sHandlerBean — Опцинальный параметр: Строка имя спрингового бина реализующего интерфейс AccessServiceLoginRightHandler, который будет заниматься проверкой прав доступа для данной записи. При сохранении проверяется наличие такого бина, и если его нет - то будет выброшена ошибка.
     */
    @ApiOperation(value = "Сохранение разрешения на доступ к сервису для пользователя", notes = "##### Примеры:\n"
     + "https://test.region.igov.org.ua/wf/service/access/setAccessServiceLoginRight\n"
     + "- sLogin=SomeLogin\n"
     + "- sService=access/hasAccessServiceLoginRight\n"
     + "\n```\n"
     + "  Ответ: Status 200\n"
     + "\n```\n"
     + "- sLogin=SomeLogin\n"
     + "- sService=access/hasAccessServiceLoginRight\n"
     + "- sHandlerBean=WrongBean\n"
     + "Ответ:\n"	
     + "\n```json\n"
     + "  {\n"
     + "    \"code\": \"SYSTEM_ERR\",\n"
     + "    \"message\": \"No bean named 'WrongBean' is defined\"\n"
     + "  }\n"
     + "\n```\n")
    @RequestMapping(value = "/setAccessServiceLoginRight", method = RequestMethod.POST)
    @ApiResponses(value = { @ApiResponse(code = 500, message = "Ошибка бизнес-процесса")} )
    public void setAccessServiceLoginRight(@ApiParam(value = "Строка логин пользователя", required = true) @RequestParam(value = "sLogin") String sLogin,
    		@ApiParam(value = "Строка название сервиса", required = true) @RequestParam(value = "sService") String sService,
    		@ApiParam(value = "Опцинальный параметр: "
                        + "Строка имя спрингового бина реализующего интерфейс AccessServiceLoginRightHandler, "
                        + "который будет заниматься проверкой прав доступа для данной записи. "
                        + "При сохранении проверяется наличие такого бина,"
                        + " и если его нет - то будет выброшена ошибка", required = false) @RequestParam(value = "sHandlerBean", required = false) String sHandlerBean,
            HttpServletResponse response)
            throws CommonServiceException {
        try {

            oAccessService.saveOrUpdateAccessServiceLoginRight(sLogin, sService, sHandlerBean);
            response.setStatus(HttpStatus.OK.value());

        } catch (HandlerBeanValidationException e) {
            LOG.warn("Error: {}", e.getMessage());
            LOG.trace("FAIL:",e);
            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE, e.getMessage());
        }
    }

    /**
     * Удаление разрешения на доступ к сервису для пользователя
     * 
     * @param sLogin — Строка имя пользователя
     * @param sService — Строка название сервиса
     */
    @ApiOperation(value = "Удаление разрешения на доступ к сервису для пользователя", notes = "##### Примеры:\n\n"
	+ "https://test.region.igov.org.ua/wf/service/access/removeAccessServiceLoginRight?sLogin=TestLogin&sService=TestService\n\n"
	+ "\n```\n"
	+ "  Ответ: Status 200\n"
	+ "\n```\n"
	+ "https://test.region.igov.org.ua/wf/service/access/removeAccessServiceLoginRight?sLogin=FakeLogin&sService=TestService\n"
	+ "\n```\n"
	+ "  Ответ: Status 304\n"
	+ "\n```\n")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Запись успешно удалена"),
    	      @ApiResponse(code = 304, message = "Такая запись не найдена") })
    @RequestMapping(value = "/removeAccessServiceLoginRight", method = RequestMethod.DELETE)
    public void setAccessServiceLoginRight(@ApiParam(value = "Строка логин пользователя", required = true) @RequestParam(value = "sLogin") String sLogin,
    		@ApiParam(value = "Строка название сервиса", required = true) @RequestParam(value = "sService") String sService,
            HttpServletResponse response) {
        if (oAccessService.removeAccessServiceLoginRight(sLogin, sService)) {
            response.setStatus(HttpStatus.OK.value());
        } else {
            response.setStatus(HttpStatus.NOT_MODIFIED.value());
        }
    }

    /**
     * Проверка разрешения на доступ к сервису для пользователя
     * 
     * @param sLogin — Строка имя пользователя для которого проверяется доступ
     * @param sService — Строка сервиса
     * @param sData — Строка параметров к сервису (опциональный параметр, формат передачи пока не определен). Если задан бин sHandlerBean (см. ниже) то он может взять на себя проверку доступности сервиса для данного набора параметров.
     */
    @ApiOperation(value = "Проверка разрешения на доступ к сервису для пользователя", notes = "##### Пример:\n"
        + "https://test.region.igov.org.ua/wf/service/access/hasAccessServiceLoginRight?sLogin=SomeLogin&sService=access/hasAccessServiceLoginRight&sMethod=GET\n"
	+ "\n```\n"
	+ "Ответ false\n"
	+ "\n```\n")
    @RequestMapping(value = "/hasAccessServiceLoginRight", method = RequestMethod.GET)
    @ApiResponses(value = { @ApiResponse(code = 500, message = "Ошибка бизнес-процесса"),
        @ApiResponse(code = 200, message = "true - если у пользоватля с логином sLogin есть доступ к рест-сервиcу sService "
                + "при вызове его с аргументами sData,"
                + " или false - если доступа нет.")} )
    public ResponseEntity hasAccessServiceLoginRight(
            @ApiParam(value = "Строка логин пользователя", required = true) @RequestParam(value = "sLogin") String sLogin,
    		@ApiParam(value = "Строка название сервиса", required = true) @RequestParam(value = "sService") String sService,
    		@ApiParam(value = "Строка параметр со строкой параметров к сервису (формат передачи пока не определен)", required = false) @RequestParam(value = "sData", required = false) String sData,
            @ApiParam(value = "Метод доступа к свервису (GET или POST или другиие)", required = false) @RequestParam(value = "sMethod") String sMethod)
            throws CommonServiceException {

        try {
            return JsonRestUtils.toJsonResponse(oAccessService.hasAccessToService(sLogin, sService, sData, sMethod));
        } catch (HandlerBeanValidationException e) {
            LOG.warn("Error: {}", e.getMessage());
            LOG.trace("FAIL:", e);
            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE, e.getMessage());
        }
    }

    @RequestMapping(value = "/getAccessServiceLoginRoles", method = RequestMethod.GET)
    public ResponseEntity getAccessServiceLoginRoles (
            @ApiParam(value = "Строка логин пользователя", required = true) @RequestParam(value = "sLogin") String sLogin) {
        return JsonRestUtils.toJsonResponse(oAccessService.getAccessServiceLoginRoles(sLogin));
    }

    @RequestMapping(value = "/setAccessServiceLoginRole", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity setAccessServiceLoginRole (
            @ApiParam(value = "номер-ИД", required = false) @RequestParam(value = "nID", required = false) Long nID,
            @ApiParam(value = "Строка логин пользователя", required = true) @RequestParam(value = "sLogin", required = true) String sLogin,
            @ApiParam(value = "номер-ИД роли", required = true) @RequestParam(value = "nID_AccessServiceRole", required = true) Long nID_AccessServiceRole ) {
        return JsonRestUtils.toJsonResponse(oAccessService.setAccessServiceLoginRole(nID, sLogin, nID_AccessServiceRole));
    }

    @RequestMapping(value = "/getAccessServiceRoleRights", method = RequestMethod.GET)
    public ResponseEntity getAccessServiceRoleRights (
            @ApiParam(value = "номер-ИД роли", required = true) @RequestParam(value = "nID_AccessServiceRole") Long nID_AccessServiceRole) {
        return JsonRestUtils.toJsonResponse(oAccessService.getAccessServiceRoleRights(nID_AccessServiceRole));
    }

    @RequestMapping(value = "/setAccessServiceRole", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity setAccessServiceRole (
            @ApiParam(value = "номер-ИД роли", required = false) @RequestParam(value = "nID", required = false) Long nID,
            @ApiParam(value = "строка-название", required = true) @RequestParam(value = "sName") String sName) {
        return JsonRestUtils.toJsonResponse(oAccessService.setAccessServiceRole(nID, sName));
    }

    @RequestMapping(value = "/removeAccessServiceRole", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity removeAccessServiceRole (
            @ApiParam(value = "номер-ИД роли", required = true) @RequestParam(value = "nID") Long nID) {
        oAccessService.removeAccessServiceRole(nID);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/removeAccessServiceRight", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity removeAccessServiceRight (
            @ApiParam(value = "номер-ИД права", required = true) @RequestParam(value = "nID") Long nID) {
        oAccessService.removeAccessServiceRight(nID);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/setAccessServiceRight", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity setAccessServiceRight (
            @ApiParam(value = "номер-ИД права", required = true) @RequestParam(value = "nID") Long nID,
            @ApiParam(value = "строка-название", required = true) @RequestParam(value = "sName", required = true) String sName,
            @ApiParam(value = "номер порядка (проверки)", required = false) @RequestParam(value = "nOrder", required = false) Integer nOrder,
            @ApiParam(value = "строка-сервис", required = false) @RequestParam(value = "sService", required = false) String sService,
            @ApiParam(value = "строка-название метода вызова", required = false) @RequestParam(value = "saMethod", required = false) String saMethod,
            @ApiParam(value = "строка-название бина-обработчика", required = false) @RequestParam(value = "sHandlerBean", required = false) String sHandlerBean,
            @ApiParam(value = "булевый, запретить доступ к сервису?", required = false) @RequestParam(value = "bDeny", required = false) Boolean bDeny) {
        AccessRightVO accessRightVO = new AccessRightVO();
        accessRightVO.setnID(nID);
        accessRightVO.setsName(sName);
        accessRightVO.setnOrder(nOrder);
        accessRightVO.setsService(sService);
        accessRightVO.setSaMethod(saMethod);
        accessRightVO.setsHandlerBean(sHandlerBean);
        accessRightVO.setbDeny(BooleanUtils.isTrue(bDeny));

        return JsonRestUtils.toJsonResponse(oAccessService.setAccessServiceRight(accessRightVO));
    }

    @RequestMapping(value = "/getAccessServiceRights", method = RequestMethod.GET)
    public ResponseEntity getAccessServiceRoleRights (
            @ApiParam(value = "номер-ИД права", required = false) @RequestParam(value = "nID", required = false) Long nID,
            @ApiParam(value = "строка-сервис (маска)", required = false) @RequestParam(value = "sService", required = false) String sService,
            @ApiParam(value = "строка-название метода вызова", required = false) @RequestParam(value = "saMethod", required = false) String saMethod,
            @ApiParam(value = "строка-название бина-обработчика", required = false) @RequestParam(value = "sHandlerBean", required = false) String sHandlerBean) {
        return JsonRestUtils.toJsonResponse(oAccessService.getAccessServiceRights(nID, sService, saMethod, sHandlerBean));
    }

    /**
     * Сервис верификации контакта - электронного адреса
     * 
     * @param sQuestion — Строка электронный адрес
     * @param sAnswer — Строка ответ на запрос (код)
     * 
     * 
     */
    @ApiOperation(value = "Сервис верификации контакта - электронного адреса", notes = "##### Примеры:\n"
            + "https://test.region.igov.org.ua/wf/service/access/verifyContactEmail?sQuestion=\\test@igov.org.ua\n"
            + "Response\n"
            + "\n```json\n"
            + "{\n"
            + "    \"bVerified\":true,\n"
            + "}\n"
            + "\n```\n")
    @RequestMapping(value = "/verifyContactEmail", method = RequestMethod.GET)
    public
    @ResponseBody
    Map<String, String> verifyContactEmail(
    		@ApiParam(value = "Строка запрос (электронный адрес)", required = true) @RequestParam(value = "sQuestion") String sQuestion,
    		@ApiParam(value = "Строка ответ (код )", required = false) 
    		@RequestParam(value = "sAnswer", required=false) String sAnswer) throws CommonServiceException, EmailException, RecordInmemoryException {
        Map<String, String> res = new HashMap<String, String>();
    	try {
            res = oAccessService.getVerifyContactEmail(sQuestion, sAnswer);
    	} catch (AddressException ex) {
    		LOG.warn("Email address {} is not correct", sQuestion);
    		LOG.warn("FAIL: ", ex);
            res.put("bVerified", "false");
    	}
        return res;
    }
}
