package org.igov.service.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.igov.service.business.address.AddressService;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Api(tags = {"AddresssCommonController"})
@RequestMapping(value = "/common/address")
public class AddresssCommonController {

    private static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json;charset=UTF-8";

    @Autowired
    AddressService oAddressService;

    @ApiOperation(value = "Получение списка областей", notes = "##### Пример:\n"
            + "https://delta.test.region.igov.org.ua/wf/service/common/address/getListRegions\n\n")
    @RequestMapping(value = "/getListRegions", method = RequestMethod.GET,
            produces = APPLICATION_JSON_CHARSET_UTF_8)
    public @ResponseBody
    JSONArray getListRegions() throws Exception {
        return oAddressService.getListRegions();
    }
    
    @ApiOperation(value = "Получение списка областей", notes = "##### Пример:\n"
            + "https://delta.test.region.igov.org.ua/wf/service/common/address/getListDistricts\n\n")
    @RequestMapping(value = "/getListDistricts", method = RequestMethod.GET,
            produces = APPLICATION_JSON_CHARSET_UTF_8)
    public @ResponseBody
    JSONArray getListDistricts(@RequestParam(value = "sRegion", required = true) String sRegion) throws Exception {
        return oAddressService.getListDistricts(sRegion);
    }
}
