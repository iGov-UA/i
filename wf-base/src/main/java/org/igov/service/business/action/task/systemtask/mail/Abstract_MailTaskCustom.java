package org.igov.service.business.action.task.systemtask.mail;

import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.form.FormData;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.task.Task;
import org.igov.service.controller.security.AuthenticationTokenSelector;
import org.apache.commons.lang3.StringUtils;
import org.igov.io.fs.FileSystemDictonary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.igov.service.business.action.task.core.AbstractModelTask;
import org.igov.service.business.finance.Currency;
import org.igov.service.business.object.Language;
import org.igov.service.business.access.AccessKeyService;
import org.igov.service.business.finance.Liqpay;
import org.igov.io.GeneralConfig;
import org.igov.io.mail.Mail;
import org.igov.util.Tool;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.igov.service.business.action.task.core.ActionTaskService;
import org.igov.service.business.action.task.systemtask.misc.CancelTaskUtil;
import static org.igov.io.fs.FileSystemData.getFileData_Pattern;

import org.igov.service.controller.security.AccessContract;
import static org.igov.util.ToolLuna.getProtectedNumber;
import org.igov.util.ToolWeb;

public abstract class Abstract_MailTaskCustom implements JavaDelegate {

    static final transient Logger LOG = LoggerFactory
            .getLogger(Abstract_MailTaskCustom.class);
    private static final Pattern TAG_PAYMENT_BUTTON_LIQPAY = Pattern.compile("\\[paymentButton_LiqPay(.*?)\\]");
    private static final Pattern TAG_sPATTERN_CONTENT_CATALOG = Pattern.compile("[a-zA-Z]+\\{\\[(.*?)\\]\\}");
    private static final Pattern TAG_PATTERN_PREFIX = Pattern.compile("_[0-9]+");
    private static final Pattern TAG_PATTERN_DOUBLE_BRACKET = Pattern.compile("\\{\\[(.*?)\\]\\}");
    private static final String TAG_CANCEL_TASK = "[cancelTask]";
    private static final String TAG_nID_Protected = "[nID_Protected]";
    private static final String TAG_sID_Order = "[sID_Order]";
    private static final String TAG_nID_SUBJECT = "[nID_Subject]";
    //private static final String TAG_sURL_SERVICE_MESSAGE = "[sURL_ServiceMessage]";
    private static final Pattern TAG_sURL_SERVICE_MESSAGE = Pattern.compile("\\[sURL_ServiceMessage(.*?)\\]");
    private static final Pattern TAG_sPATTERN_CONTENT_COMPILED = Pattern.compile("\\[pattern/(.*?)\\]");
    private static final String TAG_Function_AtEnum = "enum{[";
    private static final String TAG_Function_To = "]}";
    private static final String PATTERN_MERCHANT_ID = "sID_Merchant%s";
    private static final String PATTERN_SUM = "sSum%s";
    private static final String PATTERN_CURRENCY_ID = "sID_Currency%s";
    private static final String PATTERN_DESCRIPTION = "sDescription%s";
    private static final String PATTERN_SUBJECT_ID = "nID_Subject%s";
    @Autowired
    public TaskService taskService;
    @Value("${mailServerHost}")
    public String mailServerHost;
    @Value("${mailServerPort}")
    public String mailServerPort;
    @Value("${mailServerDefaultFrom}")
    public String mailServerDefaultFrom;
    @Value("${mailServerUsername}")
    public String mailServerUsername;
    @Value("${mailServerPassword}")
    public String mailServerPassword;
    @Value("${mailAddressNoreply}")
    public String mailAddressNoreplay;
    
    @Value("${mailServerUseSSL}")
    private boolean bSSL;
    @Value("${mailServerUseTLS}")
    private boolean bTLS;
    
