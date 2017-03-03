package org.igov.service.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.igov.service.business.export.AgroholdingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Api(tags = {"ExportCommonController -- вызов стороннего АПИ"})
@Controller
@RequestMapping(value = "/export")
public class ExportCommonController {

    private static final Logger LOG = LoggerFactory.getLogger(ExportCommonController.class);

    @Autowired
    AgroholdingService agroholdingService;

    @ApiOperation(value = "/agroholding/transferDocumentVacation", notes = "##### Экспорт документа о отпуске в агрофирму. Примеры:\n"
            + "https://alpha.test.igov.org.ua/wf/service/export/agroholding/transferDocumentVacation")
    @RequestMapping(value = "/agroholding/transferDocumentVacation", method = RequestMethod.GET)
    public @ResponseBody String transferDocumentVacation() throws Exception {
        LOG.info("transferDocumentVacation...");
        String result = agroholdingService.transferDocumentVacation();
        LOG.info("transferDocumentVacation result = " + result);
        return result;
    }

}
