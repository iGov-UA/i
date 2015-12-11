package org.activiti.rest.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.wf.dp.dniprorada.base.service.access.AccessService;
import org.wf.dp.dniprorada.base.service.access.HandlerBeanValidationException;
import org.wf.dp.dniprorada.base.util.JsonRestUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;

import javax.servlet.http.HttpServletResponse;

/**
 * User: goodg_000
 * Date: 06.10.2015
 * Time: 22:57
 */
@Controller
@Api(tags = { "Получение и установка прав доступа к rest сервисам" }, description = "#")
@RequestMapping(value = "/access")
public class ActivitiRestAccessController {

    private static final Logger LOG = Logger.getLogger(ActivitiRestAccessController.class);
    
    // Подробные описания сервисов для документирования в swagger
    private static final String noteCODE= "\n```\n";    
    private static final String noteController = "#####  Получение и установка прав доступа к rest-сервисам. ";    

    private static final String noteGetAccessServiceLoginRight = noteController    		
    		+ "Возврат списка сервисов доступных пользователю #####\n\n"
    		+ "возвращает список всех сервисов доступных пользователю с именем sLogin с формате JSON.\n"
            + "Request:\n"
            + noteCODE 
            + "  sLogin=TestLogin\n"
            + noteCODE 
            + "Response:\n"
            + noteCODE 
            + "  [\n"
            + "    \"TestService\"\n"
            + "  ]\n"
    		+ noteCODE
    		+ "Пример:\n"
            + "https://test.region.igov.org.ua/wf/service/access/getAccessServiceLoginRight?sLogin=TestLogin";

    private static final String noteSetAccessServiceLoginRight = noteController    		
    		+ "Сохранение разрешения на доступ к сервису для пользователя #####\n\n"
    		+ "Сохраняет запись в базе, что пользователь sLogin имеет доступ к сервису sService. Существование такого пользователя и сервиса не проверяется.\n\n"
    		+ "- sLogin - имя пользователя\n"
    		+ "- sService - строка сервиса\n"
    		+ "- sHandlerBean - опцинальный параметр: имя спрингового бина реализующего интерфейс AccessServiceLoginRightHandler, который будет "
    		+ "заниматься проверкой прав доступа для данной записи. При сохранении проверяется наличие такого бина, и если его нет - то будет выброшена ошибка.\n\n"
    		+ "Примеры:\n"
    		+ "https://test.region.igov.org.ua/wf/service/access/setAccessServiceLoginRight\n\n"
    		+ "- sLogin=SomeLogin\n"
    		+ "- sService=access/hasAccessServiceLoginRight\n\n"
    		+ noteCODE
    		+ "  Ответ: Status 200\n"
    		+ noteCODE
    		+ "- sLogin=SomeLogin\n"
    		+ "- sService=access/hasAccessServiceLoginRight\n"
    		+ "- sHandlerBean=WrongBean\n"
    		+ "Ответ:\n\n"
    		+ noteCODE
    		+ "  {\n"
    		+ "    \"code\": \"SYSTEM_ERR\",\n"
    		+ "    \"message\": \"No bean named 'WrongBean' is defined\"\n"
    		+ "  }\n"
    		+ noteCODE;

    private static final String noteRemoveAccessServiceLoginRight = noteController    
    		+ "Удаление разрешения на доступ к сервису для пользователя #####\n\n"
    		+ "Удаляет запись из базы, что пользователь sLogin имеет доступ к сервису sService."
    		+ "Статус код 200 означает что запись успешно удалена. Код 304 - что такая запись не найдена.\n\n"
    		+ "Примеры:\n\n"
    		+ "https://test.region.igov.org.ua/wf/service/access/removeAccessServiceLoginRight?sLogin=TestLogin&sService=TestService\n\n"
    		+ noteCODE
   			+ "  Ответ: Status 200\n"
    		+ noteCODE
   			+ "https://test.region.igov.org.ua/wf/service/access/removeAccessServiceLoginRight?sLogin=FakeLogin&sService=TestService\n"
    		+ noteCODE
   			+ "  Ответ: Status 304\n"
    		+ noteCODE;

    private static final String noteHasAccessServiceLoginRight = noteController    		
    		+ "Проверка разрешения на доступ к сервису для пользователя #####\n\n"
    		+ "возвращает true - если у пользоватля с логином sLogin есть доступ к рест сервиу sService при вызове его с аргументами sData, или false - если доступа нет.\n\n"
    		