    public Expression from;
    public Expression to;
    public Expression subject;
    public Expression text;
    protected Expression sID_Merchant;
    protected Expression sSum;
    protected Expression sID_Currency;
    protected Expression sLanguage;
    protected Expression sDescription;
    protected Expression nID_Subject;
    //private static final String PATTERN_DELIMITER = "_";
    @Autowired
    AccessKeyService accessCover;
    @Autowired
    GeneralConfig generalConfig;
    @Autowired
    Mail oMail;
    //@Autowired
    //AccessDataService accessDataDao;
    @Autowired
    Liqpay liqBuy;
    @Autowired
    private CancelTaskUtil cancelTaskUtil;

    protected String replaceTags(String sTextSource, DelegateExecution execution)
            throws Exception {

        if (sTextSource == null) {
            return null;
        }

        LOG.debug("sTextSource=" + sTextSource);
        
        String sTextReturn = sTextSource;

        sTextReturn = replaceTags_LIQPAY(sTextReturn, execution);

        sTextReturn = populatePatternWithContent(sTextReturn);

        sTextReturn = replaceTags_Enum(sTextReturn, execution);

        sTextReturn = replaceTags_Catalog(sTextReturn, execution);

        sTextReturn = new FileSystemDictonary().replaceMVSTagWithValue(sTextReturn);

        Long nID_Order = getProtectedNumber(Long.valueOf(execution
                .getProcessInstanceId()));

        if (sTextReturn.contains(TAG_nID_Protected)) {
            LOG.info("TAG_nID_Protected:(nID_Order={})",nID_Order);
            sTextReturn = sTextReturn.replaceAll("\\Q" + TAG_nID_Protected + "\\E", "" + nID_Order);
        }
        
        if (sTextReturn.contains(TAG_sID_Order)) {
            String sID_Order = generalConfig.sID_Order_ByOrder(nID_Order);
            LOG.info("TAG_sID_Order:(sID_Order={})", sID_Order);
            sTextReturn = sTextReturn.replaceAll("\\Q" + TAG_sID_Order + "\\E", "" + sID_Order);
        }
        
        if (sTextReturn.contains(TAG_CANCEL_TASK)) {
            LOG.info("TAG_CANCEL_TASK:(nID_Protected={})", nID_Order);
            String sHTML_CancelTaskButton = cancelTaskUtil.getCancelFormHTML(nID_Order);
            sTextReturn = sTextReturn.replace(TAG_CANCEL_TASK, sHTML_CancelTaskButton);
        }

        if (sTextReturn.contains(TAG_nID_SUBJECT)) {
            LOG.info("TAG_nID_SUBJECT: (nID_Subject={})", nID_Subject);
            sTextReturn = sTextReturn.replaceAll("\\Q" + TAG_nID_SUBJECT + "\\E", "" + nID_Subject);
        }

        sTextReturn = replaceTags_sURL_SERVICE_MESSAGE(sTextReturn, execution, nID_Order);
        
        return sTextReturn;
    }

