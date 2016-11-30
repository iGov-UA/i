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
import java.util.List;

import org.igov.io.GeneralConfig;
import org.igov.model.flow.FlowServiceDataDao;
import org.igov.model.flow.Flow_ServiceData;
import org.igov.service.business.finance.PaymentProcessorService;
import org.igov.service.business.flow.FlowService;
import org.igov.service.business.flow.slot.Days;
import org.igov.service.business.flow.slot.Day;
import org.igov.service.business.flow.slot.FlowSlotVO;
import org.joda.time.DateTime;

/**
 * @author BW
 */
@Controller
@Api(tags = {"DebugCommonController - Дебаг и тест общий"})
public class DebugCommonController {

    private static final Logger LOG = LoggerFactory
            .getLogger(DebugCommonController.class);

    public static final int DAYS_IN_MONTH = 30;
    public static final int WORK_DAYS_NEEDED = 20;
    public static final int DAYS_IN_HALF_YEAR = 180;
    private static final String SUFFIX_AUTO = "auto";

    @Autowired
    GeneralConfig generalConfig;
    
    @Autowired
    private FlowService oFlowService;

    @Autowired
    private FlowServiceDataDao flowServiceDataDao;

    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private Mail oMail;

    @Autowired
    private ActionTaskService oActionTaskService;
    
    @Autowired
    private PaymentProcessorService paymentProcessorService;

    @ApiOperation(value = "/test/action/task/delete-processTest", notes = "#####  DebugCommonController: описания нет\n")
    @RequestMapping(value = "/test/action/task/delete-processTest", method = RequestMethod.GET)
    public @ResponseBody
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

        //oMail._To("bvv4ik@gmail.com");
        
        oMail._To(sMailTo!=null&&generalConfig.isSelfTest()?sMailTo:"bvv4ik@gmail.com");
        oMail._Body(sBody == null ? "<a href=\"http:\\\\google.com\">Google</a> It's test Проверка!" : sBody);

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
    public @ResponseBody
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
    public @ResponseBody
    String getInfo(@ApiParam(value = "", required = false)
            @RequestParam(value = "sID_TestType", required = false) String sID_TestType
    ) {

        return "successfull";

    }

