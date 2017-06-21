package org.igov.service.controller;

import java.util.List;

import org.igov.model.arm.DboTkModel;
import org.igov.model.arm.DboTkResult;
import org.igov.service.business.arm.ArmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Controller
@Api(tags = {"ArmController — Трансфер данных в таблицу"})
@RequestMapping(value = "/arm")
public class ArmController {
	private static final Logger LOG = LoggerFactory.getLogger(ArmController.class);

	@Autowired
    private ArmService armService;
	
    @ApiOperation(value = "Получение данных из таблицы arm.dbo.TK", notes = "##### Пример:\n"
            + "https://alpha.test.region.igov.org.ua/wf/service/arm/getDboTkByOutNumber?sID_Order=10358663\n")
    @RequestMapping(value = "/getDboTkByOutNumber", method = RequestMethod.GET)
    @ResponseBody
    public List<DboTkModel> getDboTkByOutNumber(@ApiParam(value = "Номер заявки из айдок", required = true) @RequestParam(value = "sID_Order") String sID_Order)
            throws Exception {
    	List<DboTkModel> dboTkModelList = null;
        try {
        	dboTkModelList = armService.getDboTkByOutNumber(sID_Order);

        } catch (Exception e) {
        	LOG.error("FAIL: {}", e.getMessage());
        }
        return dboTkModelList;
    }
    
    
    @ApiOperation(value = "Обновление данных таблицы arm.dbo.TK", notes = "##### Пример:\n"
            + "https://alpha.test.region.igov.org.ua/wf/service/arm/updateDboTk\n")
    @RequestMapping(value = "/updateDboTk", method = RequestMethod.POST)
    @ResponseBody
    public DboTkResult updateDboTk(@ApiParam(value = "Данные для обновления данных в таблице arm.dbo.TK ", required = true) @RequestBody DboTkModel dboTkModel)
            throws Exception {
    	DboTkResult dboTkResult = null;
    	try {
    		dboTkResult =  armService.updateDboTk(dboTkModel);
    	 } catch (Exception e) {
    		 dboTkResult = new DboTkResult();
             dboTkResult.setMess(e.getMessage());
             dboTkResult.setCode("e");
             dboTkResult.setState("e");
             return dboTkResult;
         }
		return dboTkResult;
    }
    
    
    @ApiOperation(value = "Обновление данных таблицы arm.dbo.TK", notes = "##### Пример:\n"
            + "https://alpha.test.region.igov.org.ua/wf/service/arm/createDboTk\n")
    @RequestMapping(value = "/createDboTk", method = RequestMethod.POST)
    @ResponseBody
    public DboTkResult createDboTk(@ApiParam(value = "Данные для добавление новой записи в таблицу arm.dbo.TK ", required = true) @RequestBody DboTkModel dboTkModel)
            throws Exception {
    	DboTkResult dboTkResult = null;
    	try {
    		dboTkResult =  armService.createDboTk(dboTkModel);
    	 } catch (Exception e) {
    		 dboTkResult = new DboTkResult();
             dboTkResult.setMess(e.getMessage());
             dboTkResult.setCode("e");
             dboTkResult.setState("e");
         }
    	
    	return dboTkResult;
    }
    
    
    @ApiOperation(value = "Получение MaxNumber441 из таблицы arm.dbo.TK", notes = "##### Пример:\n"
            + "https://alpha.test.region.igov.org.ua/wf/service/arm/getMaxValue\n")
    @RequestMapping(value = "/getMaxValue", method = RequestMethod.GET)
    @ResponseBody
    public Integer getMaxValue()
            throws Exception {
    	Integer maxValue = null;
        try {
        	maxValue = armService.getMaxValue();

        } catch (Exception e) {
        	LOG.error("FAIL: {}", e.getMessage());
        }
        return maxValue;
    }
	
}