    private String replaceTags_Enum(String textWithoutTags, DelegateExecution execution) {
        List<String> previousUserTaskId = getPreviousTaskId(execution);
        int nLimit = StringUtils.countMatches(textWithoutTags, TAG_Function_AtEnum);
        LOG.info("Found {} enum occurrences in the text", nLimit);
        Map<String, FormProperty> aProperty = new HashMap<String, FormProperty>();
        int foundIndex = 0;
        while (nLimit > 0) {
            nLimit--;
            int nAt = textWithoutTags.indexOf(TAG_Function_AtEnum, foundIndex);
            LOG.info("sTAG_Function_AtEnum, (nAt={})", nAt);
            int nTo = textWithoutTags.indexOf(TAG_Function_To, foundIndex);
            foundIndex = nTo + 1;
            LOG.info("sTAG_Function_AtEnum,(nTo={})", nTo);
            String sTAG_Function_AtEnum = textWithoutTags.substring(nAt
                    + TAG_Function_AtEnum.length(), nTo);
            LOG.info("(sTAG_Function_AtEnum={})", sTAG_Function_AtEnum);

            if (aProperty.isEmpty()) {
                loadPropertiesFromTasks(execution, previousUserTaskId,
                        aProperty);
            }
            boolean bReplaced = false;
            for (FormProperty property : aProperty.values()) {
                String sType = property.getType().getName();
                String snID = property.getId();
                if (!bReplaced && "enum".equals(sType)
                        && sTAG_Function_AtEnum.equals(snID)) {
                    LOG.info(String
                            .format("Found field! Matching property snID=%s:name=%s:sType=%s:sValue=%s with fieldNames",
                                    snID, property.getName(), sType, property.getValue()));

                    Object variable = execution.getVariable(property.getId());
                    if (variable != null) {
                        String sID_Enum = variable.toString();
                        LOG.info("execution.getVariable()(sID_Enum={})", sID_Enum);
                        String sValue = ActionTaskService.parseEnumProperty(property, sID_Enum);
                        LOG.info("9sValue={})", sValue);

                        textWithoutTags = textWithoutTags.replaceAll("\\Q"
                                + TAG_Function_AtEnum + sTAG_Function_AtEnum
                                + TAG_Function_To + "\\E", sValue);
                        bReplaced = true;
                    }
                }
            }

        }
        return textWithoutTags;
    }

    private String replaceTags_Catalog(String textStr, DelegateExecution execution) throws Exception {
        StringBuffer outputTextBuffer = new StringBuffer();
        String replacement = "";
        Matcher matcher = TAG_sPATTERN_CONTENT_CATALOG.matcher(textStr);
        if (matcher.find()) {
            matcher = TAG_sPATTERN_CONTENT_CATALOG.matcher(textStr);
            List<String> aPreviousUserTask_ID = getPreviousTaskId(execution);
            Map<String, FormProperty> mProperty = new HashMap<String, FormProperty>();
            loadPropertiesFromTasks(execution, aPreviousUserTask_ID, mProperty);
            while (matcher.find()) {
                String tag_Payment_CONTENT_CATALOG = matcher.group();
                LOG.info("Found tag catalog group:{}", matcher.group());
                if (!tag_Payment_CONTENT_CATALOG.startsWith(TAG_Function_AtEnum)) {
                    String prefix;
                    Matcher matcherPrefix = TAG_PATTERN_DOUBLE_BRACKET.matcher(tag_Payment_CONTENT_CATALOG);
                    if (matcherPrefix.find()) {
                        prefix = matcherPrefix.group();
                        LOG.info("Found double bracket tag group: {}", matcherPrefix.group());
                        String form_ID = StringUtils.replace(prefix, "{[", "");
                        form_ID = StringUtils.replace(form_ID, "]}", "");
                        LOG.info("(form_ID={})", form_ID);
                        FormProperty formProperty = mProperty.get(form_ID);
                        LOG.info("Found form property : {}", formProperty);
                        if (formProperty != null) {
                            if (formProperty.getValue() != null) {
                                replacement = formProperty.getValue();
                            } else {
                                List<String> aID = new ArrayList<String>();
                                aID.add(formProperty.getId());
                                List<String> proccessVariable = AbstractModelTask.getVariableValues(execution, aID);
                                LOG.info("(proccessVariable={})", proccessVariable);
                                if (!proccessVariable.isEmpty() && proccessVariable.get(0) != null) {
                                    replacement = proccessVariable.get(0);
                                }
                            }

                        }
                    }
                }
                LOG.info("Replacement for pattern : {}", replacement);
                matcher.appendReplacement(outputTextBuffer, replacement);
            }
        }
        return matcher.appendTail(outputTextBuffer).toString();
    }

