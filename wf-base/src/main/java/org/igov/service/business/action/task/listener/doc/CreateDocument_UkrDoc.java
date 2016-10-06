package org.igov.service.business.action.task.listener.doc;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.igov.io.GeneralConfig;
import org.igov.io.web.HttpRequester;
import org.igov.io.web.RestRequest;
import org.igov.service.business.action.task.core.AbstractModelTask;

import static org.igov.service.business.action.task.core.AbstractModelTask.getStringFromFieldExpression;

import org.igov.service.business.action.task.systemtask.ProcessCountTaskListener;
import org.igov.service.business.action.task.systemtask.doc.util.UkrDocUtil;
import org.igov.service.business.promin.ProminSession_Singleton;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component("CreateDocument_UkrDoc")
public class CreateDocument_UkrDoc extends AbstractModelTask implements TaskListener {

    public static final String UKRDOC_ID_DOCUMENT_VARIABLE_NAME = "sID_Document";
    private final static Logger LOG = LoggerFactory.getLogger(CreateDocument_UkrDoc.class);

    private Expression sLoginAuthor;
    private Expression sHead;
    private Expression sBody;
    private Expression nID_Pattern;
    private Expression sID_Order_GovPublic;
    private Expression sSourceChannel;
    private Expression bankIdlastName;
    private Expression bankIdfirstName;
    private Expression bankIdmiddleName;
    private Expression sDepartNameFull;
    private Expression sSex;
    private Expression sAddress;
    private Expression sZipCode;
    private Expression sCity;
    private Expression sID_Document;
    private Expression sDateAppeal;

    private Expression bOrganization;
    private Expression sCompanyName;
    private Expression sID_EDRPOU;
    private Expression sBossFirstLastName;
    private Expression sDateDocIncoming;
    private Expression sNumberDocIncoming;
        
    @Autowired
    GeneralConfig generalConfig;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    FormService formService;

    @Autowired
    TaskService taskService;
    
    @Autowired
    HttpRequester httpRequester;
    
    @Autowired
    private ProminSession_Singleton prominSession_Singleton;

