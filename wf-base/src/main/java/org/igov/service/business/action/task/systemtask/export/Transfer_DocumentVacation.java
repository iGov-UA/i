package org.igov.service.business.action.task.systemtask.export;

import com.google.common.base.Optional;
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
import org.igov.model.subject.SubjectAccount;
import org.igov.model.subject.SubjectAccountDao;
import org.igov.service.business.action.task.listener.doc.CreateDocument_UkrDoc;
import org.igov.service.business.action.task.systemtask.mail.Abstract_MailTaskCustom;
import org.igov.service.business.export.AgroholdingService;
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
public class Transfer_DocumentVacation extends Abstract_MailTaskCustom  implements JavaDelegate {

    private final static Logger LOG = LoggerFactory.getLogger(Transfer_DocumentVacation.class);

    @Autowired
    AgroholdingService agroholdingService;

    @Autowired
    private HistoryService oHistoryService;

    @Autowired
    private SubjectAccountDao subjectAccountDao;

    private Expression sID_Pattern;

    private Expression soData;

    private final String SYMBOL = "%";

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        
        String sID_Pattern_Value = this.sID_Pattern.getExpressionText();
        String soData_Value = this.soData.getExpressionText();
        LOG.info("soData_Value before: " + soData_Value);
        String soData_Value_Result = replaceTags(soData_Value, execution);
        LOG.info("soData_Value after: " + soData_Value_Result);
        Map<String, Object> data = parseData(soData_Value_Result);
        LOG.info("data: " + data);
        
        Object sDateVacationBegin = execution.getVariable("sDateVacationBegin");
        LOG.info("sDateVacationBegin: " + sDateVacationBegin.getClass() + " sDateVacationBegin: " + sDateVacationBegin);
        
        
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat sdf_short = new SimpleDateFormat("yyyy-MM-dd");
        String sDate = sdf.format(new Date());
        Date oDateVacationBegin = sdf_short.parse((String) data.get("sDateVacationBegin"));
        Date oDateVacationEnd = sdf_short.parse((String) data.get("sDateVacationEnd"));
        String sCountDay = String.valueOf(getDateDiff(oDateVacationBegin, oDateVacationEnd));
        String sKeyVacationer = getLoginSubjectAccountByLoginIgovAccount(execution.getProcessInstanceId());
        data.put("sDate", sDate);
        data.put("sCountDay", sCountDay);
        data.put("sKeyVacationer", sKeyVacationer);
        data.put("sDateVacationBegin", sdf.format(oDateVacationBegin));
        data.put("sDateVacationEnd", sdf.format(oDateVacationEnd));
        LOG.info("Transfer_DocumentVacation data: " + data);
        
        String sDateVacationBegin_Email = (String) data.get("sDateVacationBegin");
        String sDateVacationEnd_Email = (String) data.get("sDateVacationEnd");
        execution.setVariable("sDateVacationBegin_Email", sDateVacationBegin_Email);
        execution.setVariable("sDateVacationEnd_Email", sDateVacationEnd_Email);

        String filePath = FileSystemData.SUB_PATH_XML + "agroholding/";
        File oFile = FileSystemData.getFile(filePath, sID_Pattern_Value + ".xml");
        String documentVacation = Files.toString(oFile, Charset.defaultCharset());
        LOG.info("Transfer_DocumentVacation documentVacation before: " + documentVacation);

        for (Entry<String, Object> entry : data.entrySet()) {
            documentVacation = documentVacation.replaceAll(SYMBOL + entry.getKey(), String.valueOf(entry.getValue()));
        }
        LOG.info("Transfer_DocumentVacation documentVacation after: " + documentVacation);

        String result = agroholdingService.transferDocumentVacation(documentVacation);
        LOG.info("Transfer_DocumentVacation result: " + result);
    }

    private Integer getDateDiff(java.util.Date date1, java.util.Date date2) {
        return Math.round((date2.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24));
    }

    private String getLoginSubjectAccountByLoginIgovAccount(String sLoginIgovAccount) {
        String result = "a807e909-abfb-11dc-aa58-00112f3000a2";
        try {
            List<HistoricTaskInstance> aHistoricTask = oHistoryService.createHistoricTaskInstanceQuery()
                    .processInstanceId(sLoginIgovAccount).orderByHistoricTaskInstanceStartTime().asc().list();
            if (aHistoricTask.size() > 0) {
                HistoricTaskInstance historicTaskInstance = aHistoricTask.get(0);
                String assigneeUser = historicTaskInstance.getAssignee();
                if (assigneeUser != null) {
                    Optional<SubjectAccount> subjectAccount = subjectAccountDao.findBy("sLogin", assigneeUser);
                    if (subjectAccount.isPresent()) {
                        Long nID_Subject = subjectAccount.get().getnID_Subject();
                        if (nID_Subject != null) {
                            List<SubjectAccount> aSubjectAccount = subjectAccountDao.findAllBy("nID_Subject", nID_Subject);
                            if (aSubjectAccount.size() > 0) {
                                for (SubjectAccount oSubjectAccount : aSubjectAccount) {
                                    if (oSubjectAccount.getSubjectAccountType().getId() == 3) {
                                        result = oSubjectAccount.getsLogin();
                                    } else {
                                        LOG.error("Can't find 1C account");
                                    }
                                }
                            } else {
                                LOG.error("Can't find SubjectAccount by Subject");
                            }
                        } else {
                            LOG.error("Subject is null ");
                        }
                    } else {
                        LOG.error("Can't find SubjectAccount by Login");
                    }
                } else {
                    LOG.error("Can't find assigneeUser");
                }
            }
        } catch (Exception ex) {
            LOG.error("getLoginSubjectAccountByLoginIgovAccount: ", ex);
        }
        return result;
    }
}
