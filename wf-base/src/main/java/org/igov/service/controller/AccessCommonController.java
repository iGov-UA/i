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
import org.igov.service.exchange.SubjectCover;
import org.igov.util.JSON.JsonRestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.mail.internet.AddressException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * User: goodg_000
 * Date: 06.10.2015
 * Time: 22:57
 */
@Controller
@Api(tags = { "AccessCommonController — Доступ общий (права доступа к сервисам)" })
@RequestMapping(value = "/access")
public class AccessCommonController {

    private static final Logger LOG = LoggerFactory.getLogger(AccessCommonController.class);

    @Autowired
    private AccessService oAccessService;

    @Autowired
    private SubjectCover oSubjectCover;

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
            @ApiParam(value = "Строка пароль пользователя", required = true) @RequestParam(value = "sPassword") String password,
            HttpServletRequest request)
            throws AccessServiceException {
        if (ProcessEngines.getDefaultProcessEngine().getIdentityService().checkPassword(login, password)) {
            request.getSession(true);
            return new LoginResponse(Boolean.TRUE.toString());
        } else {
            throw new AccessServiceException(AccessServiceException.Error.LOGIN_ERROR, "Login or password invalid");
        }
    }

    //task #1214
    /**
     * Авторизация пользователя с возвратом структуры обьектов по субьекту
     *
     * @param sLogin    - Логин пользователя
     * @param sPassword - Пароль пользователя, передается в теле запроса
     * @return Возвращает структуру объектов по конкретному субъекту
     * @throws AccessServiceException
     */
    @ApiOperation(value = "Авторизирует пользователя и возвращает соответсвующую ему структуру объектов", notes = "##### При вводе корректных данных"
            + " метод возвращает код 200 и, при этом, открывается сессия и возвращается структура объектов, которая соответсвует этому пользователю.\n"
            + " Если же пользователя с таким логином и паролем не существует, - то вернется ошибка с кодом LI_0001 и сообщением Login or password invalid.\n")
    @RequestMapping(value = { "/loginSubject" }, method = RequestMethod.POST, headers = {
            "Content-type=application/json" })
    public
    @ResponseBody
    Map loginSubject(
            @ApiParam(value = "Строка логин пользователя", required = true, defaultValue = "kermit") @RequestParam(value = "sLogin") String sLogin,
            @ApiParam(value = "Строка пароль пользователя", required = true, defaultValue = "kermit") @RequestBody String sPassword,
            HttpServletRequest request)
            throws AccessServiceException {
        LOG.info("Method loginSubjetct started");
        LOG.info("Working values " + sLogin + " " + sPassword);
        if (ProcessEngines.getDefaultProcessEngine().getIdentityService().checkPassword(sLogin, sPassword)) {
            LOG.info("Login and password are correct");
            request.getSession(true);
            Set<String> asAccounts = new HashSet<>();
            asAccounts.add(sLogin);
            LOG.info("Calling method getSubjectsBy with login " + sLogin);
            return oSubjectCover.getSubjectsBy(asAccounts);
        } else {
            throw new AccessServiceException(AccessServiceException.Error.LOGIN_ERROR, "Login or password invalid");
        }
    }

    /**
     * Логаут пользователя (наличие cookie JSESSIONID):
     */
    @ApiOperation(value = " Аутентификация пользователя. Логаут пользователя (наличие cookie JSESSIONID)", notes =
            "##### \n"
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
     * Сервис верификации контакта - электронного адреса
     *
     * @param sQuestion — Строка электронный адрес
     * @param sAnswer   — Строка ответ на запрос (код)
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
            @RequestParam(value = "sAnswer", required = false) String sAnswer)
            throws CommonServiceException, EmailException, RecordInmemoryException {
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

    /**
     * Проверка разрешения на доступ к сервису для пользователя
     *
     * @param sLogin   — Строка имя пользователя для которого проверяется доступ
     * @param sService — Строка сервиса
     * @param sData    — Строка параметров к сервису (опциональный параметр, формат передачи пока не определен). Если задан бин sHandlerBean (см. ниже) то он может взять на себя проверку доступности сервиса для данного набора параметров.
     */
    @ApiOperation(value = "Проверка разрешения на доступ к сервису для пользователя", notes = "##### Пример:\n"
            + "https://test.region.igov.org.ua/wf/service/access/hasAccessServiceLoginRight?sLogin=SomeLogin&sService=access/hasAccessServiceLoginRight&sMethod=GET\n"
            + "\n```\n"
            + "Ответ false\n"
            + "\n```\n")
    @RequestMapping(value = "/hasAccessServiceLoginRight", method = RequestMethod.GET)
    @ApiResponses(value = { @ApiResponse(code = 500, message = "Ошибка бизнес-процесса"),
            @ApiResponse(code = 200, message =
                    "true - если у пользоватля с логином sLogin есть доступ к рест-сервиcу sService "
                            + "при вызове его с аргументами sData,"
                            + " или false - если доступа нет.") })
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

    // -------------- AccessServiceLoginRoles --------------------------------------------------------------------------

    @ApiOperation(value = "Получить массив ролей логина")
    @RequestMapping(value = "/getAccessServiceLoginRoles", method = RequestMethod.GET)
    public ResponseEntity getAccessServiceLoginRoles(
            @ApiParam(value = "Строка логин пользователя", required = true) @RequestParam(value = "sLogin") String sLogin) {
        return JsonRestUtils.toJsonResponse(oAccessService.getAccessServiceLoginRoles(sLogin));
    }

    @ApiOperation(value = "Установить роль логина")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Результирующий обьект роли логина") })
    @RequestMapping(value = "/setAccessServiceLoginRole", method = { RequestMethod.POST, RequestMethod.GET })
    public ResponseEntity setAccessServiceLoginRole(
            @ApiParam(value = "номер-ИД", required = false) @RequestParam(value = "nID", required = false) Long nID,
            @ApiParam(value = "Строка логин пользователя", required = true) @RequestParam(value = "sLogin", required = true) String sLogin,
            @ApiParam(value = "номер-ИД роли", required = true) @RequestParam(value = "nID_AccessServiceRole", required = true) Long nID_AccessServiceRole) {
        return JsonRestUtils
                .toJsonResponse(oAccessService.setAccessServiceLoginRole(nID, sLogin, nID_AccessServiceRole));
    }

    @ApiOperation(value = "Удаление роли с логина", notes = "Удаление происходит либо по nID связки, либо по паре" +
            "значений sLogin и nID_AccessServiceRole.")
    @RequestMapping(value = "/removeAccessServiceLoginRole", method = { RequestMethod.POST, RequestMethod.GET })
    public ResponseEntity removeAccessServiceLoginRole(
            @ApiParam(value = "номер-ИД связки", required = false) @RequestParam(value = "nID", required = false) Long nID,
            @ApiParam(value = "строка-логин", required = false) @RequestParam(value = "sLogin", required = false) String sLogin,
            @ApiParam(value = "номер-ИД роли", required = false) @RequestParam(value = "nID_AccessServiceRole", required = false) Long nID_AccessServiceRole) {
        if (nID != null && sLogin == null && nID_AccessServiceRole == null) {
            oAccessService.removeAccessServiceLoginRole(nID);
        } else if (nID == null && sLogin != null && nID_AccessServiceRole != null) {
            oAccessService.removeAccessServiceLoginRole(sLogin, nID_AccessServiceRole);
        } else {
            return new ResponseEntity<>("Нужно указать nID или (sLogin и nID_AccessServiceRole)", HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    // -------------- AccessServiceRole --------------------------------------------------------------------------------

    @ApiOperation(value = "Получить массив объеков ролей с правами в виде дерева", notes = "Если задан" +
            " nID_AccessServiceRole то возвращает права для этой роли, если нет - то для всех ролей.")
    @RequestMapping(value = "/getAccessServiceRoleRights", method = RequestMethod.GET)
    public ResponseEntity getAccessServiceRoleRights(
            @ApiParam(value = "номер-ИД роли", required = false) @RequestParam(value = "nID_AccessServiceRole", required = false) Long nID_AccessServiceRole) {
        return JsonRestUtils.toJsonResponse(oAccessService.getAccessServiceRoleRights(nID_AccessServiceRole));
    }

    @ApiOperation(value = "Добавить/обновить объект-роли",
            notes = "Если задан nID, то обновляет обьект роли, если нет - создает новый")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Результирующий обьект роли") })
    @RequestMapping(value = "/setAccessServiceRole", method = { RequestMethod.POST, RequestMethod.GET })
    public ResponseEntity setAccessServiceRole(
            @ApiParam(value = "номер-ИД роли", required = false) @RequestParam(value = "nID", required = false) Long nID,
            @ApiParam(value = "строка-название", required = true) @RequestParam(value = "sName") String sName) {
        return JsonRestUtils.toJsonResponse(oAccessService.setAccessServiceRole(nID, sName));
    }

    @ApiOperation(value = "Удалить объект-роли")
    @RequestMapping(value = "/removeAccessServiceRole", method = { RequestMethod.POST, RequestMethod.GET })
    public ResponseEntity removeAccessServiceRole(
            @ApiParam(value = "номер-ИД роли", required = true) @RequestParam(value = "nID") Long nID) {
        oAccessService.removeAccessServiceRole(nID);
        return new ResponseEntity(HttpStatus.OK);
    }

    // -------------- AccessServiceRoleRight ---------------------------------------------------------------------------

    @ApiOperation(value = "Добавить/обновить объект-связку роли на право доступа к сервису",
            notes = "Если задан nID, то обновляет обьект связку роли на право, если нет - создает новый")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Результирующий обьект-связку роли на право доступа к сервису") })
    @RequestMapping(value = "/setAccessServiceRoleRight", method = { RequestMethod.POST, RequestMethod.GET })
    public ResponseEntity setAccessServiceRoleRight(
            @ApiParam(value = "номер-ИД связки права и роли", required = false) @RequestParam(value = "nID", required = false) Long nID,
            @ApiParam(value = "строка логин пользователя", required = true) @RequestParam(value = "nID_AccessServiceRole", required = true) Long nID_AccessServiceRole,
            @ApiParam(value = "номер-ИД роли", required = true) @RequestParam(value = "nID_AccessServiceRight", required = true) Long nID_AccessServiceRight) {
        return JsonRestUtils.toJsonResponse(oAccessService.setAccessServiceRoleRight(nID,
                nID_AccessServiceRole, nID_AccessServiceRight));
    }

    @ApiOperation(value = "Удалить объект-связку роли на право доступа к сервису")
    @RequestMapping(value = "/removeAccessServiceRoleRight", method = { RequestMethod.POST, RequestMethod.GET })
    public ResponseEntity removeAccessServiceRoleRight(
            @ApiParam(value = "номер-ИД связки роли и права", required = true) @RequestParam(value = "nID") Long nID) {
        oAccessService.removeAccessServiceRoleRight(nID);
        return new ResponseEntity(HttpStatus.OK);
    }

    // -------------- AccessServiceRoleRightInclude --------------------------------------------------------------------

    @ApiOperation(value = "Добавить/обновить объект-связку включение одного права, другим правом",
            notes = "Если задан nID, то обновляет обьект-связку, если нет - создает новый")
    @RequestMapping(value = "/setAccessServiceRoleRightInclude", method = { RequestMethod.POST, RequestMethod.GET })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Результирующий обьект-связку включение одного права, другим правом") })
    public ResponseEntity setAccessServiceRoleRightInclude(
            @ApiParam(value = "номер-ИД связки права и включенного права", required = false) @RequestParam(value = "nID", required = false) Long nID,
            @ApiParam(value = "Строка логин пользователя", required = true) @RequestParam(value = "nID_AccessServiceRole", required = true) Long nID_AccessServiceRole,
            @ApiParam(value = "номер-ИД роли", required = true) @RequestParam(value = "nID_AccessServiceRole_Include", required = true) Long nID_AccessServiceRole_Include) {
        return JsonRestUtils.toJsonResponse(oAccessService.setAccessServiceRoleRightInclude(nID,
                nID_AccessServiceRole, nID_AccessServiceRole_Include));
    }

    @ApiOperation(value = "Удалить объект-связку включение одного права, другим правом")
    @RequestMapping(value = "/removeAccessServiceRoleRightInclude", method = { RequestMethod.POST, RequestMethod.GET })
    public ResponseEntity removeAccessServiceRoleRightInclude(
            @ApiParam(value = "номер-ИД связки права и права", required = true) @RequestParam(value = "nID") Long nID) {
        oAccessService.removeAccessServiceRoleRightInclude(nID);
        return new ResponseEntity(HttpStatus.OK);
    }

    // -------------- AccessServiceRight -------------------------------------------------------------------------------

    @ApiOperation(value = "Получить массив объектов-прав на сервис по фильтру")
    @RequestMapping(value = "/getAccessServiceRights", method = RequestMethod.GET)
    public ResponseEntity getAccessServiceRoleRights(
            @ApiParam(value = "номер-ИД права", required = false) @RequestParam(value = "nID", required = false) Long nID,
            @ApiParam(value = "строка-сервис (маска)", required = false) @RequestParam(value = "sService", required = false) String sService,
            @ApiParam(value = "строка-название метода вызова", required = false) @RequestParam(value = "saMethod", required = false) String saMethod,
            @ApiParam(value = "строка-название бина-обработчика", required = false) @RequestParam(value = "sHandlerBean", required = false) String sHandlerBean) {
        return JsonRestUtils
                .toJsonResponse(oAccessService.getAccessServiceRights(nID, sService, saMethod, sHandlerBean));
    }

    @ApiOperation(value = "Добавить/обновить объект-права на сервис",
            notes = "Если задан nID, то обновляет обьект-право, если нет - создает новый")
    @RequestMapping(value = "/setAccessServiceRight", method = { RequestMethod.POST, RequestMethod.GET })
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Результирующий объект-права на сервис") })
    public ResponseEntity setAccessServiceRight(
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

    @ApiOperation(value = "Удалить объект-право на сервис")
    @RequestMapping(value = "/removeAccessServiceRight", method = { RequestMethod.POST, RequestMethod.GET })
    public ResponseEntity removeAccessServiceRight(
            @ApiParam(value = "номер-ИД права", required = true) @RequestParam(value = "nID") Long nID) {
        oAccessService.removeAccessServiceRight(nID);
        return new ResponseEntity(HttpStatus.OK);
    }

}
