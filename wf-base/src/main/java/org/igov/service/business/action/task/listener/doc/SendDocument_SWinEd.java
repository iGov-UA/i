package org.igov.service.business.action.task.listener.doc;

import java.rmi.RemoteException;

import javax.xml.rpc.holders.IntHolder;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.apache.axis.AxisFault;
import org.igov.service.business.action.task.core.AbstractModelTask;
import org.igov.util.swind.DocumentInData;
import org.igov.util.swind.DocumentType;
import org.igov.util.swind.SWinEDSoapStub;
import org.igov.util.swind.holders.ProcessResultHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("SendDocument_SWinEd")
public class SendDocument_SWinEd extends AbstractModelTask implements TaskListener {

	private static final long serialVersionUID = 1L;
	private final static String SWIN_ED_ANSWER_STATUS_VARIABLE = "sAnswer_SWinEd_Doc";
	private final static String SWIN_ED_ERROR_VARIABLE = "nAnswerError_SWinEd_Doc";
	
    private final static Logger LOG = LoggerFactory.getLogger(SendDocument_SWinEd.class);

    @Autowired
    RuntimeService runtimeService;
    
    private Expression sSenderEDRPOU;
    private Expression nSenderDept;
    private Expression sEDRPOU;
    private Expression nDept;
    private Expression sDocId;
    private Expression sDocumentData;
    private Expression sOriginalDocId;
    private Expression nTask;
    
    @Override
    public void notify(DelegateTask delegateTask) {

        DelegateExecution execution = delegateTask.getExecution();

        String sSenderEDRPOUValue = getStringFromFieldExpression(this.sSenderEDRPOU, execution);
        String nSenderDeptValue = getStringFromFieldExpression(this.nSenderDept, execution);
        String sEDRPOUValue = getStringFromFieldExpression(this.sEDRPOU, execution);
        String nDeptValue = getStringFromFieldExpression(this.nDept, execution);
        String sDocIdValue = getStringFromFieldExpression(this.sDocId, execution);
        String sDocumentDataValue = getStringFromFieldExpression(this.sDocumentData, execution);
        String sOriginalDocIdValue = getStringFromFieldExpression(this.sOriginalDocId, execution);
        String nTaskValue = getStringFromFieldExpression(this.nTask, execution);

        LOG.info("Parameters of the SendDocument_SWinEd sSenderEDRPOU:{}, nSenderDept:{}, sEDRPOU:{}, nDept:{}, sDocId:{},"
        		+ "sDocumentData:{} , sOriginalDocId:{}, nTask:{}", sSenderEDRPOUValue, nSenderDeptValue, sEDRPOUValue, nDeptValue,
        		sDocIdValue, sDocumentDataValue, sOriginalDocIdValue, nTaskValue);

        try {
			SWinEDSoapStub stub = new SWinEDSoapStub();
			ProcessResultHolder handler = new ProcessResultHolder();
			IntHolder errorDocIdx = new IntHolder();
			DocumentInData[] docs = new DocumentInData[1];
			DocumentInData document = new DocumentInData();
			document.setDept(Integer.valueOf(nDeptValue));
			document.setDocId(sDocIdValue);
			document.setEDRPOU(sEDRPOUValue);
			document.setOriginalDocId(sOriginalDocIdValue);
			document.setTask(Integer.valueOf(nTaskValue));
			docs[0] = document;
			stub.post(sSenderEDRPOUValue, Integer.valueOf(nSenderDeptValue), DocumentType.Original, docs, handler, errorDocIdx);
			
			LOG.info("Setting SwinEd status response variable to {} for the process {}", handler.value.getValue(), delegateTask.getProcessInstanceId());
			runtimeService.setVariable(delegateTask.getProcessInstanceId(), SWIN_ED_ANSWER_STATUS_VARIABLE, handler.value.getValue());
			LOG.info("Setting SwinEd error code response variable to {} for the process {}", errorDocIdx.value, delegateTask.getProcessInstanceId());
			runtimeService.setVariable(delegateTask.getProcessInstanceId(), SWIN_ED_ERROR_VARIABLE, errorDocIdx.value);
		} catch (AxisFault e) {
			LOG.error("Error occured while constructing a call to SWinEd {}", e.getMessage());
		} catch (NumberFormatException e) {
			LOG.error("Error occured while making a call to SWinEd {}", e.getMessage());
		} catch (RemoteException e) {
			LOG.error("Error occured while making a call to SWinEd {}", e.getMessage());
		}
    }

}
