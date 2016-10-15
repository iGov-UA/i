package org.igov.service.business.action.task.systemtask;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.form.StartFormData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.igov.service.business.action.task.core.AbstractModelTask;
import org.igov.service.business.action.task.form.QueueDataFormType;

import java.util.List;
import java.util.Map;

@Component("releaseTicketsOfQueue")
public class ReleaseTicketsOfQueue extends AbstractModelTask implements JavaDelegate {

    private final static Logger LOG = LoggerFactory.getLogger(ReleaseTicketsOfQueue.class);

    @Override
    public void execute(DelegateExecution oExecution) throws Exception {
        StartFormData oStartformData = oExecution.getEngineServices()
                .getFormService()
                .getStartFormData(oExecution.getProcessDefinitionId());

        LOG.info("ReleaseTicketsOfQueue:execute start");
        LOG.info("SCAN:queueData");
        List<String> asFieldID = getListField_QueueDataFormType(oStartformData);
        LOG.info("(asFieldID={})", asFieldID.toString());
        List<String> asFieldValue = getVariableValues(oExecution, asFieldID);
        LOG.info("(asFieldValue={})", asFieldValue.toString());
        if (!asFieldValue.isEmpty()) {
            String sValue = asFieldValue.get(0);
            LOG.info("(sValue={})", sValue);
            long nID_FlowSlotTicket = 0;

            Map<String, Object> m = QueueDataFormType.parseQueueData(sValue);
            nID_FlowSlotTicket = QueueDataFormType.get_nID_FlowSlotTicket(m);
            LOG.info("(nID_FlowSlotTicket={})", nID_FlowSlotTicket);
            String sDate = (String) m.get(QueueDataFormType.sDate);
            LOG.info("(sDate={})" + sDate);

			try {

				long nID_Task_Activiti = 1; // TODO set real ID!!!
				try {
					nID_Task_Activiti = Long.valueOf(oExecution.getProcessInstanceId());
					LOG.info("nID_Task_Activiti:Ok!");
				} catch (NumberFormatException oException) {
					LOG.error(oException.getMessage());
				}
				LOG.info("(nID_Task_Activiti={})", nID_Task_Activiti);

				if (!oFlowSlotTicketDao.unbindFromTask(nID_FlowSlotTicket)) {
					LOG.error("nID_Task_Activiti is empty for oFlowSlotTicket with ID {}", nID_FlowSlotTicket);
				}

			} catch (Exception oException) {
				LOG.error("Error: {}",oException.getMessage());
                LOG.trace("FAIL:", oException);
			}

        }

        LOG.info("ReleaseTicketsOfQueue:execute end");
    }

}