    @Override
    public void notify(DelegateTask delegateTask) {

        DelegateExecution execution = delegateTask.getExecution();
        String sID_DocumentValue = getStringFromFieldExpression(this.sID_Document, execution);
        try {
            if (sID_DocumentValue == null || "".equals(sID_DocumentValue.trim())) {
                String sID_Order = generalConfig.getOrderId_ByProcess(Long.valueOf(execution.getProcessInstanceId()));
                String sLoginAuthorValue = getStringFromFieldExpression(this.sLoginAuthor, execution);
                String sHeadValue = getStringFromFieldExpression(this.sHead, execution) + " (" + sID_Order + ")";
                String sBodyValue = getStringFromFieldExpression(this.sBody, execution);
                String nID_PatternValue = getStringFromFieldExpression(this.nID_Pattern, execution);
                String sID_Order_GovPublicValue = getStringFromFieldExpression(this.sID_Order_GovPublic, execution);
                String sSourceChannelValue = getStringFromFieldExpression(this.sSourceChannel, execution);
                String sDepartNameFullValue = getStringFromFieldExpression(this.sDepartNameFull, execution);
                String sSexValue = getStringFromFieldExpression(this.sSex, execution);
                String sAddressValue = getStringFromFieldExpression(this.sAddress, execution);
                String sZipCodeValue = getStringFromFieldExpression(this.sZipCode, execution);
                String sCityValue = getStringFromFieldExpression(this.sCity, execution);
                String bankIdlastName = getStringFromFieldExpression(this.bankIdlastName, execution);
                String bankIdfirstName = getStringFromFieldExpression(this.bankIdfirstName, execution);
                String bankIdmiddleName = getStringFromFieldExpression(this.bankIdmiddleName, execution);
                String sDateAppealValue = getStringFromFieldExpression(this.sDateAppeal, execution);;
                String shortFIO = "_", fullIO = "_";
                sID_Order_GovPublicValue = runtimeService.hasVariable(execution.getProcessInstanceId(), ProcessCountTaskListener.S_ID_ORDER_GOV_PUBLIC)?
                		 (String)runtimeService.getVariable(execution.getProcessInstanceId(), ProcessCountTaskListener.S_ID_ORDER_GOV_PUBLIC):"";
                LOG.info("Parameters of the task sLogin:{}, sHead:{}, sBody:{}, nId_PatternValue:{}, bankIdlastName:{}, bankIdfirstName:{}, bankIdmiddleName:{}", sLoginAuthorValue, sHeadValue, sBodyValue, nID_PatternValue, bankIdlastName, bankIdfirstName, bankIdmiddleName);

                if (bankIdlastName != null && bankIdfirstName != null && bankIdmiddleName != null
                        && bankIdfirstName.length() > 0 && bankIdmiddleName.length() > 0) {
                    fullIO = new StringBuilder(bankIdfirstName).append(" ").append(bankIdmiddleName).toString();
                    shortFIO = new StringBuilder(bankIdlastName).append(". ")
                            .append(bankIdfirstName.substring(0, 1)).append(". ")
                            .append(bankIdmiddleName.substring(0, 1)).append(".").toString();
                }

                List<Attachment> attachments = new LinkedList<Attachment>();
                List<Attachment> attach2 = taskService.getTaskAttachments(delegateTask.getId());
                if (attach2 != null && !attach2.isEmpty()) {
                    attachments = attach2;
                }

                LOG.info("Found attachments for the process {}: {}", delegateTask.getId(), attach2 != null ? attach2.size() : 0);
                String sessionId = prominSession_Singleton.getSid_Auth_UkrDoc_SED();
                LOG.info("Retrieved session ID:" + sessionId);

                if (attachments.isEmpty()) {
                    DelegateExecution oExecution = delegateTask.getExecution();
                    // получить группу бп
                    Set<IdentityLink> identityLink = delegateTask.getCandidates();
                    // получить User группы
                    List<User> aUser = oExecution.getEngineServices().getIdentityService()
                            .createUserQuery()
                            .memberOfGroup(identityLink.iterator().next().getGroupId())
                            .list();

                    LOG.info("Finding any assigned user-member of group. (aUser={})", aUser);
                    if (aUser == null || aUser.size() == 0 || aUser.get(0) == null || aUser.get(0).getId() == null) {
                        //TODO  what to do if no user?
                    } else {
	            // setAuthenticatedUserId первого попавщегося
                        //TODO Shall we implement some logic for user selection.
                        oExecution.getEngineServices().getIdentityService().setAuthenticatedUserId(aUser.get(0).getId());
                        // получить информацию по стартовой форме бп
                        FormData oStartFormData = oExecution.getEngineServices().getFormService()
                                .getStartFormData(oExecution.getProcessDefinitionId());
                        LOG.info("beginning of addAttachmentsToTask(startformData, task):execution.getProcessDefinitionId()={}",
                                oExecution.getProcessDefinitionId());
                        attachments = addAttachmentsToTask(oStartFormData, delegateTask);
                    }
                }

                LOG.info("Processing {} attachments", attachments.size());

                Boolean bOrganization = Boolean.valueOf(getStringFromFieldExpression(this.bOrganization, execution));
                String sCompanyName = getStringFromFieldExpression(this.sCompanyName, execution);
                String sID_EDRPOU = getStringFromFieldExpression(this.sID_EDRPOU, execution);
                String sBossFirstLastName = getStringFromFieldExpression(this.sBossFirstLastName, execution);
                String sDateDocIncoming = getStringFromFieldExpression(this.sDateDocIncoming, execution);
                String sNumberDocIncoming = getStringFromFieldExpression(this.sNumberDocIncoming, execution);
                
                Map<String, Object> urkDocRequest = UkrDocUtil.makeJsonRequestObject(sHeadValue, sBodyValue, sLoginAuthorValue, nID_PatternValue,
                        attachments, execution.getId(), generalConfig, sID_Order_GovPublicValue, sSourceChannelValue, shortFIO, fullIO,
                        sDepartNameFullValue, sSexValue, sAddressValue, sZipCodeValue, sCityValue, sDateAppealValue
                        , bOrganization, sCompanyName, sID_EDRPOU, sBossFirstLastName, sDateDocIncoming, sNumberDocIncoming
                );

                JSONObject json = new JSONObject();
                json.putAll(urkDocRequest);

                LOG.info("Created ukr doc request object:" + json.toJSONString());

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "promin.privatbank.ua/EXCL " + sessionId);
                headers.set("Content-Type", "application/json; charset=utf-8");

                String resp = new RestRequest().post(generalConfig.getURL_UkrDoc_SED(), json.toJSONString(),
                        null, StandardCharsets.UTF_8, String.class, headers);

                LOG.info("Ukrdoc response:" + resp);
                org.activiti.engine.impl.util.json.JSONObject respJson = new org.activiti.engine.impl.util.json.JSONObject(resp);
                Object details = respJson.get("details");

                if (details != null) {
                    String documentId = ((org.activiti.engine.impl.util.json.JSONObject) details).get("id") + ":"
                            + ((org.activiti.engine.impl.util.json.JSONObject) details).get("year");
                    runtimeService.setVariable(execution.getProcessInstanceId(), UKRDOC_ID_DOCUMENT_VARIABLE_NAME, documentId);
                    runtimeService.setVariable(execution.getProcessInstanceId(), "sID_Document_UkrDoc", documentId);
                    runtimeService.setVariable(execution.getProcessInstanceId(), "sID_Order_GovPublic", sID_Order_GovPublicValue);
                    LOG.info("Set variable to runtime process:{}", documentId);

                    LOG.info("Looking for a new task to set form properties");
                    List<Task> tasks = taskService.createTaskQuery().processInstanceId(execution.getId()).active().list();
                    LOG.info("Get {} active tasks for the process", tasks);
                    for (Task task : tasks) {
                        TaskFormData formData = formService.getTaskFormData(task.getId());
                        for (FormProperty formProperty : formData.getFormProperties()) {
                            if (formProperty.getId().equals("sID_Document_UkrDoc")) {
                                LOG.info("Found form property with the id " + "sID_Document_UkrDoc" + ". Setting value {}", documentId);
                                if (formProperty instanceof FormPropertyImpl) {
                                    ((FormPropertyImpl) formProperty).setValue(documentId);
                                }
                            }
                        }
                        StartFormData startFormData = formService.getStartFormData(execution.getProcessDefinitionId());
                        for (FormProperty formProperty : startFormData.getFormProperties()) {
                            if (formProperty.getId().equals("sID_Document_UkrDoc")) {
                                LOG.info("Found start form property with the id " + "sID_Document_UkrDoc" + ". Setting value {}", documentId);
                                if (formProperty instanceof FormPropertyImpl) {
                                    ((FormPropertyImpl) formProperty).setValue(documentId);
                                }
                            }
                        }
                    }
                }
            } else {
                LOG.info("Skip create document. Document has already been created: " + sID_DocumentValue);
            }
        } catch (Exception ex) {
            LOG.error("", ex);
            throw ex;
        }
    }

}
