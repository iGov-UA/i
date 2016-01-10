package org.igov.service.controller;

import io.swagger.annotations.*;
import org.igov.service.business.subject.SubjectMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.igov.activiti.bp.BpService;
import org.igov.io.GeneralConfig;
import org.igov.model.action.event.HistoryEvent_Service;
import org.igov.model.action.event.HistoryEvent_ServiceDao;
import org.igov.model.subject.message.SubjectMessage;
import org.igov.model.subject.message.SubjectMessagesDao;
import static org.igov.service.business.subject.SubjectMessageService.sMessageHead;
import org.igov.service.exception.ActivitiRestException;

//import com.google.common.base.Optional;
/**
 * @author BW
 */
@Controller
@Api(tags = {"DebugCentralController"}, description = "Дебаг и тест центральный")
//@RequestMapping(value = "/debug")
public class DebugCentralController {

    private static final Logger LOG = LoggerFactory
            .getLogger(DebugCentralController.class);

    @Autowired
    private HistoryEvent_ServiceDao historyEventServiceDao;
    @Autowired
    private SubjectMessagesDao subjectMessagesDao;
    @Autowired
    private GeneralConfig generalConfig;
    @Autowired
    private BpService bpService;

    @Autowired
    private SubjectMessageService subjectMessageService;