    private String replaceTags_LIQPAY(String textStr, DelegateExecution execution) throws Exception {
        String LIQPAY_CALLBACK_URL = generalConfig.sHost()
                + "/wf/service/finance/setPaymentStatus_TaskActiviti?sID_Order=%s&sID_PaymentSystem=Liqpay&sData=%s&sPrefix=%s";

        StringBuffer outputTextBuffer = new StringBuffer();
        Matcher matcher = TAG_PAYMENT_BUTTON_LIQPAY.matcher(textStr);
        while (matcher.find()) {

            String tag_Payment_Button_Liqpay = matcher.group();
            String prefix = "";
            Matcher matcherPrefix = TAG_PATTERN_PREFIX.matcher(tag_Payment_Button_Liqpay);
            if (matcherPrefix.find()) {
                prefix = matcherPrefix.group();
            }

            String pattern_merchant = String.format(PATTERN_MERCHANT_ID, prefix);
            String pattern_sum = String.format(PATTERN_SUM, prefix);
            String pattern_currency = String.format(PATTERN_CURRENCY_ID, prefix);
            String pattern_description = String.format(PATTERN_DESCRIPTION, prefix);
            String pattern_subject = String.format(PATTERN_SUBJECT_ID, prefix);

            String sID_Merchant = execution.getVariable(pattern_merchant) != null
                    ? execution.getVariable(pattern_merchant).toString()
                    : execution.getVariable(String.format(PATTERN_MERCHANT_ID, "")).toString();
            LOG.info("{}={}", pattern_merchant, sID_Merchant);
            String sSum = execution.getVariable(pattern_sum) != null
                    ? execution.getVariable(pattern_sum).toString()
                    : execution.getVariable(String.format(PATTERN_SUM, "")).toString();
            LOG.info("{}={}", pattern_sum, sSum);
            if (sSum != null) {
                sSum = sSum.replaceAll(",", ".");
            }
            String sID_Currency = execution.getVariable(pattern_currency) != null
                    ? execution.getVariable(pattern_currency).toString()
                    : execution.getVariable(String.format(PATTERN_CURRENCY_ID, "")).toString();
            LOG.info("{}={}", pattern_currency, sID_Currency);
            Currency oID_Currency = Currency
                    .valueOf(sID_Currency == null ? "UAH" : sID_Currency);

            Language sLanguage = Liqpay.DEFAULT_LANG;
            String sDescription = execution.getVariable(pattern_description) != null
                    ? execution.getVariable(pattern_description).toString()
                    : execution.getVariable(String.format(PATTERN_DESCRIPTION, "")).toString();
            LOG.info("{}={}", pattern_description, sDescription);

            String sID_Order = "TaskActiviti_" + execution.getId().trim() + prefix;
            String sURL_CallbackStatusNew = String.format(
                    LIQPAY_CALLBACK_URL, sID_Order, "", prefix);
            String sURL_CallbackPaySuccess = null;
            Long nID_Subject = Long.valueOf(execution.getVariable(pattern_subject) != null
                    ? execution.getVariable(pattern_subject).toString()
                    : execution.getVariable(String.format(PATTERN_SUBJECT_ID, "")).toString());
            nID_Subject = (nID_Subject == null ? 0 : nID_Subject);
            LOG.info("{}={}", pattern_subject, nID_Subject);
            boolean bTest = generalConfig.bTest();
            String htmlButton = liqBuy.getPayButtonHTML_LiqPay(
                    sID_Merchant, sSum, oID_Currency, sLanguage,
                    sDescription, sID_Order, sURL_CallbackStatusNew,
                    sURL_CallbackPaySuccess, nID_Subject, bTest);
            matcher.appendReplacement(outputTextBuffer, htmlButton);
        }
        return matcher.appendTail(outputTextBuffer).toString();
    }

