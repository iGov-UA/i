package org.igov.service.controller.actionexecute;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.mail.EmailException;
import org.igov.io.mail.Mail;
import org.igov.model.action.execute.item.ActionExecuteStatus;
import org.igov.model.action.execute.item.ActionExecuteStatusDAO;
import org.igov.service.business.action.execute.ActionExecuteService;
import org.igov.service.business.action.execute.old.ActionExecuteOldService;
import org.igov.service.exception.CommonServiceException;
import org.igov.util.JSON.JsonRestUtils;
import org.igov.util.MethodsCallRunnerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@Api(tags = {"ActionExecuteController"})
@RequestMapping(value = "/action/execute")
public class ActionExecuteController {

    private static final Logger LOG = LoggerFactory.getLogger(ActionExecuteController.class);

    @Autowired
    private Mail mail;

    @Autowired
	private ActionExecuteStatusDAO actionExecuteStatusDAO;

	@Autowired
    private MethodsCallRunnerUtil methodCallRunner;

    @Autowired
    private ActionExecuteService actionExecuteService;

    @Autowired
    private ActionExecuteOldService actionExecuteOldService;

    @ApiOperation(value = "Получение массива всех акций ", notes = "")
    @RequestMapping(value = "/getActionExecuteStatuses", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, headers = {"Accept=application/json"})
    public
    @ResponseBody
    ResponseEntity getActionExecuteStatuses() {

        List<ActionExecuteStatus> actionExecuteList = actionExecuteStatusDAO.findAll();
        return JsonRestUtils.toJsonResponse(actionExecuteList);
    }

    @ApiOperation(value = " получить массив всех акций", notes = "")
    @RequestMapping(value = "/getActionExecute", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, headers = {"Accept=application/json"})
    public
    @ResponseBody
    ResponseEntity getActionExecute(
            @ApiParam(value = "число строк максимальное (максимально - не более 1000, по умолчанию 10)", required = false) @RequestParam(value = "nRowsMax", required = false, defaultValue = "10") Integer nRowsMax,
            @ApiParam(value = "строка-маска метода для выборки", required = false) @RequestParam(value = "sMethodMask", required = false) String sMethodMask,
            @ApiParam(value = "булевый флаг, если true=только из олд-таблицы, иначе только из основной (по умолчанию false)", required = false) @RequestParam(value = "bOldOnly", required = false, defaultValue = "false") Boolean bOldOnly,
            @ApiParam(value = "массив JSON статусов для выборки", required = false) @RequestParam(value = "asID_Status", required = false) String asID_Status,
            @ApiParam(value = "число-лимит. выбрать только те, у которых число попыток не превышает указанный лимит (иначе с любым числом попыток)", required = false) @RequestParam(value = "nTryMax", required = false) Integer nTryMax,
            @ApiParam(value = "номер-ИД записи", required = false) @RequestParam(value = "nID", required = false) Long nID) {

        ResponseEntity<String> res;
        if(bOldOnly)
    			res = JsonRestUtils.toJsonResponse(actionExecuteOldService.getActionExecute(nRowsMax, sMethodMask, asID_Status, nTryMax, nID));
    		else
    			res = JsonRestUtils.toJsonResponse(actionExecuteService.getActionExecute(nRowsMax, sMethodMask, asID_Status, nTryMax, nID));
            
        	return res;
    }

    @ApiOperation(value = "выполнить задачи", notes = "")
    @RequestMapping(value = "/runActionExecute", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, headers = {"Accept=application/json"})
    public
    @ResponseBody
    ResponseEntity runActionExecute(
            @ApiParam(value = "число строк максимальное (максимально - не более 1000, по умолчанию 10)", required = false) @RequestParam(value = "nRowsMax", required = false, defaultValue = "10") Integer nRowsMax,
            @ApiParam(value = "строка-маска метода для выборки", required = false) @RequestParam(value = "sMethodMask", required = false) String sMethodMask,
            @ApiParam(value = "булевый флаг, если true=только из олд-таблицы, иначе только из основной (по умолчанию false)", required = false) @RequestParam(value = "bOldOnly", required = false, defaultValue = "false") Boolean bOldOnly,
            @ApiParam(value = "массив JSON статусов для выборки", required = false) @RequestParam(value = "asID_Status", required = false) String asID_Status,
            @ApiParam(value = "число-лимит. выбрать только те, у которых число попыток не превышает указанный лимит (иначе с любым числом попыток)", required = false) @RequestParam(value = "nTryMax", required = false) Integer nTryMax,
            @ApiParam(value = "номер-ИД записи", required = false) @RequestParam(value = "nID", required = false) Long nID)
            throws CommonServiceException {
        LOG.info("methodCallRunner is {methodCallRunner}");
        return JsonRestUtils.toJsonResponse(methodCallRunner.runMethod(nRowsMax, sMethodMask, asID_Status, nTryMax, nID));
    }

