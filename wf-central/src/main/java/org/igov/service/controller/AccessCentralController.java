package org.igov.service.controller;

import org.igov.service.security.AuthenticationTokenSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.igov.model.AccessDataDao;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.igov.service.interceptor.exception.ActivitiRestException;

@Controller
@Api(tags = { "AccessCentralController" }, description = "AccessCentralController")
@RequestMapping(value = "/services")
public class AccessCentralController {

    private static final Logger log = LoggerFactory.getLogger(AccessCentralController.class);

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Подробные описания сервисов для документирования в Swagger
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    private static final String noteCODE= "\n```\n";    
    private static final String noteCODEJSON= "\n```json\n";    
    private static final String noteController = "##### AccessController. ";

    private static final String noteGetAccessKey = noteController    		
    	+ "Получения ключа для аутентификации. #####\n\n"
    	+ "HTTP Context: http://server:port/wf/service/services/getAccessKey?\n\n"
    	+ " -- возвращает ключ для аутентификации\n\n"
    	+ "- sAccessContract - контракт\n"
    	+ "- sAccessLogin - технический логин\n"
    	+ "- sData - контент по которому генерируется ключ\n\n"
    	+ "Пример:\n"
   	+ "https://test.igov.org.ua/wf/service/services/getAccessKey?sAccessLogin=activiti-master&sAccessContract=Request&sData=/wf/service/setMessage\n";    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    @Autowired
    private AccessDataDao accessDataDao;

    /**
     * @param sAccessContract контракт
     * @param sAccessLogin технический логин
     * @param sData контент по которому генерируется ключ
     */
    @ApiOperation(value = "Получения ключа для аутентификации", notes = noteGetAccessKey )
    @RequestMapping(value = "/getAccessKey", method = RequestMethod.GET)
    public
    @ResponseBody
    String getAccessKey(
            //@RequestParam(value = "sAccessLogin") String sAccessLogin,
	    @ApiParam(value = "технический логин", required = true) @RequestParam(value = AuthenticationTokenSelector.ACCESS_LOGIN) String sAccessLogin,
            //@RequestParam(value = "sAccessContract") String sAccessContract,
	    @ApiParam(value = "контракт", required = true) @RequestParam(value = AuthenticationTokenSelector.ACCESS_CONTRACT) String sAccessContract,
	    @ApiParam(value = "контент по которому генерируется ключ", required = true) @RequestParam(value = "sData") String sData
    ) throws ActivitiRestException {

        //public static final String ACCESS_CONTRACT_REQUEST = "Request";
        //public static final String ACCESS_CONTRACT_REQUEST_AND_LOGIN = "RequestAndLogin";
        if (AuthenticationTokenSelector.ACCESS_CONTRACT_REQUEST_AND_LOGIN_UNLIMITED.equals(sAccessContract)
                || AuthenticationTokenSelector.ACCESS_CONTRACT_REQUEST_AND_LOGIN.equals(sAccessContract)) {
            //if(sData!=null && !"".equals(sData.trim()) && !sData.trim().endsWith("?")){
            if (sData != null && !"".equals(sData.trim())) {
                sData = sData + (sData.contains("?") ? "&" : "?") + AuthenticationTokenSelector.ACCESS_LOGIN + "="
                        + sAccessLogin;
            }
            //}else if(ACCESS_CONTRACT_REQUEST.equals(sAccessContract)){
            //}else{
        }
        return accessDataDao.setAccessData(sData);
    }
}