    private String replaceTags_sURL_SERVICE_MESSAGE(String textWithoutTags, 
            DelegateExecution execution, Long nID_Order) throws Exception {

        StringBuffer outputTextBuffer = new StringBuffer();
        Matcher matcher = TAG_sURL_SERVICE_MESSAGE.matcher(textWithoutTags);
        while (matcher.find()) {
            String tag_sURL_SERVICE_MESSAGE = matcher.group();
            String prefix = "";
            Matcher matcherPrefix = TAG_PATTERN_PREFIX.matcher(tag_sURL_SERVICE_MESSAGE);
            if (matcherPrefix.find()) {
                prefix = matcherPrefix.group();
            }
                String URL_SERVICE_MESSAGE = generalConfig.sHostCentral()
                        + "/wf/service/subject/message/setMessageRate";

                String sURI = ToolWeb.deleteContextFromURL(URL_SERVICE_MESSAGE);
                /*ProcessDefinition processDefinition = execution.getEngineServices()
                        .getRepositoryService().createProcessDefinitionQuery()
                        .processDefinitionId(execution.getProcessDefinitionId())
                        .singleResult();*/

                String sQueryParamPattern = "?"
                        //+ "sHead=Отзыв" + "&sMail= " + "&sData="
                        //+ (processDefinition != null && processDefinition.getName() != null ? processDefinition.getName().trim() : "")
                        + "&sID_Rate=" + prefix.replaceAll("_", "")
                        //+ "&nID_SubjectMessageType=1" + "&nID_Protected="+ nID_Protected
                        + "&sID_Order="+generalConfig.sID_Order_ByOrder(nID_Order)
                        ;

                String sQueryParam = String.format(sQueryParamPattern);
                if (nID_Subject != null) {
                    sQueryParam = sQueryParam
                            + "&nID_Subject=" + nID_Subject
                            ;
                }
                sQueryParam = sQueryParam
                        //TODO: Need remove in future!!!
                        + "&" + AuthenticationTokenSelector.ACCESS_CONTRACT + "=" + AccessContract.RequestAndLoginUnlimited.name()
                        ;
                LOG.info("(sURI={},{})", sURI, sQueryParam);
                String sAccessKey = accessCover.getAccessKeyCentral(sURI + sQueryParam, AccessContract.RequestAndLoginUnlimited);
                String replacemet = URL_SERVICE_MESSAGE + sQueryParam
                        + "&" + AuthenticationTokenSelector.ACCESS_KEY + "=" + sAccessKey;
                LOG.info("(replacemet URL={}) ", replacemet);
                matcher.appendReplacement(outputTextBuffer, replacemet);
        }
        return matcher.appendTail(outputTextBuffer).toString();
    }

    private void loadPropertiesFromTasks(DelegateExecution execution,
            List<String> previousUserTaskId,
            Map<String, FormProperty> aProperty) {
        LOG.info("(execution.getId()={})", execution.getId());
        LOG.info("(execution.getProcessDefinitionId()={})", execution.getProcessDefinitionId());
        LOG.info("(execution.getProcessInstanceId()={})", execution.getProcessInstanceId());
        String[] as = execution.getProcessDefinitionId().split("\\:");
        String s = as[2];
        LOG.info("(s={})", s);

        for (String taskId : previousUserTaskId) {
            try {
                FormData oTaskFormData = null;
                if (previousUserTaskId != null && !previousUserTaskId.isEmpty()) {
                    oTaskFormData = execution.getEngineServices()
                            .getFormService()
                            .getTaskFormData(taskId);
                }

                if (oTaskFormData != null && oTaskFormData.getFormProperties() != null) {
                    for (FormProperty property : oTaskFormData.getFormProperties()) {
                        aProperty.put(property.getId(), property);
                        LOG.info(String.format(
                                "Matching property id=%s:name=%s:%s:%s with fieldNames",
                                property.getId(), property.getName(), property
                                .getType().getName(), property.getValue()));
                    }
                }
            } catch (Exception e) {
                LOG.error("Error: {}, occured while looking for a form for task:{}",e.getMessage(), taskId);
                LOG.debug("FAIL:", e);
            }
        }
        try {
            FormData oTaskFormData = execution.getEngineServices()
                    .getFormService()
                    .getStartFormData(execution.getProcessDefinitionId());
            if (oTaskFormData != null && oTaskFormData.getFormProperties() != null) {
                for (FormProperty property : oTaskFormData.getFormProperties()) {
                    aProperty.put(property.getId(), property);
                    LOG.info(String.format(
                            "Matching property id=%s:name=%s:%s:%s with fieldNames",
                            property.getId(), property.getName(), property
                            .getType().getName(), property.getValue()));
                }
            }
        } catch (Exception e) {
            LOG.error("Error: {}, occured while looking for a start form for a process.", e.getMessage());
            LOG.debug("FAIL:", e);
        }
    }

