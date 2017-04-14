package org.igov.service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import java.util.List;
import org.igov.model.action.vo.Relation_VO;
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
@Api(tags = {"RelationController — Обработка Relation"})
@RequestMapping(value = "/relation")
public class RelationController {
    
    private static final Logger LOG = LoggerFactory.getLogger(RelationController.class);
    
    @Autowired
    RelationService oRelationService;
    
    @RequestMapping(value = "/getRelations", method = RequestMethod.GET)
    @Transactional
    public @ResponseBody
    List<Relation_VO> getRelations(@RequestParam(value = "sID_Relation", required = true) String sID_Relation,
                                     @RequestParam(value = "nID_Parent", required = false) Long nID_Parent) throws Exception
    {
        LOG.info("getRelations started");
        return oRelationService.getRelations(sID_Relation, nID_Parent);
    }
}