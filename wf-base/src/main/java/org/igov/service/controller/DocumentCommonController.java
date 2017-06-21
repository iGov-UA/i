package org.igov.service.controller;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

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
import org.igov.model.document.DocumentStep;
import org.igov.model.document.DocumentStepSubjectRight;
import org.igov.service.business.action.task.systemtask.DeleteProccess;
import org.json.simple.JSONValue;

@Controller
@Api(tags = {"DocumentCommonController -- Проверки наложености ЭЦП по строкам-подписантам"})
@RequestMapping(value = "/common/document")

public class DocumentCommonController {

    @Autowired
    private DocumentStepService documentStepService;

    @Autowired
    private DeleteProccess deleteProccess;

    @Autowired
    private DocumentStepService oDocumentStepService;

    private static final Logger LOG = LoggerFactory.getLogger(DocumentCommonController.class);

    @ApiOperation(value = "проверка подписано ли ЕЦП")
    @RequestMapping(value = "/isDocumentStepSubmitedAll", method = RequestMethod.GET)
    @Transactional
    public @ResponseBody
    Map<String, Object> isDocumentStepSubmitedAll(
            @ApiParam(value = "ИД процесс-активити", required = true) @RequestParam(required = true, value = "nID_Process") String nID_Process,
            @ApiParam(value = "Логин подписанта", required = true) @RequestParam(required = true, value = "sLogin") String sLogin,
            @ApiParam(value = "Ключ шага документа", required = true) @RequestParam(required = true, value = "sKey_Step") String sKey_Step,
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
    @RequestMapping(value = "/syncDocumentSubmitedsByField", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    //@Transactional
    public @ResponseBody
    String syncDocumentSubmitedsByField(
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

        List<DocumentStepSubjectRight> aDocumentStepSubjectRight = documentStepService.syncDocumentSubmitedsByField(snID_Process_Activiti, sKey_Group_Default, sID_Field, sKey_Step, bReClone);
        //List<DocumentStepSubjectRight> aDocumentStepSubjectRight = documentStepService.syncDocumentSubmitedsByField(snID_Process_Activiti, sID_Field, sKey_Group_Default, sID_Step);

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

        Map<String, Boolean> m = new HashMap();
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

        Map<String, Boolean> m = new HashMap();
        m.put("bRemoved", bRemoved);
        return m;
    }

    @ApiOperation(value = "Добавить согласующего")
    @RequestMapping(value = "/addVisor", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody
    List<DocumentStepSubjectRight> addAVisor(
            @ApiParam(value = "ИД процесс-активити", required = true) @RequestParam(required = true, value = "snID_Process_Activiti") String snID_Process_Activiti,
            @ApiParam(value = "группа", required = true) @RequestParam(required = true, value = "sKey_Group") String sKey_Group,
            @ApiParam(value = "группа делегирования", required = true) @RequestParam(required = true, value = "sKey_Group_Delegate") String sKey_Group_Delegate,
            @ApiParam(value = "степ документа", required = true) @RequestParam(required = true, value = "sKey_Step") String sKey_Step
    ) throws Exception {
        LOG.info("addAVisor started");
        LOG.info("snID_Process_Activiti={}", snID_Process_Activiti);
        LOG.info("sKey_Group={}", sKey_Group);
        LOG.info("sKey_Group_Delegate={}", sKey_Group_Delegate);
        LOG.info("sKey_Step={}", sKey_Step);

        List<DocumentStepSubjectRight> aDocumentStepSubjectRight_Current = documentStepService
                .delegateDocumentStepSubject(snID_Process_Activiti, sKey_Step, sKey_Group, sKey_Group_Delegate, "AddVisor");

        LOG.info("aDocumentStepSubjectRight_Current={}", aDocumentStepSubjectRight_Current);

        return aDocumentStepSubjectRight_Current;
    }

    @ApiOperation(value = "Добавить согласующего")
    @RequestMapping(value = "/addAcceptor", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody
    List<DocumentStepSubjectRight> addAcceptor(
            @ApiParam(value = "ИД процесс-активити", required = true) @RequestParam(required = true, value = "snID_Process_Activiti") String snID_Process_Activiti,
            @ApiParam(value = "группа", required = true) @RequestParam(required = true, value = "sKey_Group") String sKey_Group,
            @ApiParam(value = "группа делегирования", required = true) @RequestParam(required = true, value = "sKey_Group_Delegate") String sKey_Group_Delegate,
            @ApiParam(value = "степ документа", required = true) @RequestParam(required = true, value = "sKey_Step") String sKey_Step
    ) throws Exception {
        LOG.info("addAcceptor started");
        LOG.info("snID_Process_Activiti={}", snID_Process_Activiti);
        LOG.info("sKey_Group={}", sKey_Group);
        LOG.info("sKey_Group_Delegate={}", sKey_Group_Delegate);
        LOG.info("sKey_Step={}", sKey_Step);

        List<DocumentStepSubjectRight> aDocumentStepSubjectRight_Current = documentStepService
                .delegateDocumentStepSubject(snID_Process_Activiti, sKey_Step, sKey_Group, sKey_Group_Delegate, "AddAcceptor");

        LOG.info("aDocumentStepSubjectRight_Current={}", aDocumentStepSubjectRight_Current);

        return aDocumentStepSubjectRight_Current;
    }

    @ApiOperation(value = "Делегировать подписанта документа")
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

        List<DocumentStepSubjectRight> aDocumentStepSubjectRight_Current = documentStepService
                .delegateDocumentStepSubject(snID_Process_Activiti, sKey_Step, sKey_Group, sKey_Group_Delegate, "delegate");

        LOG.info("aDocumentStepSubjectRight_Current={}", aDocumentStepSubjectRight_Current);

        return aDocumentStepSubjectRight_Current;
    }

//delegateDocumentStepSubject(String snID_Process_Activiti, String sKey_Step, String sKey_Group, String sKey_Group_Delegate)
    @ApiOperation(value = "Удаление степов и процесса")
    @RequestMapping(value = "/removeDocumentSteps", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody
    String removeDocumentSteps(
            @ApiParam(value = "ИД процесс-активити", required = true) @RequestParam(required = true, value = "snID_Process_Activiti") String snID_Process_Activiti)
            throws Exception {

        return deleteProccess.closeProcessInstance(snID_Process_Activiti);
    }

    @ApiOperation(value = "Получение списка подписанных документов без ЕЦП")
    @RequestMapping(value = "/getDocumentSubmitedUnsigned", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    // @Transactional
    public @ResponseBody
    List<DocumentSubmitedUnsignedVO> getDocumentSubmitedUnsigned(
            @ApiParam(value = "Логин сотрудника", required = false) @RequestParam(required = false, value = "sLogin") String sLogin)
            throws Exception {

        LOG.info("sLogin: ", sLogin);

        List<DocumentSubmitedUnsignedVO> aDocumentSubmitedUnsignedVO = documentStepService.getDocumentSubmitedUnsigned(sLogin);

        //LOG.info("aDocumentSubmitedUnsignedVO in getDocumentSubmitedUnsigned is {}", aDocumentSubmitedUnsignedVO);

        /*if (aDocumentSubmitedUnsignedVO != null) {
                    return JSONValue.toJSONString(aDocumentSubmitedUnsignedVO);
            }

            return "aDocumentSubmitedUnsignedVO is null";*/
        return aDocumentSubmitedUnsignedVO;
    }

    @ApiOperation(value = "/getDocumentStepRights", notes = "##### Получение списка прав у логина по документу#####\n\n")
    @RequestMapping(value = "/getDocumentStepRights", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> getDocumentStepRights(@ApiParam(value = "sLogin", required = true) @RequestParam(value = "sLogin", required = true) String sLogin, //String
            @ApiParam(value = "nID_Process", required = true) @RequestParam(value = "nID_Process", required = true) String nID_Process) throws Exception {

        long startTime = System.nanoTime();
        Map<String, Object> res = oDocumentStepService.getDocumentStepRights(sLogin, nID_Process + "");
        long stopTime = System.nanoTime();
        LOG.info("getDocumentStepRights total time execution is: " + String.format("%,12d", (stopTime - startTime)));
        return res;
    }

    @ApiOperation(value = "/getDocumentStepLogins", notes = "##### Получение списка прав у логина по документу#####\n\n")
    @RequestMapping(value = "/getDocumentStepLogins", method = RequestMethod.GET)
    public @ResponseBody
    List<Map<String, Object>> getDocumentStepLogins(@ApiParam(value = "nID_Process", required = true)
            @RequestParam(value = "nID_Process", required = true) String nID_Process) throws Exception {//String
        return oDocumentStepService.getDocumentStepLogins(String.valueOf(nID_Process));
    }

    @ApiOperation(value = "/getDocumentStep", notes = "##### Получение степ по документу#####\n\n")
    @RequestMapping(value = "/getDocumentStep", method = RequestMethod.GET)
    public @ResponseBody
    DocumentStep getDocumentStep(
            @ApiParam(value = "snID_Process_Activiti", required = true)
            @RequestParam(value = "snID_Process_Activiti", required = true) String snID_Process_Activiti,
            @ApiParam(value = "sKey_Step", required = true)
            @RequestParam(value = "sKey_Step", required = true) String sKey_Step
    ) throws Exception {
        return oDocumentStepService.getDocumentStep(snID_Process_Activiti, sKey_Step);
    }

}