    //maxline: тестирование работы получения свободных слотов getFlowSlots findFlowSlotsByFlow и в случае отсутствия 
    //генерация новых слотов buildFlowSlots
    @ApiOperation(value = "/test/action/testSheduleBuilderFlowSlots", notes = "#####  DebugCommonController: описания нет\n")
    @RequestMapping(value = "/test/action/testSheduleBuilderFlowSlots", method = RequestMethod.GET)
    public @ResponseBody
    void testSheduleBuilderFlowSlots(
            @RequestParam(value = "nID_Flow_ServiceData", required = false, defaultValue = "12") Long nID_Flow_ServiceData,  //12L - _test_queue_cancel
            @RequestParam(value = "nID_ServiceData", required = false) Long nID_ServiceData,
            @RequestParam(value = "sDateStart", required = false) String sDateStart,
            @RequestParam(value = "sDateStop", required = false) String sDateStop,
            @RequestParam(value = "bAll", required = false) boolean bAll,
            @RequestParam(value = "nDays", required = false, defaultValue = "10") int nDays,
            @RequestParam(value = "sOperation", required = false) String sOperation) throws Exception {
        LOG.info("/test/action/testSheduleBuilderFlowSlots  - invoked");

        if (sOperation == null) {
            sOperation = "";
        }
        DateTime oDateStart;
        DateTime oDateEnd;
        Flow_ServiceData flow;
        Long nID_SubjectOrganDepartment;

        LOG.info(" sDateStart = {}", sDateStart);
        LOG.info(" sDateStop = {}", sDateStop);
        LOG.info(" sOperation = {}", sOperation);

        switch (sOperation) {
            case "auto":
                List<Flow_ServiceData> aFlowServiceData = flowServiceDataDao.findAll();
                for(Flow_ServiceData item:aFlowServiceData){
                    if (item.getsID_BP().endsWith(SUFFIX_AUTO)){
                        LOG.info(" Flow_ServiceData ID {}, sID_BP = {} ", item.getId(), item.getsID_BP());
                    }
                }
                break;
            case "checkAndBuild":
                oDateStart = DateTime.now().withTimeAtStartOfDay();
                LOG.info(" oDateStart = {}", oDateStart);

                //Maxline: TODO добавить исключения
                //nID_Flow_ServiceData = 12L; //_test_queue_cancel
                flow = flowServiceDataDao.findByIdExpected(nID_Flow_ServiceData);
                nID_ServiceData = flow.getnID_ServiceData();   //nID_ServiceData = 358  _test_queue_cancel, nID_ServiceData = 63L Видача/заміна паспорта громадянина для виїзду за кордон
                nID_SubjectOrganDepartment = flow.getnID_SubjectOrganDepartment();
                LOG.info(" nID_Flow_ServiceData = {}, nID_ServiceData = {}, nID_SubjectOrganDepartment = {}", 
                        nID_Flow_ServiceData, nID_ServiceData, nID_SubjectOrganDepartment);

                int nStartDay = 0;
                DateTime dateStart = oDateStart.plusDays(0);
                DateTime dateEnd;

                while (!isEnoughFreeDays(nID_ServiceData, nID_SubjectOrganDepartment, oDateStart) 
                        && nStartDay < DAYS_IN_HALF_YEAR) {
                    dateStart = oDateStart.plusDays(nStartDay);
                    dateEnd = oDateStart.plusDays(nStartDay + DAYS_IN_MONTH);
                    LOG.info(" dateStart = {}, dateEnd = {}", dateStart, dateEnd);

                    List<FlowSlotVO> resFlowSlotVO = oFlowService.buildFlowSlots(nID_Flow_ServiceData, 
                            dateStart, dateEnd); // строит четко на месяц вперед независимо от рабочих или нерабочих дней
                    LOG.info(" resFlowSlotVO.size() = {}", resFlowSlotVO.size());

                    nStartDay += DAYS_IN_MONTH;
                }

                boolean bEnoughFreeDays = nStartDay < DAYS_IN_HALF_YEAR;
                break;
            case "check":
                oDateStart = DateTime.now().withTimeAtStartOfDay();
                LOG.info(" oDateStart = {}", oDateStart);

                flow = flowServiceDataDao.findByIdExpected(nID_Flow_ServiceData);
                nID_ServiceData = flow.getnID_ServiceData();   //nID_ServiceData = 358  _test_queue_cancel, nID_ServiceData = 63L Видача/заміна паспорта громадянина для виїзду за кордон
                nID_SubjectOrganDepartment = flow.getnID_SubjectOrganDepartment();
                LOG.info(" nID_Flow_ServiceData = {}, nID_ServiceData = {}, nID_SubjectOrganDepartment = {}", 
                        nID_Flow_ServiceData, nID_ServiceData, nID_SubjectOrganDepartment);

                isEnoughFreeDays(nID_ServiceData, nID_SubjectOrganDepartment, oDateStart);
                break;
            case "build":
                oDateStart = getoDateStart(sDateStart);
                oDateEnd = oDateStart.plusDays(nDays);
                LOG.info(" oDateEnd = {}", oDateEnd);

                nID_Flow_ServiceData = (nID_Flow_ServiceData == null) ? 12L : nID_Flow_ServiceData; //_test_queue_cancel

                List<FlowSlotVO> resFlowSlotVO = oFlowService.buildFlowSlots(nID_Flow_ServiceData, oDateStart, oDateEnd);
                LOG.info(" resFlowSlotVO.size() = {}", resFlowSlotVO.size());
                break;
            case "clear":
                oDateStart = getoDateStart(sDateStart);
                oDateEnd = oDateStart.plusDays(nDays);
                LOG.info(" oDateEnd = {}", oDateEnd);

                boolean bWithTickets = false;
                nID_Flow_ServiceData = (nID_Flow_ServiceData == null) ? 12L : nID_Flow_ServiceData; //_test_queue_cancel

                oFlowService.clearFlowSlots(nID_Flow_ServiceData, oDateStart, oDateEnd, bWithTickets);
                break;
        }

        LOG.info(" /test/action/testSheduleBuilderFlowSlots  - exit4");
        //runtimeService.deleteProcessInstance(processInstanceID, sReason);
    }

    private DateTime getoDateStart(String sDateStart) {
        DateTime oDateStart;
        if (sDateStart == null || sDateStart.equals("")) {  //sDateStart = "2016-05-12 00:00:00.000";
            oDateStart = DateTime.now().withTimeAtStartOfDay();
        } else {
            oDateStart = oFlowService.parseJsonDateTimeSerializer(sDateStart);
        }
        LOG.info(" oDateStart = {}", oDateStart);
        return oDateStart;
    }

    private boolean isEnoughFreeDays(Long nID_ServiceData, Long nID_SubjectOrganDepartment, DateTime oDateStart) {
        boolean bAll = false; //Получаем только свободные дни
        int nFreeWorkDaysFact;
        Long nID_Service = null; //176L;
        String sID_BP = null;

        DateTime oDateEnd = oDateStart.plusDays(DAYS_IN_HALF_YEAR);
        LOG.info(" oDateEnd = {}", oDateEnd);

        Days res = oFlowService.getFlowSlots(nID_Service, nID_ServiceData, sID_BP, nID_SubjectOrganDepartment,
                oDateStart, oDateEnd, bAll, WORK_DAYS_NEEDED, 1); //WORK_DAYS_NEEDED
        LOG.info(" Days = {}", res);

        nFreeWorkDaysFact = res.getaDay().size();
        LOG.info(" nFreeWorkDaysFact = {}, WORK_DAYS_NEEDED = {}", nFreeWorkDaysFact, WORK_DAYS_NEEDED);
        for (Day day : res.getaDay()) {
            LOG.info(" Day = {}, isbHasFree = {}", day.getsDate(), day.isbHasFree());
        }
        return nFreeWorkDaysFact >= WORK_DAYS_NEEDED;
    }
    
    @ApiOperation(value = "/test/action/loadPayments", notes = "#####  DebugCommonController: \n")
    @RequestMapping(value = "/test/action/loadPayments", method = RequestMethod.GET)
    public @ResponseBody
    String loadPayments() {
    	paymentProcessorService.loadPaymentInformation();
        return "successful";

    }

}
