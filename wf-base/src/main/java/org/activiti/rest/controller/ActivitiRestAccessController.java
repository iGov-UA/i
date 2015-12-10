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
@Api(tags = { "wf-central", "Безопасность" }, description = "#")
@RequestMapping(value = "/access")
public class ActivitiRestAccessController {

    private static final Logger LOG = Logger.getLogger(ActivitiRestAccessController.class);
    
    private final String noteController = "<strong>[wf-central] Получение и установка прав доступа к rest-сервисам</strong><br /><br />";

    private final String noteGetAccessServiceLoginRight = noteController    		
    		+ "возвращает список всех сервисов доступных пользователю с именем sLogin с формате JSON.<br /><br />"
    		+ "Пример:<br />"
            + "<a href=\"https://test.region.igov.org.ua/wf/service/access/getAccessServiceLoginRight?sLogin=TestLogin\" target=\"_blank\">"
            + "https://test.region.igov.org.ua/wf/service/access/getAccessServiceLoginRight?sLogin=TestLogin</a>";

    private final String noteSetAccessServiceLoginRight = noteController    		
    		+ "Сохраняет запись в базе, что пользователь sLogin имеет доступ к сервису sService. Существование такого пользователя и сервиса не проверяется.<br /><br />"
    		+ "Примечание:<br />"
    		+ "sHandlerBean - опцинальный параметр: имя спрингового бина реализующего интерфейс AccessServiceLoginRightHandler, который будет "
    		+ "заниматься проверкой прав доступа для данной записи. При сохранении проверяется наличие такого бина, и если его нет - то будет выброшена ошибка.<br /><br />"
    		+ "Пример:<br />"
            + "<a href=\"https://test.region.igov.org.ua/wf/service/access/setAccessServiceLoginRight\" target=\"_blank\">"
            + "https://test.region.igov.org.ua/wf/service/access/setAccessServiceLoginRight</a><br /><br />"
    		+ "Ответ <strong>false</strong>";

    private final String noteRemoveAccessServiceLoginRight = noteController    		
    		+ "Удаляет запись из базы, что пользователь sLogin имеет доступ к сервису sService.<br /><br />"
    		+ "Пример:<br />"
            + "<a href=\"https://test.region.igov.org.ua/wf/service/access/removeAccessServiceLoginRight?sLogin=TestLogin&sService=TestService\" target=\"_blank\">"
            + "https://test.region.igov.org.ua/wf/service/access/removeAccessServiceLoginRight?sLogin=TestLogin&sService=TestService</a>";
    
    private final String noteHasAccessServiceLoginRight = noteController    		
    		+ "возвращает true - если у пользоватля с логином sLogin есть доступ к рест сервиу sService при вызове его с аргументами sData, или false - если доступа нет.<br /><br />"
    		+ "Примечание:<br />"
    		+ "sData - опциональный параметр со строкой параметров к сервису (формат передачи пока не определен).<br />"
    		+ "Если задан бин sHandlerBean (см. ниже) то он может взять на себя проверку достуспности сервиса для данного набора параметров.<br /><br />"
    		+ "Пример:<br />"
            + "<a href=\"https://test.region.igov.org.ua/wf/service/access/hasAccessServiceLoginRight?sLogin=SomeLogin&sService=access/hasAccessServiceLoginRight\" target=\"_blank\">"
            + "https://test.region.igov.org.ua/wf/service/access/hasAccessServiceLoginRight?sLogin=SomeLogin&sService=access/hasAccessServiceLoginRight</a><br /><br />"
    		+ "Ответ <strong>false</strong>";

    @Autowired
    private AccessService accessService;

    /**
     * @param sLogin имя пользователя
     */
    @ApiOperation(value = "Возвращает список сервисов доступных пользователю", notes = noteGetAccessServiceLoginRight)
    @RequestMapping(value = "/getAccessServiceLoginRight", method = RequestMethod.GET)
    public ResponseEntity getAccessServiceLoginRight(@ApiParam(value = "Логин пользователя", required = true) @RequestParam(value = "sLogin") String sLogin) {
        return JsonRestUtils.toJsonResponse(accessService.getAccessibleServices(sLogin));
    }

    /**
     * @param sLogin имя пользователя
     * @param sService строка сервиса
     * @param sHandlerBean опцинальный параметр: имя спрингового бина реализующего интерфейс AccessServiceLoginRightHandler, который будет заниматься проверкой прав доступа для данной записи. При сохранении проверяется наличие такого бина, и если его нет - то будет выброшена ошибка.
     */
    @ApiOperation(value = "Сохраняет разрешение на доступ к сервису для пользователя", notes = noteSetAccessServiceLoginRight)
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
    @ApiOperation(value = "Удаляет разрешение на доступ к сервису для пользователя", notes = noteRemoveAccessServiceLoginRight)
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
    @ApiOperation(value = "Проверяет разрешение на доступ к сервису для пользователя", notes = noteHasAccessServiceLoginRight)
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
