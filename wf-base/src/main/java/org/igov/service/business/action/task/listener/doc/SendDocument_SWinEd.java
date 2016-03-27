package org.igov.service.business.action.task.listener.doc;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.rpc.holders.IntHolder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import org.activiti.engine.FormService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.form.FormData;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.form.FormPropertyImpl;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.apache.axis.AxisFault;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.igov.io.GeneralConfig;
import org.igov.io.db.kv.temp.IBytesDataInmemoryStorage;
import org.igov.io.db.kv.temp.exception.RecordInmemoryException;
import org.igov.service.business.action.task.core.AbstractModelTask;
import org.igov.service.business.action.task.systemtask.doc.util.UkrDocUtil;
import org.igov.util.swind.DocumentInData;
import org.igov.util.swind.DocumentType;
import org.igov.util.swind.SWinEDLocator;
import org.igov.util.swind.SWinEDSoapProxy;
import org.igov.util.swind.SWinEDSoapStub;
import org.igov.util.swind.holders.ProcessResultHolder;
import org.igov.util.swind.jaxb.DBody;
import org.igov.util.swind.jaxb.DHead;
import org.igov.util.swind.jaxb.ObjectFactory;
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
    
    @Autowired
    TaskService taskService;
    
    @Autowired
    GeneralConfig generalConfig;
    
    @Autowired
    FormService formService;
    
    @Autowired
    private IBytesDataInmemoryStorage oBytesDataInmemoryStorage;
    
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
	        
	        SWinEDSoapProxy soapProxy = new SWinEDSoapProxy();
			ProcessResultHolder handler = new ProcessResultHolder();
			IntHolder errorDocIdx = new IntHolder();
			
			DBody dbody = new DBody();
			String hlname = null;
			String hpname = null;
			String hfname = null;
			String htin = null;
			String passport = null;
			String hemail = null;
			String hstreet = null;
			String hbuild = null;
			String hapart = null;
			//String hfill = Calendar.getInstance().get;
			StartFormData startFormData = formService.getStartFormData(delegateTask.getProcessDefinitionId());
			for (FormProperty formProperty : startFormData.getFormProperties()) {
				if (formProperty.getId().equals("bankIdfirstName")){
					hpname = formProperty.getValue();
				} else if (formProperty.getId().equals("bankIdlastName")){
					hlname = formProperty.getValue();
				} else if (formProperty.getId().equals("bankIdmiddleName")){
					hfname = formProperty.getValue();
				} else if (formProperty.getId().equals("bankIdinn")){
					htin = formProperty.getValue();
				} else if (formProperty.getId().equals("passport")){
					passport = formProperty.getValue();
				} else if (formProperty.getId().equals("email")){
					hemail = formProperty.getValue();
				} else if (formProperty.getId().equals("street")){
					hstreet = formProperty.getValue();
				} else if (formProperty.getId().equals("building")){
					hbuild = formProperty.getValue();
				} else if (formProperty.getId().equals("apartment")){
					hapart = formProperty.getValue();
				}
			}
			String hpass = null;
			String hpassDate = null;
			String hpassiss = null;
			if (passport != null){
				hpass = StringUtils.substringBefore(passport.trim(), " ");
				hpassDate = StringUtils.substringAfterLast(passport.trim(), " ");
				hpassiss = StringUtils.substringAfter(passport.trim(), " ");
				hpassiss = StringUtils.substringBeforeLast(hpassiss, " ");
			}
			
			LOG.info("Loaded the next variables to pass to swinEd. hlname:{}, hpname:{}, hfname:{}, htin:{}, hemail:{}, "
					+ "hpass:{}, hpassDate:{}, hpassiss:{}, hstreet:{}, hbuild:{}, hapart:{}",
					hlname, hpname, hfname, htin, hemail, hpass, hpassDate, hpassiss, hstreet, hbuild, hapart);
			
			DBody body = new DBody();
			ObjectFactory factory = new ObjectFactory();
			body.setHLNAME(hlname);
			body.setHPNAME(hpname);
			body.setHFNAME(factory.createDBodyHFNAME(hfname));
			body.setHTIN(htin);
			body.setHEMAIL(hemail);
			body.setHPASS(factory.createDBodyHPASS(hpass));
			body.setHPASSDATE(factory.createDBodyHPASSDATE(hpassDate));
			body.setHPASSISS(factory.createDBodyHPASS(hpassiss));
			body.setHSTREET(hstreet);
			body.setHBUILD(hbuild);
			body.setHAPT(factory.createDBodyHAPT(hapart));
			body.setHCOUNTRY("Україна");
			
			StringWriter sw = new StringWriter();
			JAXBContext jc = JAXBContext.newInstance(DBody.class);
			Marshaller m = jc.createMarshaller();
			m.marshal(body, sw);
			
			String bodyContent = sw.toString();
			LOG.info("Created document with customer info to embedd to message: {}", bodyContent);
			
			DocumentInData document = new DocumentInData();
			document.setDept(Integer.valueOf(nDeptValue));
			document.setDocument(Base64.encodeBase64(bodyContent.getBytes("UTF-8")));
			document.setDocId(sDocIdValue);
			document.setEDRPOU(sEDRPOUValue);
			document.setOriginalDocId(sOriginalDocIdValue);
			document.setTask(Integer.valueOf(delegateTask.getProcessInstanceId()));
			
			DocumentInData[] docs = new DocumentInData[1];
			docs[0] = document;
			
			LOG.info("Sending document to SwinEd with parameters. SenderEDRPOU:{}, nSenderDept:{}, DocumentType:{}, docs:{}", sSenderEDRPOUValue, nSenderDeptValue, DocumentType.Original, docs);
			soapProxy.post(sSenderEDRPOUValue, Integer.valueOf(nSenderDeptValue), DocumentType.Original, docs, handler, errorDocIdx);
			
			LOG.info("Setting SwinEd status response variable to {} for the process {}", handler.value.getValue(), delegateTask.getProcessInstanceId());
			runtimeService.setVariable(delegateTask.getProcessInstanceId(), SWIN_ED_ANSWER_STATUS_VARIABLE, handler.value.getValue());
			LOG.info("Setting SwinEd error code response variable to {} for the process {}", errorDocIdx.value, delegateTask.getProcessInstanceId());
			runtimeService.setVariable(delegateTask.getProcessInstanceId(), SWIN_ED_ERROR_VARIABLE, errorDocIdx.value);
			
			LOG.info("Looking for a new task to set form properties");
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(execution.getId()).active().list();
            LOG.info("Get {} active tasks for the process", tasks);
            for (Task task : tasks) {
                TaskFormData formData = formService.getTaskFormData(task.getId());
                for (FormProperty formProperty : formData.getFormProperties()) {
                    if (formProperty.getId().equals(SWIN_ED_ANSWER_STATUS_VARIABLE)) {
                        LOG.info("Found form property with the id " + SWIN_ED_ANSWER_STATUS_VARIABLE + ". Setting value {}", handler.value.getValue());
                        if (formProperty instanceof FormPropertyImpl) {
                            ((FormPropertyImpl) formProperty).setValue(handler.value.getValue());
                        }
                    }
                    if (formProperty.getId().equals(SWIN_ED_ERROR_VARIABLE)) {
                        LOG.info("Found form property with the id " + SWIN_ED_ERROR_VARIABLE + ". Setting value {}", errorDocIdx.value);
                        if (formProperty instanceof FormPropertyImpl) {
                            ((FormPropertyImpl) formProperty).setValue(String.valueOf(errorDocIdx.value));
                        }
                    }
                }
            }
		} catch (AxisFault e) {
			LOG.error("Error occured while constructing a call to SWinEd {}", e.getMessage(), e);
		} catch (NumberFormatException e) {
			LOG.error("Error occured while making a call to SWinEd {}", e.getMessage(), e);
		} catch (RemoteException e) {
			LOG.error("Error occured while making a call to SWinEd {}", e.getMessage(), e);
		} catch (JAXBException e) {
			LOG.error("Error occured while creating xml message to send {}", e.getMessage(), e);
		} catch (UnsupportedEncodingException e) {
			LOG.error("Error occured while creating xml message to send {}", e.getMessage(), e);
		}
    }

}
