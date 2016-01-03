package org.igov.activiti.systemtask;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.form.StartFormData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.igov.activiti.common.AbstractModelTask;
import org.igov.activiti.form.QueueDataFormType;

import java.util.List;
import java.util.Map;

@Component("releaseTicketsOfQueue")
public class ReleaseTicketsOfQueue extends AbstractModelTask implements JavaDelegate {

    private final static Logger oLog = LoggerFactory.getLogger(ReleaseTicketsOfQueue.class);

    @Override
    public void execute(DelegateExecution oExecution) throws Exception {
        StartFormData oStartformData = oExecution.getEngineServices()
                .getFormService()
                .getStartFormData(oExecution.getProcessDefinitionId());

        oLog.info("ReleaseTicketsOfQueue:execute start");
        oLog.info("SCAN:queueData");
        List<String> asFieldID = getListField_QueueDataFormType(oStartformData);
        oLog.info("asFieldID=" + asFieldID.toString());
        List<String> asFieldValue = getVariableValues(oExecution, asFieldID);
        oLog.info("asFieldValue=" + asFieldValue.toString());
        if (!asFieldValue.isEmpty()) {
            String sValue = asFieldValue.get(0);
            oLog.info("sValue=" + sValue);
            long nID_FlowSlotTicket = 0;

            Map<String, Object> m = QueueDataFormType.parseQueueData(sValue);
            nID_FlowSlotTicket = QueueDataFormType.get_nID_FlowSlotTicket(m);
            oLog.info("nID_FlowSlotTicket=" + nID_FlowSlotTicket);
            String sDate = (String) m.get(QueueDataFormType.sDate);
            oLog.info("sDate=" + sDate);

			try {

				long nID_Task_Activiti = 1; // TODO set real ID!!!
				try {
					nID_Task_Activiti = Long.valueOf(oExecution.getProcessInstanceId());
					oLog.info("nID_Task_Activiti:Ok!");
				} catch (NumberFormatException oException) {
					oLog.error(oException.getMessage());
				}
				oLog.info("nID_Task_Activiti=" + nID_Task_Activiti);

				if (!oFlowSlotTicketDao.unbindFromTask(nID_FlowSlotTicket)) {
					oLog.error("nID_Task_Activiti is empty for oFlowSlotTicket with ID " + nID_FlowSlotTicket);
				}

			} catch (Exception oException) {
				oLog.error(oException.getMessage(), oException);
			}

        }

        oLog.info("ReleaseTicketsOfQueue:execute end");
    }

}
