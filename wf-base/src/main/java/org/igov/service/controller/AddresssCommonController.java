package org.igov.service.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;
import org.igov.service.business.address.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Api(tags = { "AddresssCommonController" })
@RequestMapping(value = "/common/address")
public class AddresssCommonController {
    
    @Autowired
    AddressService oAddressService;
    
    /**
     * Получение списка областей
     *
     * @param nID_Flow_ServiceData - ИД потока
     * @param sID_BP строка-ИД БизнесПроцесса
     * @param nID_SubjectOrganDepartment ИД номер-ИН департамента
     * @return List of schedule with bExclude=false
     */
    @ApiOperation(value = "Получение списка областей", notes = "##### Пример:\n"
            + "https://test.region.igov.org.ua/wf/service/common/address/getListRegions\n\n"
            + "Пример результата\n\n"
            + "\n```json\n"
            + "[\n"
            + "  {\n"
            + "    \"sData\": null,\n"
            + "    \"bExclude\": false,\n"
            + "    \"sName\": \"Test\",\n"
            + "    \"sRegionTime\": \"\"10:30-11:30\"\",\n"
            + "    \"saRegionWeekDay\": \"\"mo,tu\"\",\n"
            + "    \"sDateTimeAt\": \"\"2010-08-01 10:10:30\"\",\n"
            + "    \"sDateTimeTo\": \"\"2010-08-01 18:10:00\"\",\n"
            + "    \"nID\": 20367,\n"
            + "    \"nID_FlowPropertyClass\": {\n"
            + "      \"sPath\": \"org.igov.service.business.flow.handler.DefaultFlowSlotScheduler\",\n"
            + "      \"sBeanName\": \"defaultFlowSlotScheduler\",\n"
            + "      \"nID\": 1,\n"
            + "      \"sName\": \"DefaultFlowSlotScheduler\"\n"
            + "    }\n"
            + "  },\n"
            + "  {\n"
            + "    \"sData\": null,\n"
            + "    \"bExclude\": false,\n"
            + "    \"sName\": \"Test\",\n"
            + "    \"sRegionTime\": \"10:30-11:30\",\n"
            + "    \"saRegionWeekDay\": \"mo,tu\",\n"
            + "    \"sDateTimeAt\": \"10:30\",\n"
            + "    \"sDateTimeTo\": \"12:30\",\n"
            + "    \"nID\": 20364,\n"
            + "    \"nID_FlowPropertyClass\": {\n"
            + "      \"sPath\": \"org.igov.service.business.flow.handler.DefaultFlowSlotScheduler\",\n"
            + "      \"sBeanName\": \"defaultFlowSlotScheduler\",\n"
            + "      \"nID\": 1,\n"
            + "      \"sName\": \"DefaultFlowSlotScheduler\"\n"
            + "    }\n"
            + "  }\n"
            + "]\n"
            + "\n```\n")
    @RequestMapping(value = "/getListRegions", method = RequestMethod.GET)
    public @ResponseBody
    String getListRegions() throws Exception {
        return oAddressService.getListRegions();
    }
}
