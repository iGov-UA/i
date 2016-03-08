package org.igov.service.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Attachment;
import org.apache.commons.mail.ByteArrayDataSource;
import org.apache.commons.mail.EmailException;
import org.igov.io.mail.Mail;
import org.igov.service.business.action.task.core.ActionTaskService;
import org.igov.service.exception.CRCInvalidException;
import org.igov.service.exception.CommonServiceException;
import org.igov.service.exception.RecordNotFoundException;
import org.igov.service.exception.TaskAlreadyUnboundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author BW
 */

@Controller
@Api(tags = { "DebugCommonController - Дебаг и тест общий" })
public class DebugCommonController {

    private static final Logger LOG = LoggerFactory
            .getLogger(DebugCommonController.class);

    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private Mail oMail;

    @Autowired
    private ActionTaskService oActionTaskService;

    @ApiOperation(value = "/test/action/task/delete-processTest", notes = "#####  DebugCommonController: описания нет\n")
    @RequestMapping(value = "/test/action/task/delete-processTest", method = RequestMethod.GET)
    public
    @ResponseBody
    void deleteProcessTest(@RequestParam(value = "sProcessInstanceID") String processInstanceID,
            @RequestParam(value = "sLogin", required = false) String sLogin,
            @RequestParam(value = "sReason", required = false) String sReason
    ) throws Exception {
        runtimeService.deleteProcessInstance(processInstanceID, sReason);
    }

    @ApiOperation(value = "/test/sendAttachmentsByMail", notes = "#####  DebugCommonController: описания нет\n")
    @RequestMapping(value = "/test/sendAttachmentsByMail", method = RequestMethod.GET)
    @Transactional
    public void sendAttachmentsByMail(
            @RequestParam(value = "sMailTo", required = false) String sMailTo,
            @RequestParam(value = "nID_Task", required = false) String snID_Task,
            @RequestParam(value = "sBody", required = false) String sBody,
            @RequestParam(value = "bHTML", required = false) boolean bHTML,
            @RequestParam(value = "naID_Attachment", required = false) String snaID_Attachment,
            @RequestParam(value = "bUnisender", required = false) Boolean bUnisender)
            throws IOException, MessagingException, EmailException {

        oMail._To("bvv4ik@gmail.com");
        oMail._Body(sBody == null ? "<a href=\"http:\\\\google.com\">Google</a> It's test Проверка ! ��� ��������!"
                : sBody);

        LOG.info("(oMail.getHead()={})", oMail.getHead());
        LOG.info("(oMail.getBody()={})", oMail.getBody());
        LOG.info("(oMail.getAuthUser()={})", oMail.getAuthUser());
        LOG.info("(oMail.getAuthPassword()={})", oMail.getAuthPassword());
        LOG.info("(oMail.getFrom()={})", oMail.getFrom());
        LOG.info("(oMail.getTo()={})", oMail.getTo());
        LOG.info("(oMail.getHost()={})", oMail.getHost());
        LOG.info("(oMail.getPort()={})", oMail.getPort());

        if (snaID_Attachment != null) {
            String[] ansID_Attachment = snaID_Attachment.split(",");
            for (String snID_Attachment : ansID_Attachment) {
                Attachment oAttachment = taskService
                        .getAttachment(snID_Attachment);
                String sFileName = oAttachment.getName();
                String sFileExt = oAttachment.getType().split(";")[0];
                String sDescription = oAttachment.getDescription();
                LOG.info("(oAttachment.getId()={}, sFileName={}, sFileExt={}, sDescription={}",
                        oAttachment.getId(), sFileName, sFileExt, sDescription);
                InputStream oInputStream = taskService
                        .getAttachmentContent(oAttachment.getId());
                DataSource oDataSource = new ByteArrayDataSource(oInputStream,
                        sFileExt);

                oMail._Attach(oDataSource, sFileName + "." + sFileExt,
                        sDescription);
            }
        }

        if (bUnisender != null && bUnisender) {
            oMail.sendWithUniSender();
        } else {
            oMail.send();
        }
    }

    @Deprecated
    //Нужно будет удалить после недели работы продеплоеной в прод версии (для обратной временной совместимости)
    @ApiOperation(value = "/rest/tasks/cancelTask", notes = "#####  DebugCommonController:\n")
    @RequestMapping(value = "/rest/tasks/cancelTask", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
    public
    @ResponseBody
    ResponseEntity<String> cancelTask(
            @ApiParam(value = "", required = true) @RequestParam(value = "nID_Protected") Long nID_Protected,
            @ApiParam(value = "", required = false) @RequestParam(value = "sInfo", required = false) String sInfo)
            throws CommonServiceException, TaskAlreadyUnboundException {

        String sMessage = "Ваша заявка відмінена. Ви можете подати нову на Порталі державних послуг iGov.org.ua.<\n<br>"
                + "З повагою, команда порталу  iGov.org.ua";

        try {
            oActionTaskService.cancelTasksInternal(nID_Protected, sInfo);
            return new ResponseEntity<String>(sMessage, HttpStatus.OK);
        } catch (CRCInvalidException | RecordNotFoundException e) {
            CommonServiceException newErr = new CommonServiceException(
                    "BUSINESS_ERR", e.getMessage(), e);
            newErr.setHttpStatus(HttpStatus.FORBIDDEN);
            LOG.warn("Error: {}", e.getMessage());
            LOG.trace("FAIL:", e);
            sMessage = "Вибачте, виникла помилка при виконанні операції. Спробуйте ще раз, будь ласка";

            return new ResponseEntity<String>(sMessage, HttpStatus.FORBIDDEN);
        }

    }

    @ApiOperation(value = "/test/action/getInfo", notes = "#####  DebugCommonController: \n")
    @RequestMapping(value = "/test/action/getInfo", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
    public
    @ResponseBody
    String getInfo(@ApiParam(value = "", required = false)
    @RequestParam(value = "sID_TestType", required = false) String sID_TestType
    ) {

        return "successfull";

    }

}
