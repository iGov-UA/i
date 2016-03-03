package org.igov.service.controller.actionexecute;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.activation.DataSource;
import javax.mail.MessagingException;

import org.activiti.engine.task.Attachment;
import org.apache.commons.mail.ByteArrayDataSource;
import org.apache.commons.mail.EmailException;
import org.igov.io.mail.Mail;
import org.igov.model.action.execute.item.ActionExecute;
import org.igov.model.action.execute.item.ActionExecuteDAO;
import org.igov.model.action.execute.item.ActionExecuteOld;
import org.igov.model.action.execute.item.ActionExecuteOldDAO;
import org.igov.model.action.execute.item.ActionExecuteStatus;
import org.igov.model.action.execute.item.ActionExecuteStatusDAO;
import org.igov.service.exception.CommonServiceException;
import org.igov.util.MethodsCallRunnerUtil;
import org.igov.util.JSON.JsonRestUtils;
import org.joda.time.DateTime;
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

@Controller
@Api(tags = {"ActionExecuteController"})
@RequestMapping(value = "/action/execute")
public class ActionExecuteController {
	
	@Autowired
	private Mail mail;
	
	@Autowired
	private ActionExecuteStatusDAO actionExecuteStatusDAO;
	@Autowired
	private ActionExecuteDAO actionExecuteDAO;
	@Autowired
	private ActionExecuteOldDAO actionExecuteOldDAO;
	
	@Autowired
	private MethodsCallRunnerUtil methodCallRunner; 
    
	private static final Logger LOG = LoggerFactory.getLogger(ActionExecuteController.class);

    @ApiOperation(value = "Получение массива всех акций ", notes = "")
    @RequestMapping(value = "/getActionExecuteStatuses", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, headers = {"Accept=application/json"})
    public
    @ResponseBody
    ResponseEntity getActionExecuteStatuses() {

        List<ActionExecuteStatus> actionExecuteList = actionExecuteStatusDAO.findAll();
        return JsonRestUtils.toJsonResponse(actionExecuteList);
    }
       

    @ApiOperation(value = " получить массив всех акций. ", notes = "")
    @RequestMapping(value = "/getActionExecute", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, headers = {"Accept=application/json"})
    public
    @ResponseBody
    ResponseEntity getActionExecute(
    		 @ApiParam(value = "число строк максимальное (максимально - не более 1000)", required = false) @RequestParam(value = "nRowsMax", required = false, defaultValue = "10") Integer nRowsMax,
    		 @ApiParam(value = "выбрать только с указанной маской метода", required = false) @RequestParam(value = "sMethodMask", required = false) String sMethodMask,
             @ApiParam(value = "булевый, true=только из олд-таблицы, иначе только из основной (по умолчанию из основной", required = false) @RequestParam(value = "bOldOnly", required = false, defaultValue = "false") Boolean bOldOnly,
             @ApiParam(value = "выбрать только с указанными статусами (массив JSON)", required = false) @RequestParam(value = "asID_Status", required = false) String asID_Status,
             @ApiParam(value = "выбрать только те, у которых число попыток не превышает указанный лимит (иначе с любым числом попыток)", required = false) @RequestParam(value = "nTryMax", required = false) Integer nTryMax,
    		 @ApiParam(value = "номер-ИД записи", required = false) @RequestParam(value = "nID", required = false) Long nID) throws EmailException{
        	
    		ResponseEntity<String> res = null;
    		/*if(bOldOnly)
    			res = JsonRestUtils.toJsonResponse(actionExecuteOldDAO.getActionExecute(nRowsMax, sMethodMask, asID_Status, nTryMax, nID));
    		else
    			res = JsonRestUtils.toJsonResponse(actionExecuteDAO.getActionExecute(nRowsMax, sMethodMask, asID_Status, nTryMax, nID));*/
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
        	return res;
    }

    @ApiOperation(value = "выполнить задачи.", notes = "")
    @RequestMapping(value = "/runActionExecute", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, headers = {"Accept=application/json"})
    public
    @ResponseBody
    ResponseEntity runActionExecute(
    		 @ApiParam(value = "число строк максимальное (максимально - не более 1000)", required = false) @RequestParam(value = "nRowsMax", required = false, defaultValue = "10") Integer nRowsMax,
    		 @ApiParam(value = "выбрать только с указанной маской метода", required = false) @RequestParam(value = "sMethodMask", required = false) String sMethodMask,
             @ApiParam(value = "булевый, true=только из олд-таблицы, иначе только из основной (по умолчанию из основной", required = false) @RequestParam(value = "bOldOnly", required = false, defaultValue = "false") Boolean bOldOnly,
             @ApiParam(value = "выбрать только с указанными статусами (массив JSON)", required = false) @RequestParam(value = "asID_Status", required = false) String asID_Status,
             @ApiParam(value = "выбрать только те, у которых число попыток не превышает указанный лимит (иначе с любым числом попыток)", required = false) @RequestParam(value = "nTryMax", required = false) Integer nTryMax,
    		 @ApiParam(value = "номер-ИД записи", required = false) @RequestParam(value = "nID", required = false) Long nID) throws CommonServiceException{    	
        return JsonRestUtils.toJsonResponse(methodCallRunner.runMethod(nRowsMax, sMethodMask, asID_Status, nTryMax, nID));
    }
    
    @ApiOperation(value = "переместить записи из основной таблицы в олд или обратно.", notes = "")
    @RequestMapping(value = "/moveActionExecute", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, headers = {"Accept=application/json"})
    public
    @ResponseBody
    ResponseEntity moveActionExecute(
    		@ApiParam(value = "число строк максимальное (максимально - не более 1000)", required = false) @RequestParam(value = "nRowsMax", required = false, defaultValue = "10") Integer nRowsMax,
    		@ApiParam(value = "выбрать только с указанной маской метода", required = false) @RequestParam(value = "sMethodMask", required = false) String sMethodMask,
    		@ApiParam(value = "булевый, true=только из олд-таблицы, иначе только из основной (по умолчанию из основной", required = false) @RequestParam(value = "bOldOnly", required = false, defaultValue = "false") Boolean bOldOnly,
    		@ApiParam(value = "выбрать только с указанными статусами (массив JSON)", required = false) @RequestParam(value = "asID_Status", required = false) String asID_Status,
    		@ApiParam(value = "выбрать только те, у которых число попыток не превышает указанный лимит (иначе с любым числом попыток)", required = false) @RequestParam(value = "nTryMax", required = false) Integer nTryMax,
    		@ApiParam(value = "номер-ИД записи", required = false) @RequestParam(value = "nID", required = false) Long nID,
    		@ApiParam(value = "булевый, если указан true, то переместить из олд-а в основную (по умолчанию наоборот)", required = false) @RequestParam(value = "bBack", required = false, defaultValue="false") Boolean bBack){
    	if(bBack)
    		actionExecuteOldDAO.moveActionExecuteOld(nRowsMax, sMethodMask, asID_Status, nTryMax, nID);
    	else
    		actionExecuteDAO.moveActionExecute(nRowsMax, sMethodMask, asID_Status, nTryMax, nID);
    	return null;
    }        
    
}
