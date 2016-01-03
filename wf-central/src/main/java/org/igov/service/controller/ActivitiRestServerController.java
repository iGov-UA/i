package org.igov.service.controller;

import com.google.common.base.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.igov.util.convert.JsonRestUtils;
import org.igov.model.ServerDao;
import org.igov.model.Server;
import org.igov.model.Service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import org.igov.service.controller.RecordNotFoundException;

/**
 * User: goodg_000
 * Date: 29.10.2015
 * Time: 22:07
 */
@Controller
@Api(tags = { "ActivitiRestServerController" }, description = "ActivitiRestServerController")
@RequestMapping(value = "/server")
public class ActivitiRestServerController {

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Подробные описания сервисов для документирования в Swagger
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    private static final String noteCODE= "\n```\n";    
    private static final String noteCODEJSON= "\n```json\n";    
    private static final String noteController = "##### ActivitiRestServerController. ";

    private static final String noteGetServer = noteController + "Получение информации о сервере #####\n\n"
        + "HTTP Context: https://test.region.igov.org.ua/wf/service/server/getServer?nID=nID\n\n\n"
        + "возвращает json представление сущности Server, которая содержит информацию о сервере.\n\n"
        + "- nID - nID сервера.\n\n\n"
        + "Примеры:\n"
        + "https://test.region.igov.org.ua/wf/service/server/getServer?nID=0\n\n"
        + "Ответ:\n"
        + noteCODEJSON
        + "{\n"
        + "    \"sID\": \"Common_Region\",\n"
        + "    \"sType\": \"Region\",\n"
        + "    \"sURL_Alpha\": \"https://test.region.igov.org.ua/wf\",\n"
        + "    \"sURL_Beta\": \"https://test-version.region.igov.org.ua/wf\",\n"
        + "    \"sURL_Omega\": \"https://master-version.region.igov.org.ua/wf\",\n"
        + "    \"sURL\": \"https://region.igov.org.ua/wf\",\n"
        + "    \"nID\": 0\n"
        + "}\n"
        + noteCODE
        + "https://test.region.igov.org.ua/wf/service/server/getServer?nID=-1\n"
        + "Ответ:\n"
        + "HTTP Status: 500 (internal server error)\n"
        + noteCODEJSON
        + "{\n"
        + "    \"code\": \"BUSINESS_ERR\",\n"
        + "    \"message\": \"Record not found\"\n"
        + "}\n"
        + noteCODE;
///////////////////////////////////////////////////////////////////////////////////////////////////////

        @Autowired
    private ServerDao serverDao;

    /**
     * @param nID nID сервера.
     */
    @ApiOperation(value = "Получение информации о сервере", notes = noteGetServer )
    @RequestMapping(value = "/getServer", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity getService(@ApiParam(value = "nID сервера", required = true) @RequestParam(value = "nID") Long nID) throws RecordNotFoundException {
        Optional<Server> serverOpt = serverDao.findById(nID);
        if (!serverOpt.isPresent()) {
            throw new RecordNotFoundException();
        }

        return JsonRestUtils.toJsonResponse(serverOpt.get());
    }
}
