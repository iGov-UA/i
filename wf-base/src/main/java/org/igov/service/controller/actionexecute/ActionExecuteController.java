package org.igov.service.controller.actionexecute;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.List;

import org.igov.model.action.execute.item.ActionExecute;
import org.igov.model.action.execute.item.ActionExecuteDAO;
import org.igov.model.action.execute.item.ActionExecuteOld;
import org.igov.model.action.execute.item.ActionExecuteOldDAO;
import org.igov.model.action.execute.item.ActionExecuteStatus;
import org.igov.model.action.execute.item.ActionExecuteStatusDAO;
import org.igov.service.exception.CommonServiceException;
import org.igov.util.JSON.JsonRestUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Api(tags = {"ActionExecuteController"})
@RequestMapping(value = "/action/execute")
public class ActionExecuteController {
	
	@Autowired
	private ActionExecuteStatusDAO actionExecuteStatusDAO;
	@Autowired
	private ActionExecuteDAO actionExecuteDAO;
	@Autowired
	private ActionExecuteOldDAO actionExecuteOldDAO;
	
	
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
    		 @ApiParam(value = "выбрать только с указанной маской метода", required = false) @RequestParam(value = "sMethodMask ", required = false) String sMethodMask,
             @ApiParam(value = "булевый, true=только из олд-таблицы, иначе только из основной (по умолчанию из основной", required = false) @RequestParam(value = "bOldOnly", required = false, defaultValue = "false") Boolean bOldOnly,
             @ApiParam(value = "выбрать только с указанными статусами (массив JSON)", required = false) @RequestParam(value = "asID_Status", required = false) String asID_Status,
             @ApiParam(value = "выбрать только те, у которых число попыток не превышает указанный лимит (иначе с любым числом попыток)", required = false) @RequestParam(value = "nTryMax", required = false) Long nTryMax,
    		 @ApiParam(value = "номер-ИД записи", required = false) @RequestParam(value = "nID", required = false) Long nID){
        	
    		ResponseEntity<String> res = null;
    		if(bOldOnly)
    			res = JsonRestUtils.toJsonResponse(actionExecuteOldDAO.getActionExecute(nRowsMax, sMethodMask, asID_Status, nTryMax, nID));
    		else
    			res = JsonRestUtils.toJsonResponse(actionExecuteDAO.getActionExecute(nRowsMax, sMethodMask, asID_Status, nTryMax, nID));
        	return res;
    }

    @ApiOperation(value = "выполнить задачи.", notes = "")
    @RequestMapping(value = "/runActionExecute", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, headers = {"Accept=application/json"})
    public
    @ResponseBody
    ResponseEntity runActionExecute(
    		 @ApiParam(value = "число строк максимальное (максимально - не более 1000)", required = false) @RequestParam(value = "nRowsMax", required = false, defaultValue = "10") Integer nRowsMax,
    		 @ApiParam(value = "выбрать только с указанной маской метода", required = false) @RequestParam(value = "sMethodMask ", required = false) String sMethodMask,
             @ApiParam(value = "булевый, true=только из олд-таблицы, иначе только из основной (по умолчанию из основной", required = false) @RequestParam(value = "bOldOnly", required = false, defaultValue = "false") Boolean bOldOnly,
             @ApiParam(value = "выбрать только с указанными статусами (массив JSON)", required = false) @RequestParam(value = "asID_Status", required = false) String asID_Status,
             @ApiParam(value = "выбрать только те, у которых число попыток не превышает указанный лимит (иначе с любым числом попыток)", required = false) @RequestParam(value = "nTryMax", required = false) Long nTryMax,
    		 @ApiParam(value = "номер-ИД записи", required = false) @RequestParam(value = "nID", required = false) Long nID){
        
        return JsonRestUtils.toJsonResponse(null);
    }
    
    @ApiOperation(value = "переместить записи из основной таблицы в олд или обратно.", notes = "")
    @RequestMapping(value = "/moveActionExecute", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, headers = {"Accept=application/json"})
    public
    @ResponseBody
    ResponseEntity moveActionExecute(
    		@ApiParam(value = "число строк максимальное (максимально - не более 1000)", required = false) @RequestParam(value = "nRowsMax", required = false, defaultValue = "10") Integer nRowsMax,
    		@ApiParam(value = "выбрать только с указанной маской метода", required = false) @RequestParam(value = "sMethodMask ", required = false) String sMethodMask,
    		@ApiParam(value = "булевый, true=только из олд-таблицы, иначе только из основной (по умолчанию из основной", required = false) @RequestParam(value = "bOldOnly", required = false, defaultValue = "false") Boolean bOldOnly,
    		@ApiParam(value = "выбрать только с указанными статусами (массив JSON)", required = false) @RequestParam(value = "asID_Status", required = false) String asID_Status,
    		@ApiParam(value = "выбрать только те, у которых число попыток не превышает указанный лимит (иначе с любым числом попыток)", required = false) @RequestParam(value = "nTryMax", required = false) Long nTryMax,
    		@ApiParam(value = "номер-ИД записи", required = false) @RequestParam(value = "nID", required = false) Long nID,
    		@ApiParam(value = "булевый, если указан true, то переместить из олд-а в основную (по умолчанию наоборот)", required = false) @RequestParam(value = "bBack", required = false, defaultValue="false") Boolean bBack){
    	
    	return JsonRestUtils.toJsonResponse(null);
    }    
    
}
