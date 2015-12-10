package org.activiti.rest.controller;

import org.activiti.engine.ProcessEngines;
import org.activiti.rest.controller.entity.LoginResponse;
import org.activiti.rest.controller.entity.LoginResponseI;
import org.activiti.rest.controller.entity.LogoutResponse;
import org.activiti.rest.controller.entity.LogoutResponseI;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@Api(tags = { "wf-central", "Безопасность" }, description = "#")
@RequestMapping(value = "/auth")
public class ActivitiRestAuthController {
    private final String noteController ="<strong>[wf-central] Аутентификация пользователя</strong><br /><br />";

    private final String noteLogin = noteController
    		+ "Логин пользователя в систему. Возращает признак успеха/неудачи входа.<br /><br />"
            + "Response 200:<br />" 
    		+ " {\"session\":\"true\"} - Пользователь авторизирован<br />"
    		+ " {\"session\":\"false\"} - Имя пользователя или пароль не корректны<br /><br />"
    		+ "Пример:<br />"
            + "<a href=\"https://test.region.igov.org.ua/wf/auth/login?sLogin=kermit&sPassword=kermit\" target=\"_blank\">"
            + "https://test.region.igov.org.ua/wf/auth/login?sLogin=kermit&sPassword=kermit</a>";

    private final String noteLogout = noteController 
    		+ "Логаут пользователя (наличие cookie JSESSIONID)<br /><br />"
            + "Response 200:<br />" 
            + "{\"session\":\"97AE7CA414A5DA85749FE379CC843796\"}";

    /**
     * Логин пользователя в систему. Возращает признак успеха/неудачи входа.
     * true - Пользователь авторизирован
     * false - Имя пользователя или пароль не корректны
     *
     * @param login    - Логин пользователя
     * @param password - Пароль пользователя
     * @return {"session":"true"} -- Пользователь авторизирован
     * OR  {"session":"false"}- Имя пользователя или пароль не корректны
     * @throws ActivitiAuthException
     */
    @ApiOperation(value = "Авторизация пользователя", notes = noteLogin)
    @RequestMapping(value = { "/login", "/login-v2" }, method = RequestMethod.POST)
    public
    @ResponseBody
    LoginResponseI login(
    		@ApiParam(value = "Логин пользователя", required = true) @RequestParam(value = "sLogin") String login,
    		@ApiParam(value = "Пароль пользователя", required = true) @RequestParam(value = "sPassword") String password, HttpServletRequest request)
            throws ActivitiAuthException {
        if (ProcessEngines.getDefaultProcessEngine().getIdentityService().checkPassword(login, password)) {
            request.getSession(true);
            return new LoginResponse(Boolean.TRUE.toString());
        } else {
            throw new ActivitiAuthException(ActivitiAuthException.Error.LOGIN_ERROR, "Login or password invalid");
        }
    }

    /**
     * Логаут пользователя (наличие cookie JSESSIONID):
     */
    @ApiOperation(value = "Выход из системы", notes = noteLogout)
    @RequestMapping(value = "/logout", method = { RequestMethod.DELETE, RequestMethod.POST })
    public
    @ResponseBody
    LogoutResponseI logout(HttpServletRequest request) throws ActivitiAuthException {
        HttpSession session = request.getSession();
        if (session.isNew()) {
            throw new ActivitiAuthException(ActivitiAuthException.Error.LOGOUT_ERROR,
                    "Client doesn't have a valid server session");
        } else {
            session.invalidate();
            return new LogoutResponse(session.getId());
        }
    }
}
