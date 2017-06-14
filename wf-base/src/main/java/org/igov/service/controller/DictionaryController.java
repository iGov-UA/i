package org.igov.service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Map;
import org.igov.model.action.vo.Relation_VO;
import org.igov.service.business.dictionary.DictionaryService;
import org.igov.service.business.relation.RelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.transaction.annotation.Transactional;


/**
 *
 * @author Kovilin
 */
@Controller
@Api(tags = {"DictionaryController — Обработка csv-файлов без бд"})
@RequestMapping(value = "/dictionary")
public class DictionaryController {
    
    private static final Logger LOG = LoggerFactory.getLogger(DictionaryController.class);
    
    @Autowired
    DictionaryService oDictionaryService;
    
    @ApiOperation(value = "Обработка csv-файлов без бд", notes = "##### Пример:\n"
            + "https://alpha.test.region.igov.org.ua/wf/service/dictionary/processDictionary?sPath=mreo&sID_Field=sID&sValue=7 \n")
    @RequestMapping(value = "/processDictionary", method = RequestMethod.GET)
    @Transactional
    public @ResponseBody
    List<Map<String, String>> processDictionary (@RequestParam(value = "sPath", required = true) String sPath,
                                    @RequestParam(value = "sID_Field", required = false) String sID_Field,
                                    @RequestParam(value = "sValue", required = false) String sValue) throws Exception
    {
        LOG.info("processDictionary started");
        LOG.info("sPath = {} sID_Field = {} sValue = {}", sPath, sID_Field, sValue);
        
        return oDictionaryService.processDictionary(sPath, sID_Field, sValue);
    }
}