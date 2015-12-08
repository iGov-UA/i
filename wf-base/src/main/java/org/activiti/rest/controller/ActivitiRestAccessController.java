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

import javax.servlet.http.HttpServletResponse;

/**
 * User: goodg_000
 * Date: 06.10.2015
 * Time: 22:57
 */
@Controller
@RequestMapping(value = "/access")
public class ActivitiRestAccessController {

    private static final Logger LOG = Logger.getLogger(ActivitiRestAccessController.class);

    @Autowired
    private AccessService accessService;

    /**
     * @param sLogin имя пользователя
     */
    @RequestMapping(value = "/getAccessServiceLoginRight", method = RequestMethod.GET)
    public ResponseEntity getAccessServiceLoginRight(@RequestParam(value = "sLogin") String sLogin) {
        return JsonRestUtils.toJsonResponse(accessService.getAccessibleServices(sLogin));
    }

    /**
     * @param sLogin имя пользователя
     * @param sService строка сервиса
     * @param sHandlerBean опцинальный параметр: имя спрингового бина реализующего интерфейс AccessServiceLoginRightHandler, который будет заниматься проверкой прав доступа для данной записи. При сохранении проверяется наличие такого бина, и если его нет - то будет выброшена ошибка.
     */
    @RequestMapping(value = "/setAccessServiceLoginRight", method = RequestMethod.POST)
    public void setAccessServiceLoginRight(@RequestParam(value = "sLogin") String sLogin,
            @RequestParam(value = "sService") String sService,
            @RequestParam(value = "sHandlerBean", required = false) String sHandlerBean,
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
    @RequestMapping(value = "/removeAccessServiceLoginRight", method = RequestMethod.DELETE)
    public void setAccessServiceLoginRight(@RequestParam(value = "sLogin") String sLogin,
            @RequestParam(value = "sService") String sService,
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
    @RequestMapping(value = "/hasAccessServiceLoginRight", method = RequestMethod.GET)
    public ResponseEntity hasAccessServiceLoginRight(@RequestParam(value = "sLogin") String sLogin,
            @RequestParam(value = "sService") String sService,
            @RequestParam(value = "sData", required = false) String sData)
            throws ActivitiRestException {

        try {
            return JsonRestUtils.toJsonResponse(accessService.hasAccessToService(sLogin, sService, sData));
        } catch (HandlerBeanValidationException e) {
            LOG.warn(e.getMessage(), e);
            throw new ActivitiRestException(ActivitiExceptionController.BUSINESS_ERROR_CODE, e.getMessage());
        }
    }
}
