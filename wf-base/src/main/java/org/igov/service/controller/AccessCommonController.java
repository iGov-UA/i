package org.igov.service.controller;

import org.igov.service.exception.CommonServiceException;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.igov.service.business.access.AccessService;
import org.igov.service.business.access.handler.HandlerBeanValidationException;
import org.igov.util.convert.JsonRestUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import java.util.HashMap;
import java.util.Map;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.activiti.engine.ProcessEngines;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.mail.EmailException;
import org.igov.io.db.kv.temp.IBytesDataInmemoryStorage;
import org.igov.io.db.kv.temp.exception.RecordInmemoryException;
import org.igov.io.mail.Mail;
import org.igov.service.entity.LoginResponse;
import org.igov.service.entity.LoginResponseI;
import org.igov.service.entity.LogoutResponse;
import org.igov.service.entity.LogoutResponseI;
import org.igov.service.exception.AccessServiceException;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * User: goodg_000
 * Date: 06.10.2015
 * Time: 22:57
 */
@Controller
@Api(tags = { "AccessCommonController" }, description = "Доступ общий (права доступа к сервисам)")
@RequestMapping(value = "/access")
public class AccessCommonController {

    private static final Logger LOG = LoggerFactory.getLogger(AccessCommonController.class);
    
    @Autowired
    private AccessService accessService;
    @Autowired
    private Mail oMail;
    @Autowired
    private IBytesDataInmemoryStorage oBytesDataInmemoryStorage;