    @ApiOperation(value = "переместить записи из основной таблицы в олд или обратно", notes = "")
    @RequestMapping(value = "/moveActionExecute", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, headers = {"Accept=application/json"})
    public
    @ResponseBody
    ResponseEntity moveActionExecute(
            @ApiParam(value = "число строк максимальное (максимально - не более 1000, по умолчанию 10)", required = false) @RequestParam(value = "nRowsMax", required = false, defaultValue = "10") Integer nRowsMax,
            @ApiParam(value = "строка-маска метода для выборки", required = false) @RequestParam(value = "sMethodMask", required = false) String sMethodMask,
            @ApiParam(value = "булевый флаг, если true=только из олд-таблицы, иначе только из основной (по умолчанию false)", required = false) @RequestParam(value = "bOldOnly", required = false, defaultValue = "false") Boolean bOldOnly,
            @ApiParam(value = "массив JSON статусов для выборки", required = false) @RequestParam(value = "asID_Status", required = false) String asID_Status,
            @ApiParam(value = "число-лимит. выбрать только те, у которых число попыток не превышает указанный лимит (иначе с любым числом попыток)", required = false) @RequestParam(value = "nTryMax", required = false) Integer nTryMax,
            @ApiParam(value = "номер-ИД записи", required = false) @RequestParam(value = "nID", required = false) Long nID,
            @ApiParam(value = "булевый флаг, если указан true, то переместить из олд-а в основную (по умолчанию false)", required = false) @RequestParam(value = "bBack", required = false, defaultValue = "false") Boolean bBack)
            throws EmailException {
        if (bBack)
            actionExecuteOldService.moveActionExecuteOld(nRowsMax, sMethodMask, asID_Status, nTryMax, nID);
    	else
    		actionExecuteService.moveActionExecute(nRowsMax, sMethodMask, asID_Status, nTryMax, nID);
    	return null;
    }

    @Transactional
    @ApiOperation(value = "переместить записи из основной таблицы в олд или обратно", notes = "")
    @RequestMapping(value = "/testMail", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, headers = {"Accept=application/json"})
    public
    @ResponseBody
    ResponseEntity testMail(
            @ApiParam(value = "число строк максимальное (максимально - не более 1000, по умолчанию 10)", required = false) @RequestParam(value = "nRowsMax", required = false, defaultValue = "10") Integer nRowsMax,
            @ApiParam(value = "строка-маска метода для выборки", required = false) @RequestParam(value = "sMethodMask", required = false) String sMethodMask,
            @ApiParam(value = "булевый, true=только из олд-таблицы, иначе только из основной (по умолчанию false)", required = false) @RequestParam(value = "bOldOnly", required = false, defaultValue = "false") Boolean bOldOnly,
            @ApiParam(value = "массив JSON статусов для выборки", required = false) @RequestParam(value = "asID_Status", required = false) String asID_Status,
            @ApiParam(value = "число-лимит. выбрать только те, у которых число попыток не превышает указанный лимит (иначе с любым числом попыток)", required = false) @RequestParam(value = "nTryMax", required = false) Integer nTryMax,
            @ApiParam(value = "номер-ИД записи", required = false) @RequestParam(value = "nID", required = false) Long nID,
            @ApiParam(value = "булевый, если указан true, то переместить из олд-а в основную (по умолчанию false)", required = false) @RequestParam(value = "bBack", required = false, defaultValue = "false") Boolean bBack)
            throws EmailException, CommonServiceException {
        mail._To("a.maryushin@astoundcommerce.com");
        mail._Body("blblblblblallablablabllbabl");

        LOG.info("(mail.getHead()={})", mail.getHead());
        LOG.info("(mail.getBody()={})", mail.getBody());
        LOG.info("(mail.getAuthUser()={})", mail.getAuthUser());
        LOG.info("(mail.getAuthPassword()={})", mail.getAuthPassword());
        LOG.info("(mail.getFrom()={})", mail.getFrom());
        LOG.info("(mail.getTo()={})", mail.getTo());
        LOG.info("(mail.getHost()={})", mail.getHost());
        LOG.info("(mail.getPort()={})", mail.getPort());       
        mail.sendWithUniSender();	
        LOG.info("test method call");
        methodCallRunner.registerMethod(ActionExecuteController.class.getName(), "testMethod", new Object[]{"param1", "param2".getBytes(), new Integer(1)}); 
    	return null;
    }   
    
    private void testMethod(String param1, byte[] param2, Integer param3){
    	System.out.println(param1);
    }
    
}
