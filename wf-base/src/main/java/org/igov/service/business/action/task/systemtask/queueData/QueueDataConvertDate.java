package org.igov.service.business.action.task.systemtask.queueData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.form.FormData;
import org.activiti.engine.form.StartFormData;
import org.igov.service.business.action.task.core.AbstractModelTask;
import org.igov.service.business.action.task.form.QueueDataFormType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author inna
 */
@Component("queueDataConvertDate")
public class QueueDataConvertDate extends AbstractModelTask implements JavaDelegate {
    static final transient Logger LOG = LoggerFactory
            .getLogger(QueueDataConvertDate.class);
    
    @Override
    public void execute(DelegateExecution oExecution) throws Exception {
        StartFormData oStartformData = oExecution.getEngineServices()
                .getFormService()
                .getStartFormData(oExecution.getProcessDefinitionId());

        scanExecutionOnQueueTickets(oExecution, oStartformData);
    }
    
	public void scanExecutionOnQueueTickets(DelegateExecution oExecution, FormData oFormData) {
		LOG.info("SCAN:queueData");
		List<String> asFieldID = getListField_QueueDataFormType(oFormData);// startformData
		LOG.info("(asFieldID={})", asFieldID.toString());
		List<String> asFieldValue = getVariableValues(oExecution, asFieldID);
		LOG.info("(asFieldValue={})", asFieldValue.toString());
		if (!asFieldValue.isEmpty()) {
			String sValue = asFieldValue.get(0);
			LOG.info("(sValue={})", sValue);
			if (sValue != null && !"".equals(sValue.trim()) && !"null".equals(sValue.trim())) {
				LOG.info("sValue is present, so queue is filled");
				Map<String, Object> m = QueueDataFormType.parseQueueData(sValue);

				String sDate = (String) m.get(QueueDataFormType.sDate);
				LOG.info("(sDate queueDataConvertDate={})", sDate);
				
				String sDateRes = formateDate(sDate);
				
				LOG.info("(sDateRes={})", sDateRes);

				oExecution.setVariable("sNotification_day", sDateRes);
				LOG.info("(date_of_visit={})", sDateRes);
			}
		}

	}
	
	public static String formateDate(String dateString) {
	    Date date;
	    String formattedDate = "";
	    try {
	        date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault()).parse(dateString);
	        formattedDate = new SimpleDateFormat("dd MMMM, HH:mm:ss", new Locale("uk","UA")).format(date);
	    } catch (ParseException e) {
	        e.printStackTrace();
	    }

	    return formattedDate;
	}

}