    private List<String> getPreviousTaskId(DelegateExecution execution) {
        ExecutionEntity ee = (ExecutionEntity) execution;

        List<String> tasksRes = new LinkedList<String>();
        List<String> resIDs = new LinkedList<String>();

        for (FlowElement flowElement : execution.getEngineServices().getRepositoryService()
                .getBpmnModel(ee.getProcessDefinitionId()).getMainProcess().getFlowElements()) {
            if (flowElement instanceof UserTask) {
                UserTask userTask = (UserTask) flowElement;
                LOG.info("Checking user task with ID={} ", userTask.getId());
                resIDs.add(userTask.getId());

            }
        }

        for (String taskIdInBPMN : resIDs) {
            List<Task> tasks = execution.getEngineServices().getTaskService().createTaskQuery()
                    .executionId(execution.getId()).taskDefinitionKey(taskIdInBPMN).list();
            if (tasks != null) {
                for (Task task : tasks) {
                    LOG.info("Task with (ID={}, name={}, taskDefinitionKey={})", task.getId(), task.getName(), task
                            .getTaskDefinitionKey());
                    tasksRes.add(task.getId());
                }
            }
        }
        return tasksRes;
    }

    protected String getStringFromFieldExpression(Expression expression,
            DelegateExecution execution) {
        if (expression != null) {
            Object value = expression.getValue(execution);
            if (value != null) {
                return value.toString();
            }
        }
        return null;
    }

    protected Long getLongFromFieldExpression(Expression expression,
            DelegateExecution execution) {
        if (expression != null) {
            Object value = expression.getValue(execution);
            if (value != null) {
                return Long.valueOf(value.toString());
            }
        }
        return null;
    }

    protected String populatePatternWithContent(String inputText) throws IOException, URISyntaxException {
        StringBuffer outputTextBuffer = new StringBuffer();
        Matcher matcher = TAG_sPATTERN_CONTENT_COMPILED.matcher(inputText);
        while (matcher.find()) {
            matcher.appendReplacement(outputTextBuffer, getPatternContentReplacement(matcher));
        }
        matcher.appendTail(outputTextBuffer);
        return outputTextBuffer.toString();
    }

    public Mail Mail_BaseFromTask(DelegateExecution oExecution)
            throws Exception {

        String sFromMail = getStringFromFieldExpression(this.from, oExecution);
        String saToMail = getStringFromFieldExpression(this.to, oExecution);
        String sHead = getStringFromFieldExpression(this.subject, oExecution);
        String sBodySource = getStringFromFieldExpression(this.text, oExecution);
        String sBody = replaceTags(sBodySource, oExecution);

        oMail.reset();

        oMail._From(mailAddressNoreplay)._To(saToMail)._Head(sHead)
                ._Body(sBody)._AuthUser(mailServerUsername)
                ._AuthPassword(mailServerPassword)._Host(mailServerHost)
                ._Port(Integer.valueOf(mailServerPort))
                //._SSL(true)
                //._TLS(true)
                ._SSL(bSSL)
                ._TLS(bTLS)
                ;

        return oMail;
    }

    /*
     * Access modifier changed from private to default to enhance testability
     */
    String getPatternContentReplacement(Matcher matcher) throws IOException, URISyntaxException {
        String sPath = matcher.group(1);
        LOG.info("Found content group! (sPath={})", sPath);
        byte[] bytes = getFileData_Pattern(sPath);
        String sData = Tool.sData(bytes);
        LOG.debug("Loaded content from file:" + sData);
        return sData;
    }
}

