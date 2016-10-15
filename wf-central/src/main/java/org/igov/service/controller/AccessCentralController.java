package org.igov.service.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.igov.service.business.access.AccessDataService;
import org.igov.service.controller.security.AccessContract;
import org.igov.service.controller.security.AuthenticationTokenSelector;
import org.igov.service.exception.CommonServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Api(tags = { "AccessCentralController -- Доступ центральный" })
@RequestMapping(value = "/access")
public class AccessCentralController {

    private static final Logger LOG = LoggerFactory.getLogger(AccessCentralController.class);

    @Autowired
    private AccessDataService oAccessDataService;

    @ApiOperation(value = "Получения ключа для аутентификации", notes =
            "##### Возвращает ключ для аутентификации. Пример:\n"
            + "https://test.igov.org.ua/wf/service/access/getAccessKey?sAccessLogin=activiti-master&sAccessContract=Request&sData=/wf/service/setMessage\n")
    @RequestMapping(value = "/getAccessKey", method = RequestMethod.GET)
    public @ResponseBody
    String getAccessKey(
            @ApiParam(value = "строка-технический логин", required = true) @RequestParam(value = AuthenticationTokenSelector.ACCESS_LOGIN) String sAccessLogin,
            @ApiParam(value = "строка-контракт", required = true) @RequestParam(value = AuthenticationTokenSelector.ACCESS_CONTRACT) String sAccessContract,
            @ApiParam(value = "строка-контент по которому генерируется ключ", required = true) @RequestParam(value = "sData") String sData
    ) throws CommonServiceException {

        if (AccessContract.RequestAndLoginUnlimited.name().equals(sAccessContract)
                || AccessContract.RequestAndLogin.name().equals(sAccessContract)) {
            //if(sData!=null && !"".equals(sData.trim()) && !sData.trim().endsWith("?")){
            if (sData != null && !"".equals(sData.trim())) {
                sData = sData
                        + (sData.contains("?") ? "&" : "?")
                        + AuthenticationTokenSelector.ACCESS_LOGIN + "=" + sAccessLogin
                        //TODO: Need inclide in future!!!
                        + "&sAccessContract=" + sAccessContract;
            }
            //}else if(AccessContract.Request.name().equals(sAccessContract)){
            //}else{
        }
        return oAccessDataService.setAccessData(sData);
    }
}
