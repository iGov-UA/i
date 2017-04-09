package org.igov.service.controller;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.HistoryService;
import org.igov.service.business.document.DocumentStepService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.HashMap;
import java.util.List;

import org.igov.model.action.vo.DocumentSubmitedUnsignedVO;
import org.igov.model.document.DocumentStepSubjectRight;
import org.igov.service.business.action.task.systemtask.DeleteProccess;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

@Controller
@Api(tags = {"DocumentCommonController -- Проверки наложености ЭЦП по строкам-подписантам"})
@RequestMapping(value = "/common/document")

public class DocumentCommonController {

    @Autowired
    private DocumentStepService documentStepService;
    
    @Autowired
    private DeleteProccess deleteProccess;

    private static final Logger LOG = LoggerFactory.getLogger(DocumentCommonController.class);

    @ApiOperation(value = "проверка подписано ли ЕЦП")
    @RequestMapping(value = "/isDocumentStepSubmitedAll", method = RequestMethod.GET)
    @Transactional
    public @ResponseBody
    Map<String, Object> isDocumentStepSubmitedAll(
            @ApiParam(value = "ИД процесс-активити", required = false) @RequestParam(required = false, value = "nID_Process") String nID_Process,
            @ApiParam(value = "Логин подписанта", required = false) @RequestParam(required = false, value = "sLogin") String sLogin,
            @ApiParam(value = "Ключ шага документа", required = false) @RequestParam(required = false, value = "sKey_Step") String sKey_Step,
            HttpServletResponse httpResponse) throws Exception {

        LOG.info("snID_Process_Activiti: " + nID_Process);
        LOG.info("sLogin: " + sLogin);
        LOG.info("sKey_Step: " + sKey_Step);
        return documentStepService.isDocumentStepSubmitedAll(nID_Process, sLogin, sKey_Step);

    }