    /**
     * Логин пользователя в систему. Возращает признак успеха/неудачи входа.
     * true - Пользователь авторизирован
     * false - Имя пользователя или пароль не корректны
     *
     * @param login    - Логин пользователя
     * @param password - Пароль пользователя
     * @return {"session":"true"} -- Пользователь авторизирован
     * OR  {"session":"false"}- Имя пользователя или пароль не корректны
     * @throws AccessServiceException
     */
    @ApiOperation(value = "Логин пользователя", notes = "##### AccessCommonController - Доступ общий (права доступа к сервисам) #####\n\n"
     + "Request:\n"
     + "\n```\n" 
     + "  sLogin=user&sPassword=password\n"
     + "\n```\n" 
     + "Response:\n"
     + "\n```json\n" 
     + "  {\"session\":\"true\"}\n"
     + "\n```\n" 
     + "где:\n"
     + "- **true** - Пользователь авторизирован\n"
     + "- **false** - Имя пользователя или пароль не корректны\n"
     + "Пример:\n"
     + "https://test.region.igov.org.ua/wf/access/login?sLogin=kermit&sPassword=kermit")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Возращает признак успеха/неудачи входа") })
    @RequestMapping(value = { "/login", "/login-v2" }, method = RequestMethod.POST)
    public
    @ResponseBody
    LoginResponseI login(
    		@ApiParam(value = "Логин пользователя", required = true) @RequestParam(value = "sLogin") String login,
    		@ApiParam(value = "Пароль пользователя", required = true) @RequestParam(value = "sPassword") String password, HttpServletRequest request)
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
    @ApiOperation(value = "Логаут пользователя", notes = "##### Аутентификация пользователя. Логаут пользователя (наличие cookie JSESSIONID) #####\n"
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
     * @param sLogin имя пользователя
     */
    @ApiOperation(value = "Возврат списка сервисов доступных пользователю", notes = "#####  Получение и установка прав доступа к rest-сервисам. "
      + "Возврат списка сервисов доступных пользователю #####\n\n"
      + "возвращает список всех сервисов доступных пользователю с именем sLogin с формате JSON.\n"
      + "Request:\n"
      + "\n```\n" 
      + "  sLogin=TestLogin\n"
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
    public ResponseEntity getAccessServiceLoginRight(@ApiParam(value = "Логин пользователя", required = true) @RequestParam(value = "sLogin") String sLogin) {
        return JsonRestUtils.toJsonResponse(accessService.getAccessibleServices(sLogin));
    }

    /**
     * @param sLogin имя пользователя
     * @param sService строка сервиса
     * @param sHandlerBean опцинальный параметр: имя спрингового бина реализующего интерфейс AccessServiceLoginRightHandler, который будет заниматься проверкой прав доступа для данной записи. При сохранении проверяется наличие такого бина, и если его нет - то будет выброшена ошибка.
     */
    @ApiOperation(value = "Сохранение разрешения на доступ к сервису для пользователя", notes = "#####  Получение и установка прав доступа к rest-сервисам. "    		
     + "Сохранение разрешения на доступ к сервису для пользователя #####\n\n"
     + "Сохраняет запись в базе, что пользователь sLogin имеет доступ к сервису sService. Существование такого пользователя и сервиса не проверяется.\n\n\n"
     + "- sHandlerBean - опцинальный параметр: имя спрингового бина реализующего интерфейс AccessServiceLoginRightHandler, который будет "
     + "заниматься проверкой прав доступа для данной записи. При сохранении проверяется наличие такого бина, и если его нет - то будет выброшена ошибка.\n\n"
     + "Примеры:\n"
     + "https://test.region.igov.org.ua/wf/service/access/setAccessServiceLoginRight\n\n"
     + "- sLogin=SomeLogin\n"
     + "- sService=access/hasAccessServiceLoginRight\n\n"
     + "\n```\n"
     + "  Ответ: Status 200\n"
     + "\n```\n"
     + "- sLogin=SomeLogin\n"
     + "- sService=access/hasAccessServiceLoginRight\n"
     + "- sHandlerBean=WrongBean\n"
     + "Ответ:\n\n"	
     + "\n```json\n"
     + "  {\n"
     + "    \"code\": \"SYSTEM_ERR\",\n"
     + "    \"message\": \"No bean named 'WrongBean' is defined\"\n"
     + "  }\n"
     + "\n```\n")
    @RequestMapping(value = "/setAccessServiceLoginRight", method = RequestMethod.POST)
    @ApiResponses(value = { @ApiResponse(code = 500, message = "Ошибка бизнес процесса")} )
    public void setAccessServiceLoginRight(@ApiParam(value = "Логин пользователя", required = true) @RequestParam(value = "sLogin") String sLogin,
    		@ApiParam(value = "Строка сервиса", required = true) @RequestParam(value = "sService") String sService,
    		@ApiParam(value = "Имя спрингового бина реализующего интерфейс AccessServiceLoginRightHandler", required = false) @RequestParam(value = "sHandlerBean", required = false) String sHandlerBean,
            HttpServletResponse response)
            throws CommonServiceException {
        try {

            accessService.saveOrUpdateAccessServiceLoginRight(sLogin, sService, sHandlerBean);
            response.setStatus(HttpStatus.OK.value());

        } catch (HandlerBeanValidationException e) {
            LOG.warn(e.getMessage(), e);
            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE, e.getMessage());
        }
    }

    /**
     * @param sLogin имя пользователя
     * @param sService строка сервиса
     */
    @ApiOperation(value = "Удаление разрешения на доступ к сервису для пользователя", notes = "#####  Получение и установка прав доступа к rest-сервисам. "    
	+ "Удаление разрешения на доступ к сервису для пользователя #####\n\n"
	+ "Удаляет запись из базы, что пользователь sLogin имеет доступ к сервису sService."
	+ "Статус код 200 означает что запись успешно удалена. Код 304 - что такая запись не найдена.\n\n"
	+ "Примеры:\n\n"
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
    public void setAccessServiceLoginRight(@ApiParam(value = "Логин пользователя", required = true) @RequestParam(value = "sLogin") String sLogin,
    		@ApiParam(value = "Строка сервиса", required = true) @RequestParam(value = "sService") String sService,
            HttpServletResponse response) {
        if (accessService.removeAccessServiceLoginRight(sLogin, sService)) {
            response.setStatus(HttpStatus.OK.value());
        } else {
            response.setStatus(HttpStatus.NOT_MODIFIED.value());
        }
    }

    /**
     * @param sLogin имя пользователя для которого проверяется доступ
     * @param sService строка сервиса
     * @param sData опциональный параметр со строкой параметров к сервису (формат передачи пока не определен). Если задан бин sHandlerBean (см. ниже) то он может взять на себя проверку допуспности сервиса для данного набора параметров.
     */
    @ApiOperation(value = "Проверка разрешения на доступ к сервису для пользователя", notes = "#####  Получение и установка прав доступа к rest-сервисам. "    		
	+ "Проверка разрешения на доступ к сервису для пользователя #####\n\n"
	+ "возвращает true - если у пользоватля с логином sLogin есть доступ к рест сервиу sService при вызове его с аргументами sData, или false - если доступа нет.\n\n"		
	+ "Если задан бин sHandlerBean (см. ниже) то он может взять на себя проверку допуспности сервиса для данного набора параметров.\n\n"
	+ "Пример:\n"
        + "https://test.region.igov.org.ua/wf/service/access/hasAccessServiceLoginRight?sLogin=SomeLogin&sService=access/hasAccessServiceLoginRight\n"
	+ "\n```\n"
	+ "Ответ false\n"
	+ "\n```\n")
    @RequestMapping(value = "/hasAccessServiceLoginRight", method = RequestMethod.GET)
    @ApiResponses(value = { @ApiResponse(code = 500, message = "Ошибка бизнес процесса")} )
    public ResponseEntity hasAccessServiceLoginRight(@ApiParam(value = "Логин пользователя", required = true) @RequestParam(value = "sLogin") String sLogin,
    		@ApiParam(value = "строка сервиса", required = true) @RequestParam(value = "sService") String sService,
    		@ApiParam(value = "параметр со строкой параметров к сервису (формат передачи пока не определен)", required = false) @RequestParam(value = "sData", required = false) String sData)
            throws CommonServiceException {

        try {
            return JsonRestUtils.toJsonResponse(accessService.hasAccessToService(sLogin, sService, sData));
        } catch (HandlerBeanValidationException e) {
            LOG.warn(e.getMessage(), e);
            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE, e.getMessage());
        }
    }
    
    
    
    
    
    
    @ApiOperation(value = "verifyContactEmail", notes = "#####  Activiti. Сервис верификации контакта - электронного адреса #####\n\n"
            + "HTTP Context: https://server:port/wf/service/access/verifyContactEmail?sQuestion=sQuestion&sAnswer=sAnswer\n\n\n"
            + "Принцип работы:\n"
            + "1) если sAnswer не задан, то отсылать на адрес, указанный в sQuestion письмо(класс Mail) с:\n"
            + "темой: Верификация адреса\n"
            + "телом: Код подтверждения: ________\n"
            + "2) код подтверждения (для п.1) генерировать из больших и маленьких латинских символов и цифр, длиной 15 символов\n"
            + "3) также сохоанять этот-же код в Редис-хранилище с ключем, в виде присланного электронного адреса \n"
            + "4) также проверять по маске сам формат электронного адреса при запросе, и если он не валидный, то возвращать в ответе bVerified: false\n"
            + "5) если sAnswer задан, то сверять его с сохраненным ранее в хранилище Редис (п.4.3) и при его совпадении выводить в ответе bVerified: true иначе bVerified: false\n"
            + "Примеры:\n\n"
            + "\n```\n"
            + "https://test.region.igov.org.ua/wf/service/access/verifyContactEmail?sQuestion=test@igov.org.ua\n\n"
            + "\n```\n"
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
    		@ApiParam(value = "строка-запроса (электронный адрес)", required = true) @RequestParam(value = "sQuestion") String sQuestion,
    		@ApiParam(value = "строка-ответа (код )", required = false) 
    		@RequestParam(value = "строка ответа", required=false) String sAnswer) throws CommonServiceException, EmailException, RecordInmemoryException {
        Map<String, String> res = new HashMap<String, String>();
    	try {
	    	InternetAddress emailAddr = new InternetAddress(sQuestion);
	        emailAddr.validate();
	        if (sAnswer == null || sAnswer.isEmpty()){
	        	String saToMail = sQuestion;
	            String sHead = "Верификация адреса";
	            String sToken = RandomStringUtils.randomAlphanumeric(15);
	            String sBody = "Код подтверждения: " + sToken;
	            oMail.reset();
	            oMail._To(saToMail)
	                 ._Head(sHead)
	                 ._Body(sBody);
	            oMail.send();
	            
	            oBytesDataInmemoryStorage.putString(saToMail, sToken);
	            LOG.info("Send email with token " + sToken + " to the address:" + saToMail + " and saved token");
	            res.put("bVerified", "true");
	        } else {
	            String sToken = oBytesDataInmemoryStorage.getString(sQuestion);
	            LOG.info("Got token from Redis:" + sToken);
	            if (sAnswer.equals(sToken)){
		            res.put("bVerified", "true");	            	
	            } else {
		            res.put("bVerified", "false");
	            }
	        }
    	} catch (AddressException ex) {
    		LOG.warn("Email address " + sQuestion + " is not correct");
            res.put("bVerified", "false");
    	}
        return res;
    }    
    
    
}