    @Deprecated //Нужно будет удалить после недели работы продеплоеной в прод версии (для обратной временной совместимости)
    /**
     * Сохранение сообщения оценки
     *
     * @param sID_Order Строка-ИД заявки (временно опциональный)
     * @param sID_Rate Строка-ИД Рнйтинга/оценки (число от 1 до 5)
     * @param nID_Protected Номер-ИД заявки, защищенный по алгоритму Луна,
     * опционально(для обратной совместимости)
     * @throws ActivitiRestException
     */
    @ApiOperation(value = "/messages/setMessageRate", notes = "##### DebugCentralController(SubjectMessageController) - Сообщения субьектов. Установка сообщения-оценки #####\n\n")
    @RequestMapping(value = "/messages/setMessageRate", method = RequestMethod.GET)//Rate
    public @ResponseBody
    String setMessageRate(
            @ApiParam(value = "Строка-ИД заявки (временно опциональный)", required = false) @RequestParam(value = "sID_Order", required = false) String sID_Order,
            @ApiParam(value = "Строка-ИД рейтинга/оценки (число от 1 до 5)", required = true) @RequestParam(value = "sID_Rate", required = true) String sID_Rate,
            @ApiParam(value = "Номер-ИД заявки, защищенный по алгоритму Луна, опционально(для обратной совместимости)", required = false) @RequestParam(value = "nID_Protected", required = false) Long nID_Protected,
            HttpServletResponse oResponse) throws ActivitiRestException {

        if (sID_Order == null) {
            if (nID_Protected == null) {
                LOG.error("sID_Order=null and nID_Protected=null");
            } else {
                LOG.warn("sID_Order=null and nID_Protected=" + nID_Protected);
                sID_Order = "0-" + nID_Protected;
            }
        }
        else if (!sID_Order.contains("-")) {
            LOG.warn("Incorrect parameter! {sID_Order}", sID_Order);
            throw new ActivitiRestException(404, "Incorrect parameter! {sID_Order=" + sID_Order + "}");
        }

        if ("".equals(sID_Rate.trim())) {
            LOG.warn("Parameter(s) is absent! {sID_Order}, {sID_Rate}", sID_Order, sID_Rate);
            throw new ActivitiRestException(404, "Incorrect value of sID_Rate! It isn't number.");
        }
        Integer nRate;
        try {
            nRate = Integer.valueOf(sID_Rate);
        } catch (NumberFormatException ex) {
            LOG.warn("incorrect param sID_Rate (not a number): " + sID_Rate);
            throw new ActivitiRestException(404, "Incorrect value of sID_Rate! It isn't number.");
        }
        if (nRate < 1 || nRate > 5) {
            LOG.warn("incorrect param sID_Rate (not in range[1..5]): " + sID_Rate);
            throw new ActivitiRestException(404, "Incorrect value of sID_Rate! It is too short or too long number");
        }

        String sReturn = "Ok!";

        Long nID_HistoryEvent_Service;
        Long nID_Subject;
        HistoryEvent_Service oHistoryEvent_Service;

        try {
            //LOG.info("sID_Order: " + sID_Order + ", nRate: " + nRate);
            oHistoryEvent_Service = historyEventServiceDao.getOrgerByID(sID_Order);
            if (oHistoryEvent_Service == null) {
                throw new ActivitiRestException(404, "(sID_Order: " + sID_Order + ", nRate: " + nRate + "): Record of HistoryEvent_Service, with sID_Order=" + sID_Order + " - not found!");
            }
            nID_HistoryEvent_Service = oHistoryEvent_Service.getId();
            nID_Subject = oHistoryEvent_Service.getnID_Subject();

            String sToken = null;
            Integer nRateWas = oHistoryEvent_Service.getnRate();
            if (nRateWas != null && nRateWas > 0) {
                //throw new ActivitiRestException(404, "(sID_Order: " + sID_Order + "): Record of HistoryEvent_Service, with sID_Order="+sID_Order+" - alredy has nRateWas="+nRateWas);
                sReturn = "Record of HistoryEvent_Service, with sID_Order=" + sID_Order + " - already has nRateWas=" + nRateWas;
                LOG.warn("{} (nID_HistoryEvent_Service={}, nID_Subject={})", sReturn, nID_HistoryEvent_Service, nID_Subject);
            } else {

                oHistoryEvent_Service.setnRate(nRate);
                //LOG.info(String.format("set nRate=%s in sID_Order=%s", nRate, sID_Order));
                sToken = RandomStringUtils.randomAlphanumeric(15);
                //HistoryEvent_Service oHistoryEvent_Service = historyEventServiceDao.getOrgerByID(sID_Order);
                oHistoryEvent_Service.setsToken(sToken);

                LOG.info("save HistoryEvent_Service... (nID_HistoryEvent_Service={}, nID_Subject={}, sID_Order={}, nRate={})", nID_HistoryEvent_Service, nID_Subject, sID_Order, nRate);
                historyEventServiceDao.saveOrUpdate(oHistoryEvent_Service);

                Long nID_SubjectMessageType = 0l;
                SubjectMessage oSubjectMessage_Rate = subjectMessageService.createSubjectMessage(
                                sMessageHead(nID_SubjectMessageType, sID_Order),
                                "Оцінка " + sID_Rate + " (по шкалі від 2 до 5)", nID_Subject, "", "", "sID_Rate=" + sID_Rate, nID_SubjectMessageType);
                if (nID_HistoryEvent_Service != null) {
                    oSubjectMessage_Rate.setnID_HistoryEvent_Service(nID_HistoryEvent_Service);
                }
                subjectMessagesDao.setMessage(oSubjectMessage_Rate);
            }

            //сохранения сообщения с рейтингом, а на ррегиональном сервере, т.к. именно там хранится экземпляр БП.
            if (oHistoryEvent_Service.getnID_Proccess_Feedback() != null) {//issue 1006
                String snID_Process = "" + oHistoryEvent_Service.getnID_Proccess_Feedback();
                Integer nID_Server = oHistoryEvent_Service.getnID_Server();
                LOG.info("set rate={} to the nID_Proccess_Feedback={}", nRate, snID_Process);
                List<String> aTaskIds = bpService.getProcessTasks(nID_Server, snID_Process);
                LOG.info("Found '{}' tasks by nID_Proccess_Feedback...", aTaskIds.size());
                if (aTaskIds.size() > 0) {//when process is not complete
                    bpService.setVariableToProcessInstance(nID_Server, snID_Process, "nID_Rate", nRate);
                    LOG.info("process is not complete -- change rate in it!");
                    for (String sTaskId : aTaskIds) {
                        bpService.setVariableToActivitiTask(nID_Server, sTaskId, "nID_Rate", nRate);
                    }
                }
            }
            String sURL_Redirect = generalConfig.sHostCentral() + "/feedback?sID_Order=" + sID_Order + "&sSecret=" + sToken;
            LOG.info("Redirecting to URL:" + sURL_Redirect);
            oResponse.sendRedirect(sURL_Redirect);

        } catch (ActivitiRestException oActivitiRestException) {
            LOG.error("FAIL: {}", oActivitiRestException.getMessage());
            throw oActivitiRestException;
        } catch (Exception e) {
            LOG.error("FAIL:", e);
            throw new ActivitiRestException(404, "[setMessageRate](sID_Order: " + sID_Order + ", nRate: " + nRate + "): Unknown exception: " + e.getMessage());
        }
        return sReturn;//"Ok!";
    }

}