    @ApiOperation(value = "Клонирование подписанта-субьекта документа")
    @RequestMapping(value = "/cloneDocumentStepSubject", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    //@Transactional
    public @ResponseBody
    String cloneDocumentStepSubject(
            @ApiParam(value = "ИД процесс-активити", required = false) @RequestParam(required = false, value = "snID_Process_Activiti") String snID_Process_Activiti,
            @ApiParam(value = "группа", required = false) @RequestParam(required = false, value = "sKeyGroupPostfix") String sKey_GroupPostfix,
            @ApiParam(value = "новая группа", required = false) @RequestParam(required = false, value = "sKeyGroupPostfix_New") String sKey_GroupPostfix_New,
            @ApiParam(value = "степ документа", required = false) @RequestParam(required = false, value = "sID_Step") String sID_Step) throws Exception {

        LOG.info("snID_Process_Activiti: {}", snID_Process_Activiti);
        LOG.info("sKey_GroupPostfix: {}", sKey_GroupPostfix);
        LOG.info("sKey_GroupPostfix_New: {}", sKey_GroupPostfix_New);

        List<DocumentStepSubjectRight> aDocumentStepSubjectRight = documentStepService.cloneDocumentStepSubject(snID_Process_Activiti, sKey_GroupPostfix, sKey_GroupPostfix_New, sID_Step);
        
        LOG.info("oDocumentStepSubjectRight in cloneDocumentStepSubject is {}", aDocumentStepSubjectRight);
        
        if (aDocumentStepSubjectRight != null) {
            return JSONValue.toJSONString(aDocumentStepSubjectRight);
        }

        return "DocumentStepSubjectRight is null";

    }
    
    @ApiOperation(value = "Синхронизация сабмитеров на степе по полю ")
    @RequestMapping(value = "/syncDocumentSubmitersByField", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    //@Transactional
    public @ResponseBody
    String syncDocumentSubmitersByField(
            @ApiParam(value = "ИД процесс-активити", required = true) @RequestParam(required = true, value = "snID_Process_Activiti") String snID_Process_Activiti,
            @ApiParam(value = "группа - эталон/дэфолтная", required = true) @RequestParam(required = true, value = "sKeyGroup_Default") String sKey_Group_Default,
            @ApiParam(value = "строка-ИД поля, в рамках значения/ний которого(логинов) пройдет синхронизация", required = true) @RequestParam(required = true, value = "sID_Field") String sID_Field,
            @ApiParam(value = "степ документа", required = true) @RequestParam(required = true, value = "sKey_Step") String sKey_Step,
            @ApiParam(value = "перезаписать при совпадении", required = true) @RequestParam(required = true, value = "bReClone") boolean bReClone
    ) throws Exception {

        LOG.info("snID_Process_Activiti: {}", snID_Process_Activiti);
        LOG.info("sKey_Group_Default: {}", sKey_Group_Default);
        LOG.info("sID_Field: {}", sID_Field);
        LOG.info("sKey_Step: {}", sKey_Step);
        LOG.info("bReClone: {}", bReClone);

        List<DocumentStepSubjectRight> aDocumentStepSubjectRight = documentStepService.syncDocumentSubmitersByField(snID_Process_Activiti, sKey_Group_Default, sID_Field, sKey_Step, bReClone);
        //List<DocumentStepSubjectRight> aDocumentStepSubjectRight = documentStepService.syncDocumentSubmitersByField(snID_Process_Activiti, sID_Field, sKey_Group_Default, sID_Step);
        
        LOG.info("oDocumentStepSubjectRight is {}", aDocumentStepSubjectRight);
        
        if (aDocumentStepSubjectRight != null) {
            return JSONValue.toJSONString(aDocumentStepSubjectRight);
        }

        return "DocumentStepSubjectRight is null";
    }
    

    
    
    @ApiOperation(value = "Отмена сабмиченности и подписанности документа")
    @RequestMapping(value = "/cancelDocumentSubmit", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    //@Transactional
    public @ResponseBody
    Map<String, Boolean> cancelDocumentSubmit(
            @ApiParam(value = "ИД процесс-активити", required = true) @RequestParam(required = true, value = "snID_Process_Activiti") String snID_Process_Activiti,
            @ApiParam(value = "группа", required = true) @RequestParam(required = true, value = "sKey_Group") String sKey_Group,
            @ApiParam(value = "степ документа", required = true) @RequestParam(required = true, value = "sKey_Step") String sKey_Step) throws Exception {

        LOG.info("snID_Process_Activiti={}", snID_Process_Activiti);
        LOG.info("sKey_Group={}", sKey_Group);
        LOG.info("sKey_Step={}", sKey_Step);

        Boolean bCanceled = documentStepService.cancelDocumentSubmit(snID_Process_Activiti, sKey_Step, sKey_Group);
        
        LOG.info("bCanceled={}", bCanceled);
        
        Map<String, Boolean> m=new HashMap();
        m.put("bCanceled", bCanceled);
        return m;
    }

    
    @ApiOperation(value = "Удаление подписанта документа")
    @RequestMapping(value = "/removeDocumentStepSubject", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    //@Transactional
    public @ResponseBody
    Map<String, Boolean> removeDocumentStepSubject(
            @ApiParam(value = "ИД процесс-активити", required = true) @RequestParam(required = true, value = "snID_Process_Activiti") String snID_Process_Activiti,
            @ApiParam(value = "группа", required = true) @RequestParam(required = true, value = "sKey_Group") String sKey_Group,
            @ApiParam(value = "степ документа", required = true) @RequestParam(required = true, value = "sKey_Step") String sKey_Step) throws Exception {

        LOG.info("snID_Process_Activiti={}", snID_Process_Activiti);
        LOG.info("sKey_Group={}", sKey_Group);
        LOG.info("sKey_Step={}", sKey_Step);

        Boolean bRemoved = documentStepService.removeDocumentStepSubject(snID_Process_Activiti, sKey_Step, sKey_Group);
        
        LOG.info("bRemoved={}", bRemoved);
        
        Map<String, Boolean> m=new HashMap();
        m.put("bRemoved", bRemoved);
        return m;
    }
    
    @ApiOperation(value = "Удаление подписанта документа")
    @RequestMapping(value = "/delegateDocumentStepSubject", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    //@Transactional
    public @ResponseBody
    List<DocumentStepSubjectRight> delegateDocumentStepSubject(
            @ApiParam(value = "ИД процесс-активити", required = true) @RequestParam(required = true, value = "snID_Process_Activiti") String snID_Process_Activiti,
            @ApiParam(value = "группа", required = true) @RequestParam(required = true, value = "sKey_Group") String sKey_Group,
            @ApiParam(value = "группа делегирования", required = true) @RequestParam(required = true, value = "sKey_Group_Delegate") String sKey_Group_Delegate,
            @ApiParam(value = "степ документа", required = true) @RequestParam(required = true, value = "sKey_Step") String sKey_Step
    ) throws Exception {

        LOG.info("snID_Process_Activiti={}", snID_Process_Activiti);
        LOG.info("sKey_Group={}", sKey_Group);
        LOG.info("sKey_Group_Delegate={}", sKey_Group_Delegate);
        LOG.info("sKey_Step={}", sKey_Step);

        List<DocumentStepSubjectRight> aDocumentStepSubjectRight_Current = documentStepService.delegateDocumentStepSubject(snID_Process_Activiti, sKey_Step, sKey_Group, sKey_Group_Delegate);
        
        LOG.info("aDocumentStepSubjectRight_Current={}", aDocumentStepSubjectRight_Current);
        
        return aDocumentStepSubjectRight_Current;
    }
    
//delegateDocumentStepSubject(String snID_Process_Activiti, String sKey_Step, String sKey_Group, String sKey_Group_Delegate)
        
        
    @ApiOperation(value = "Удаление степов и процесса")
    @RequestMapping(value = "/removeDocumentSteps", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody String removeDocumentSteps(
                    @ApiParam(value = "ИД процесс-активити", required = true) @RequestParam(required = true, value = "snID_Process_Activiti") String snID_Process_Activiti)
                    throws Exception {

             return deleteProccess.closeProcessInstance(snID_Process_Activiti);
    }
    

    @ApiOperation(value = "Получение списка подписанных документов без ЕЦП")
    @RequestMapping(value = "/getDocumentSubmitedUnsigned", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    // @Transactional
    public @ResponseBody List<DocumentSubmitedUnsignedVO> getDocumentSubmitedUnsigned(
                    @ApiParam(value = "Логин сотрудника", required = false) @RequestParam(required = false, value = "sLogin") String sLogin)
                    throws Exception {

            LOG.info("sLogin: ", sLogin);

            List<DocumentSubmitedUnsignedVO> aDocumentSubmitedUnsignedVO = documentStepService.getDocumentSubmitedUnsigned(sLogin);

            LOG.info("aDocumentSubmitedUnsignedVO in getDocumentSubmitedUnsigned is {}", aDocumentSubmitedUnsignedVO);

            /*if (aDocumentSubmitedUnsignedVO != null) {
                    return JSONValue.toJSONString(aDocumentSubmitedUnsignedVO);
            }

            return "aDocumentSubmitedUnsignedVO is null";*/
             return aDocumentSubmitedUnsignedVO;
    }

}
