package org.igov.service.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.rmi.RemoteException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.rpc.ServiceException;

import org.igov.bjust.IService;
import org.igov.bjust.ResultModel;
import org.igov.bjust.ServiceLocator;
import org.igov.bjust.Signs;
import org.igov.bjust.UploadSignsModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Api(tags = { "MUController -- контроллер для взаимодействия с веб сервисом министерства юстиции" })
@RequestMapping(value = "/MU")
public class MUController {

	private final Logger LOG = LoggerFactory.getLogger(FinanceCentralController.class);
	
	@ApiOperation(value = "")
    @RequestMapping(value = "/setOrder", method = RequestMethod.POST)
    public @ResponseBody
    String getObjectEarthTargets(
    		@ApiParam(value = "application_id", required = false) 
    		@RequestParam(value = "application_id", required = false) String sApplicationId,
    		@ApiParam(value = "sessionId", required = false) 
    		@RequestParam(value = "sessionId", required = false) String sSessionId,
    		@ApiParam(value = "fileName", required = false) 
    		@RequestParam(value = "fileName", required = false) String sFileName,
            @ApiParam(value = "Документ для передачи в веб сервис министерства юстиции", required = false) @RequestParam("file_sign") byte[] file_sign,
            @ApiParam(value = "Подпись документа для передачи в веб сервис министерства юстиции", required = false) 
    		@RequestParam("file_stamp") byte[] file_stamp,
            HttpServletRequest request) throws ServiceException, RemoteException {
		ServiceLocator serviceLocator = new ServiceLocator();
		IService service = serviceLocator.getBinding_IService();
		LOG.info("Got web service locator. Length of parameters of the method: " + file_sign != null ? String.valueOf(file_sign.length) : "-1" + ":" + 
				file_stamp != null ? String.valueOf(file_stamp.length) : "-1");
		UploadSignsModel uploadSignsModel = new UploadSignsModel();
		
		Signs signs = new Signs();
		signs.setFile_name(sFileName);
		signs.setFile_sign(String.valueOf(file_sign));
		signs.setFile_stamp(String.valueOf(file_stamp));
		Signs[] params = new Signs[1];
		params[0] = signs;
		uploadSignsModel.setFiles(params);
		
		LOG.info("Before calling upload signs method");
		
		ResultModel resultModel = service.uploadSigns(uploadSignsModel);
		
		LOG.info("Received response from min just web service:" + resultModel.getResult() + ":" + resultModel.getMessage());
		
		return resultModel.getResult();
    }
	
}