			+ "- sLogin - имя пользователя для которого проверяется доступ\n"
			+ "- sService - строка сервиса\n"
			+ "- sData - опциональный параметр со строкой параметров к сервису (формат передачи пока не определен). "
			+ "Если задан бин sHandlerBean (см. ниже) то он может взять на себя проверку допуспности сервиса для данного набора параметров.\n\n"
    		+ "Пример:\n"
            + "https://test.region.igov.org.ua/wf/service/access/hasAccessServiceLoginRight?sLogin=SomeLogin&sService=access/hasAccessServiceLoginRight\n"
    		+ noteCODE
    		+ "Ответ false\n"
    		+ noteCODE;
	///////////////////////////////////////////////////////////////////////////

    @Autowired
    private AccessService accessService;

    /**
     * @param sLogin имя пользователя
     */
    @ApiOperation(value = "Возврат списка сервисов доступных пользователю", notes = noteGetAccessServiceLoginRight)
    @RequestMapping(value = "/getAccessServiceLoginRight", method = RequestMethod.GET)
    public ResponseEntity getAccessServiceLoginRight(@ApiParam(value = "Логин пользователя", required = true) @RequestParam(value = "sLogin") String sLogin) {
        return JsonRestUtils.toJsonResponse(accessService.getAccessibleServices(sLogin));
    }

    /**
     * @param sLogin имя пользователя
     * @param sService строка сервиса
     * @param sHandlerBean опцинальный параметр: имя спрингового бина реализующего интерфейс AccessServiceLoginRightHandler, который будет заниматься проверкой прав доступа для данной записи. При сохранении проверяется наличие такого бина, и если его нет - то будет выброшена ошибка.
     */
    @ApiOperation(value = "Сохранение разрешения на доступ к сервису для пользователя", notes = noteSetAccessServiceLoginRight)
    @RequestMapping(value = "/setAccessServiceLoginRight", method = RequestMethod.POST)
    @ApiResponses(value = { @ApiResponse(code = 500, message = "Ошибка бизнес процесса")} )
    public void setAccessServiceLoginRight(@ApiParam(value = "Логин пользователя", required = true) @RequestParam(value = "sLogin") String sLogin,
    		@ApiParam(value = "Строка сервиса", required = true) @RequestParam(value = "sService") String sService,
    		@ApiParam(value = "Имя спрингового бина реализующего интерфейс AccessServiceLoginRightHandler", required = false) @RequestParam(value = "sHandlerBean", required = false) String sHandlerBean,
            HttpServletResponse response)
            throws ActivitiRestException {
        try {

            accessService.saveOrUpdateAccessServiceLoginRight(sLogin, sService, sHandlerBean);
            response.setStatus(HttpStatus.OK.value());

        } catch (HandlerBeanValidationException e) {
            LOG.warn(e.getMessage(), e);
            throw new ActivitiRestException(ActivitiExceptionController.BUSINESS_ERROR_CODE, e.getMessage());
        }
    }

    /**
     * @param sLogin имя пользователя
     * @param sService строка сервиса
     */
    @ApiOperation(value = "Удаление разрешения на доступ к сервису для пользователя", notes = noteRemoveAccessServiceLoginRight)
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
    @ApiOperation(value = "Проверка разрешения на доступ к сервису для пользователя", notes = noteHasAccessServiceLoginRight)
    @RequestMapping(value = "/hasAccessServiceLoginRight", method = RequestMethod.GET)
    @ApiResponses(value = { @ApiResponse(code = 500, message = "Ошибка бизнес процесса")} )
    public ResponseEntity hasAccessServiceLoginRight(@ApiParam(value = "Логин пользователя", required = true) @RequestParam(value = "sLogin") String sLogin,
    		@ApiParam(value = "строка сервиса", required = true) @RequestParam(value = "sService") String sService,
    		@ApiParam(value = "строка параметров к сервису", required = false) @RequestParam(value = "sData", required = false) String sData)
            throws ActivitiRestException {

        try {
            return JsonRestUtils.toJsonResponse(accessService.hasAccessToService(sLogin, sService, sData));
        } catch (HandlerBeanValidationException e) {
            LOG.warn(e.getMessage(), e);
            throw new ActivitiRestException(ActivitiExceptionController.BUSINESS_ERROR_CODE, e.getMessage());
        }
    }
}
