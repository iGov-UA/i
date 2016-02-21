package org.igov.service.controller.actionexecute;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.igov.model.action.execute.item.ActionExecute;
import org.igov.model.action.execute.item.ActionExecuteDAO;
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
	
    private static final Logger LOG = LoggerFactory.getLogger(ActionExecuteController.class);

    @ApiOperation(value = "Получение массива всех акций ", notes = "")
    @RequestMapping(value = "/getActionExecuteStatuses", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, headers = {"Accept=application/json"})
    public
    @ResponseBody
    ResponseEntity getActionExecuteStatuses() {

        List<ActionExecuteStatus> actionExecuteList = actionExecuteStatusDAO.findAll();
        return JsonRestUtils.toJsonResponse(actionExecuteList);
    }
    
    @ApiOperation(value = "Создание акции ", notes = "")
    @RequestMapping(value = "/setActionExecute", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, headers = {"Accept=application/json"})
    public
    @ResponseBody
    Long setActionExecute() {

        Long actionExecuteId = actionExecuteDAO.setActionExecute(1L, new DateTime(), new DateTime(), 1, "method", "request", "smParam", "sReturn");
        return actionExecuteId;
    }
 
    @ApiOperation(value = "Получение акции ", notes = "")
    @RequestMapping(value = "/getActionExecute", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, headers = {"Accept=application/json"})
    public
    @ResponseBody
    ActionExecute getActionExecute(@RequestParam(value = "nID") Long id) throws CommonServiceException {
        ActionExecute actionExecute = actionExecuteDAO.getActionExecute(id);
        return actionExecute;
    }
    
    @ApiOperation(value = "Получение акции ", notes = "")
    @RequestMapping(value = "/getActionExecutes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, headers = {"Accept=application/json"})
    public
    @ResponseBody
    List<ActionExecute> getActionExecutes() throws CommonServiceException {
        List<ActionExecute> actionExecutes = actionExecuteDAO.findAll();
        return actionExecutes;
    }
}
