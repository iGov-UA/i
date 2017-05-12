package org.igov.service.business.action.task.systemtask.export;

import com.google.common.io.Files;
import java.io.File;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.activiti.engine.HistoryService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.history.HistoricTaskInstance;
import org.igov.io.fs.FileSystemData;
import org.igov.service.business.action.task.systemtask.mail.Abstract_MailTaskCustom;
import org.igov.service.business.export.IC_Service;
import org.igov.service.business.subject.SubjectService;
import static org.igov.service.business.util.Date.getDateDiff;
import static org.igov.util.Tool.parseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author askosyr
 *
 */
@Component("Transfer_DocumentVacation")
public class Transfer_DocumentVacation extends Abstract_MailTaskCustom implements JavaDelegate {

    private final static Logger LOG = LoggerFactory.getLogger(Transfer_DocumentVacation.class);

    @Autowired
    IC_Service o1C_Service;

    private Expression sID_Pattern;

    private Expression soData;

    private final String SYMBOL = "%";
    
    @Autowired
    private HistoryService oHistoryService;

    @Autowired
    private SubjectService oSubjectService;

    //http://koatuu.test.igov.org.ua/test1c/odata/standard.odata/Document_%D0%9E%D1%82%D0%BF%D1%83%D1%81%D0%BA%D0%B0%D0%9E%D1%80%D0%B3%D0%B0%D0%BD%D0%B8%D0%B7%D0%B0%D1%86%D0%B8%D0%B9
    @Override
    public void execute(DelegateExecution execution) throws Exception {

        String sID_Pattern_Value = this.sID_Pattern.getExpressionText();
        String soData_Value = this.soData.getExpressionText();
        LOG.info("soData_Value before: " + soData_Value);
        String soData_Value_Result = replaceTags(soData_Value, execution);
        LOG.info("soData_Value after: " + soData_Value_Result);
        Map<String, Object> data = parseData(soData_Value_Result);
        LOG.info("data: " + data);

        Date oDateVacationBegin = (Date) execution.getVariable("sDateVacationBegin");
        Date oDateVacationEnd = (Date) execution.getVariable("sDateVacationEnd");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat sdf_short = new SimpleDateFormat("dd.MM.yyyy");
        String sDate = sdf.format(new Date());
        String sCountDay = String.valueOf(getDateDiff(oDateVacationBegin, oDateVacationEnd));
        String sKeyVacationer = getLoginSubjectAccountByLoginIgovAccount(execution.getProcessInstanceId(), "1C");
        data.put("sDate", sDate);
        data.put("sCountDay", sCountDay);
        data.put("sKeyVacationer", sKeyVacationer);
        data.put("sDateVacationBegin", sdf.format(oDateVacationBegin));
        data.put("sDateVacationEnd", sdf.format(oDateVacationEnd));
        LOG.info("Transfer_DocumentVacation data: " + data);

        String sDateVacationBegin_Email = sdf_short.format(oDateVacationBegin);
        String sDateVacationEnd_Email = sdf_short.format(oDateVacationEnd);
        execution.setVariable("sDateVacationBegin_Email", sDateVacationBegin_Email);
        execution.setVariable("sDateVacationEnd_Email", sDateVacationEnd_Email);

        String filePath = FileSystemData.SUB_PATH_XML + "agroholding/";
        File oFile = FileSystemData.getFile(filePath, sID_Pattern_Value + ".xml");
        String documentVacation = Files.toString(oFile, Charset.defaultCharset());
        LOG.debug("Transfer_DocumentVacation documentVacation before: " + documentVacation);

        for (Entry<String, Object> entry : data.entrySet()) {
            documentVacation = documentVacation.replaceAll(SYMBOL + entry.getKey(), String.valueOf(entry.getValue()));
        }
        LOG.info("Transfer_DocumentVacation documentVacation after: " + documentVacation);

        String result = o1C_Service.transferDocument(documentVacation, "/Document_ОтпускаОрганизаций");
        LOG.info("Transfer_DocumentVacation result: " + result);
    }

    private String getLoginSubjectAccountByLoginIgovAccount(String sLoginIgovAccount, String sID_SubjectAccountType) {
        String result = null;
        try {
            List<HistoricTaskInstance> aHistoricTask = oHistoryService.createHistoricTaskInstanceQuery()
                    .processInstanceId(sLoginIgovAccount).orderByHistoricTaskInstanceStartTime().asc().list();
            LOG.info("aHistoricTask: " + aHistoricTask);
            if (aHistoricTask.size() > 0) {
                LOG.info("aHistoricTask.size: " + aHistoricTask.size());
                HistoricTaskInstance historicTaskInstance = aHistoricTask.get(0);
                LOG.info("historicTaskInstance.getName(): " + historicTaskInstance.getName());
                String assigneeUser = historicTaskInstance.getAssignee();
                LOG.info("assigneeUser: " + assigneeUser);
                return oSubjectService.getLoginSubjectAccountByLoginIgovAccount(assigneeUser, sID_SubjectAccountType);
            }
        } catch (Exception ex) {
            LOG.error("getLoginSubjectAccountByLoginIgovAccount: ", ex);
        }
        return result;
    }
}
