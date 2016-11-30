package org.igov.service.business.action.task.systemtask.mail;

import static org.igov.io.fs.FileSystemData.getFileData_Pattern;
import static org.igov.util.ToolLuna.getProtectedNumber;
import static org.igov.service.business.action.task.core.AbstractModelTask.getStringFromFieldExpression;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.form.FormData;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.util.json.JSONObject;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.igov.io.GeneralConfig;
import org.igov.io.db.kv.statical.IBytesDataStorage;
import org.igov.io.fs.FileSystemDictonary;
import org.igov.io.mail.Mail;
import org.igov.io.sms.ManagerSMS;
import org.igov.io.sms.ManagerSMS_New;
import org.igov.io.web.HttpRequester;
import org.igov.service.business.access.AccessKeyService;
import org.igov.service.business.action.event.HistoryEventService;
import org.igov.service.business.action.task.core.AbstractModelTask;

import org.igov.service.business.action.task.core.ActionTaskService;
import org.igov.service.business.action.task.systemtask.misc.CancelTaskUtil;
import org.igov.service.business.finance.Currency;
import org.igov.service.business.finance.Liqpay;
import org.igov.service.business.object.Language;
import org.igov.service.business.place.PlaceService;
import org.igov.service.controller.security.AccessContract;
import org.igov.service.controller.security.AuthenticationTokenSelector;
import org.igov.util.Tool;
import org.igov.util.ToolWeb;
import org.igov.util.JSON.JsonDateTimeSerializer;
import org.igov.util.JSON.JsonRestUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMethod;

public abstract class Abstract_MailTaskCustom extends AbstractModelTask implements JavaDelegate {

    static final transient Logger LOG = LoggerFactory
            .getLogger(Abstract_MailTaskCustom.class);
    private static final Pattern TAG_PAYMENT_BUTTON_LIQPAY = Pattern
            .compile("\\[paymentButton_LiqPay(.*?)\\]");
    private static final Pattern TAG_sPATTERN_CONTENT_CATALOG = Pattern
            .compile("[a-zA-Z]+\\{\\[(.*?)\\]\\}");
    private static final Pattern TAG_PATTERN_PREFIX = Pattern
            .compile("_[0-9]+");
    private static final Pattern TAG_PATTERN_DOUBLE_BRACKET = Pattern
            .compile("\\{\\[(.*?)\\]\\}");
    private static final String TAG_CANCEL_TASK = "[cancelTask]";
    private static final String TAG_CANCEL_TASK_SIMPLE = "[cancelTaskSimple]";
    private static final String TAG_nID_Protected = "[nID_Protected]";
    private static final String TAG_sID_Order = "[sID_Order]";
    private static final String TAG_nID_SUBJECT = "[nID_Subject]";
    private static final String TAG_sDateCreate = "[sDateCreate]";
    // private static final String TAG_sURL_SERVICE_MESSAGE =
    // "[sURL_ServiceMessage]";
    private static final Pattern TAG_sURL_SERVICE_MESSAGE = Pattern
            .compile("\\[sURL_ServiceMessage(.*?)\\]");
    private static final Pattern TAG_sURL_FEEDBACK_MESSAGE = Pattern
            .compile("\\[sURL_FeedbackMessage(.*?)\\]");
    private static final Pattern TAG_sPATTERN_CONTENT_COMPILED = Pattern
            .compile("\\[pattern/(.*?)\\]");
    private static final String TAG_Function_AtEnum = "enum{[";
    private static final String TAG_Function_To = "]}";
    private static final String PATTERN_MERCHANT_ID = "sID_Merchant%s";
    private static final String PATTERN_SUM = "sSum%s";
    private static final String PATTERN_CURRENCY_ID = "sID_Currency%s";
    private static final String PATTERN_DESCRIPTION = "sDescription%s";
    private static final String PATTERN_SUBJECT_ID = "nID_Subject%s";

    @Autowired
    public TaskService taskService;

    @Autowired
    public HistoryService historyService;
    @Value("${general.Mail.sHost}")
    public String mailServerHost;
    @Value("${general.Mail.nPort}")
    public String mailServerPort;
    @Value("${general.Mail.sAddressDefaultFrom}")
    public String mailServerDefaultFrom;
    @Value("${general.Mail.sUsername}")
    public String mailServerUsername;
    @Value("${general.Mail.sPassword}")
    public String mailServerPassword;
    @Value("${general.Mail.sAddressNoreply}")
    public String mailAddressNoreplay;

    @Value("${general.Mail.bUseSSL}")
    private boolean bSSL;
    @Value("${general.Mail.bUseTLS}")
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
    // private static final String PATTERN_DELIMITER = "_";

    //@Autowired
    //public ManagerSMS_New oManagerSMS;
    
    @Autowired
    public ManagerSMS ManagerSMS;

    @Autowired
    AccessKeyService accessCover;
    @Autowired
    GeneralConfig generalConfig;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private HttpRequester httpRequester;
    // @Autowired
    // AccessDataService accessDataDao;
    @Autowired
    Liqpay liqBuy;
    @Autowired
    private CancelTaskUtil cancelTaskUtil;

    @Autowired
    private HistoryEventService historyEventService;
    @Autowired
    private IBytesDataStorage durableBytesDataStorage;
    @Autowired
    private PlaceService placeService;

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

        sTextReturn = new FileSystemDictonary()
                .replaceMVSTagWithValue(sTextReturn);

        Long nID_Order = getProtectedNumber(Long.valueOf(execution
                .getProcessInstanceId()));

        if (sTextReturn.contains(TAG_nID_Protected)) {
            LOG.info("TAG_nID_Protected:(nID_Order={})", nID_Order);
            sTextReturn = sTextReturn.replaceAll("\\Q" + TAG_nID_Protected
                    + "\\E", "" + nID_Order);
        }

        if (sTextReturn.contains(TAG_sID_Order)) {
            String sID_Order = generalConfig.getOrderId_ByOrder(nID_Order);
            LOG.info("TAG_sID_Order:(sID_Order={})", sID_Order);
            sTextReturn = sTextReturn.replaceAll("\\Q" + TAG_sID_Order + "\\E",
                    "" + sID_Order);
        }

        if (sTextReturn.contains(TAG_CANCEL_TASK)) {
            LOG.info("TAG_CANCEL_TASK:(nID_Protected={})", nID_Order);
            String sHTML_CancelTaskButton = cancelTaskUtil.getCancelFormHTML(
                    nID_Order, false);
            sTextReturn = sTextReturn.replace(TAG_CANCEL_TASK,
                    sHTML_CancelTaskButton);
        }

        if (sTextReturn.contains(TAG_CANCEL_TASK_SIMPLE)) {
            LOG.info("TAG_CANCEL_TASK_SIMPLE:(nID_Protected={})", nID_Order);
            String sHTML_CancelTaskButton = cancelTaskUtil.getCancelFormHTML(
                    nID_Order, true);
            sTextReturn = sTextReturn.replace(TAG_CANCEL_TASK_SIMPLE,
                    sHTML_CancelTaskButton);
        }

        if (sTextReturn.contains(TAG_nID_SUBJECT)) {
            LOG.info("TAG_nID_SUBJECT: (nID_Subject={})", nID_Subject);
            sTextReturn = sTextReturn.replaceAll("\\Q" + TAG_nID_SUBJECT
                    + "\\E", "" + nID_Subject);
        }

        if (sTextReturn.contains(TAG_sDateCreate)) {
            Date oProcessInstanceStartDate = historyService
                    .createProcessInstanceHistoryLogQuery(
                            execution.getProcessInstanceId()).singleResult()
                    .getStartTime();
            DateTimeFormatter formatter = JsonDateTimeSerializer.DATETIME_FORMATTER;
            String sDateCreate = formatter.print(oProcessInstanceStartDate
                    .getTime());
            LOG.info("TAG_sDateCreate: (sDateCreate={})", sDateCreate);
            sTextReturn = sTextReturn.replaceAll("\\Q" + TAG_sDateCreate
                    + "\\E", "" + sDateCreate);
        }

        sTextReturn = replaceTags_sURL_SERVICE_MESSAGE(sTextReturn, execution,
                nID_Order);
        sTextReturn = replaceTags_sURL_FEEDBACK_MESSAGE(sTextReturn, execution,
                nID_Order);

        return sTextReturn;
    }

    private String replaceTags_Enum(String textWithoutTags,
            DelegateExecution execution) {
        List<String> previousUserTaskId = getPreviousTaskId(execution);
        int nLimit = StringUtils.countMatches(textWithoutTags,
                TAG_Function_AtEnum);
        LOG.info("Found {} enum occurrences in the text ", nLimit);
        Map<String, FormProperty> aProperty = new HashMap<>();
        int foundIndex = 0;
        while (nLimit > 0) {
            nLimit--;
            int nAt = textWithoutTags.indexOf(TAG_Function_AtEnum, foundIndex);
            LOG.info("sTAG_Function_AtEnum, (nAt={})", nAt);
            foundIndex = nAt + 1;
            int nTo = textWithoutTags.indexOf(TAG_Function_To, foundIndex);
            foundIndex = nTo + 1;
            LOG.info("sTAG_Function_ToEnum,(nTo={})", nTo);
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
                                    snID, property.getName(), sType,
                                    property.getValue()));

                    Object variable = execution.getVariable(property.getId());
                    if (variable != null) {
                        String sID_Enum = variable.toString();
                        LOG.info("execution.getVariable()(sID_Enum={})",
                                sID_Enum);
                        String sValue = ActionTaskService.parseEnumProperty(
                                property, sID_Enum);
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

    private String replaceTags_Catalog(String textStr,
            DelegateExecution execution) throws Exception {
        StringBuffer outputTextBuffer = new StringBuffer();
        String replacement = "";
        Matcher matcher = TAG_sPATTERN_CONTENT_CATALOG.matcher(textStr);
        if (matcher.find()) {
            matcher = TAG_sPATTERN_CONTENT_CATALOG.matcher(textStr);
            List<String> aPreviousUserTask_ID = getPreviousTaskId(execution);
            Map<String, FormProperty> mProperty = new HashMap<>();
            loadPropertiesFromTasks(execution, aPreviousUserTask_ID, mProperty);
            while (matcher.find()) {
                String tag_Payment_CONTENT_CATALOG = matcher.group();
                LOG.info("Found tag catalog group:{}", matcher.group());
                if (!tag_Payment_CONTENT_CATALOG
                        .startsWith(TAG_Function_AtEnum)) {
                    String prefix;
                    Matcher matcherPrefix = TAG_PATTERN_DOUBLE_BRACKET
                            .matcher(tag_Payment_CONTENT_CATALOG);
                    if (matcherPrefix.find()) {
                        prefix = matcherPrefix.group();
                        LOG.info("Found double bracket tag group: {}",
                                matcherPrefix.group());
                        String form_ID = StringUtils.replace(prefix, "{[", "");
                        form_ID = StringUtils.replace(form_ID, "]}", "");
                        LOG.info("(form_ID={})", form_ID);
                        FormProperty formProperty = mProperty.get(form_ID);
                        LOG.info("Found form property : {}", formProperty);
                        if (formProperty != null) {
                            if (formProperty.getValue() != null) {
                                replacement = formProperty.getValue();
                            } else {
                                List<String> aID = new ArrayList<>();
                                aID.add(formProperty.getId());
                                List<String> proccessVariable = AbstractModelTask
                                        .getVariableValues(execution, aID);
                                LOG.info("(proccessVariable={})",
                                        proccessVariable);
                                if (!proccessVariable.isEmpty()
                                        && proccessVariable.get(0) != null) {
                                    replacement = proccessVariable.get(0);
                                }
                            }

                            String sType = formProperty.getType().getName();
                            if ("date".equals(sType)) {
                                if (formProperty.getValue() != null) {
                                    LOG.info(
                                            "formProperty.getValue() getFormattedDateS : {}",
                                            formProperty.getValue());
                                    replacement = getFormattedDateS(formProperty
                                            .getValue());
                                }
                            } else {
                                //replacement = formProperty.getValue();
                            }

                        }
                    }
                }
                LOG.info("Replacement for pattern : {}", replacement);
                matcher.appendReplacement(outputTextBuffer, replacement);
                replacement = "";
            }
        }
        return matcher.appendTail(outputTextBuffer).toString();
    }

    private String replaceTags_LIQPAY(String textStr,
            DelegateExecution execution) throws Exception {
        String LIQPAY_CALLBACK_URL = generalConfig.getSelfHost()
                + "/wf/service/finance/setPaymentStatus_TaskActiviti?sID_Order=%s&sID_PaymentSystem=Liqpay&sData=%s&sPrefix=%s";

        StringBuffer outputTextBuffer = new StringBuffer();
        Matcher matcher = TAG_PAYMENT_BUTTON_LIQPAY.matcher(textStr);
        while (matcher.find()) {

            String tag_Payment_Button_Liqpay = matcher.group();
            String prefix = "";
            Matcher matcherPrefix = TAG_PATTERN_PREFIX
                    .matcher(tag_Payment_Button_Liqpay);
            if (matcherPrefix.find()) {
                prefix = matcherPrefix.group();
            }

            String pattern_merchant = String
                    .format(PATTERN_MERCHANT_ID, prefix);
            String pattern_sum = String.format(PATTERN_SUM, prefix);
            String pattern_currency = String
                    .format(PATTERN_CURRENCY_ID, prefix);
            String pattern_description = String.format(PATTERN_DESCRIPTION,
                    prefix);
            String pattern_subject = String.format(PATTERN_SUBJECT_ID, prefix);

            String sID_Merchant = execution.getVariable(pattern_merchant) != null ? execution
                    .getVariable(pattern_merchant).toString() : execution
                    .getVariable(String.format(PATTERN_MERCHANT_ID, ""))
                    .toString();
            LOG.info("{}={}", pattern_merchant, sID_Merchant);
            String sSum = execution.getVariable(pattern_sum) != null ? execution
                    .getVariable(pattern_sum).toString() : execution
                    .getVariable(String.format(PATTERN_SUM, "")).toString();
            LOG.info("{}={}", pattern_sum, sSum);
            if (sSum != null) {
                sSum = sSum.replaceAll(",", ".");
            }
            String sID_Currency = execution.getVariable(pattern_currency) != null ? execution
                    .getVariable(pattern_currency).toString() : execution
                    .getVariable(String.format(PATTERN_CURRENCY_ID, ""))
                    .toString();
            LOG.info("{}={}", pattern_currency, sID_Currency);
            Currency oID_Currency = Currency
                    .valueOf(sID_Currency == null ? "UAH" : sID_Currency);

            Language sLanguage = Liqpay.DEFAULT_LANG;
            String sDescription = execution.getVariable(pattern_description) != null ? execution
                    .getVariable(pattern_description).toString() : execution
                    .getVariable(String.format(PATTERN_DESCRIPTION, ""))
                    .toString();
            LOG.info("{}={}", pattern_description, sDescription);

            String sID_Order = "TaskActiviti_" + execution.getId().trim()
                    + prefix;
            String sURL_CallbackStatusNew = String.format(LIQPAY_CALLBACK_URL,
                    sID_Order, "", prefix);
            String sURL_CallbackPaySuccess = null;
            Long nID_Subject = Long.valueOf(execution
                    .getVariable(pattern_subject) != null ? execution
                    .getVariable(pattern_subject).toString() : execution
                    .getVariable(String.format(PATTERN_SUBJECT_ID, ""))
                    .toString());
            nID_Subject = (nID_Subject == null ? 0 : nID_Subject);
            LOG.info("{}={}", pattern_subject, nID_Subject);
            boolean bTest = generalConfig.isTest_LiqPay();
            String htmlButton = liqBuy.getPayButtonHTML_LiqPay(sID_Merchant,
                    sSum, oID_Currency, sLanguage, sDescription, sID_Order,
                    sURL_CallbackStatusNew, sURL_CallbackPaySuccess,
                    nID_Subject, bTest);
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
            Matcher matcherPrefix = TAG_PATTERN_PREFIX
                    .matcher(tag_sURL_SERVICE_MESSAGE);
            if (matcherPrefix.find()) {
                prefix = matcherPrefix.group();
            }
            String URL_SERVICE_MESSAGE = generalConfig.getSelfHostCentral()
                    + "/wf/service/subject/message/setMessageRate";

            String sURI = ToolWeb.deleteContextFromURL(URL_SERVICE_MESSAGE);
            /*
			 * ProcessDefinition processDefinition =
			 * execution.getEngineServices()
			 * .getRepositoryService().createProcessDefinitionQuery()
			 * .processDefinitionId(execution.getProcessDefinitionId())
			 * .singleResult();
             */

            String sQueryParamPattern = "?"
                    // + "sHead=Отзыв" + "&sMail= " + "&sData="
                    // + (processDefinition != null &&
                    // processDefinition.getName() != null ?
                    // processDefinition.getName().trim() : "")
                    + "&sID_Rate="
                    + prefix.replaceAll("_", "")
                    // + "&nID_SubjectMessageType=1" + "&nID_Protected="+
                    // nID_Protected
                    + "&sID_Order="
                    + generalConfig.getOrderId_ByOrder(nID_Order);

            String sQueryParam = String.format(sQueryParamPattern);
            if (nID_Subject != null) {
                sQueryParam = sQueryParam + "&nID_Subject=" + nID_Subject;
            }
            sQueryParam = sQueryParam
                    // TODO: Need remove in future!!!
                    + "&" + AuthenticationTokenSelector.ACCESS_CONTRACT + "="
                    + AccessContract.RequestAndLoginUnlimited.name();
            LOG.info("(sURI={},{})", sURI, sQueryParam);
            String sAccessKey = accessCover.getAccessKeyCentral(sURI
                    + sQueryParam, AccessContract.RequestAndLoginUnlimited);
            String replacemet = URL_SERVICE_MESSAGE + sQueryParam + "&"
                    + AuthenticationTokenSelector.ACCESS_KEY + "=" + sAccessKey;
            LOG.info("(replacemet URL={}) ", replacemet);
            matcher.appendReplacement(outputTextBuffer, replacemet);
        }
        return matcher.appendTail(outputTextBuffer).toString();
    }

    //TODO: Допилить и начать использовать PlaceServiceImpl вместо этого
    /*private String getPlaceByProcess(String sID_Process) {
        Map<String, String> mParam = new HashMap<String, String>();
        mParam.put("nID_Process", sID_Process);
        //LOG.info("2sID_Process: " + sID_Process);
        mParam.put("nID_Server", generalConfig.getSelfServerId().toString());
        //LOG.info("3generalConfig.getSelfServerId().toString(): " + generalConfig.getSelfServerId().toString());
        String sURL = generalConfig.getSelfHostCentral() + "/wf/service/object/place/getPlaceByProcess";
        //LOG.info("ssURL: " + sURL);
        LOG.info("(sURL={},mParam={})", sURL, mParam);
        String soResponse = null;
        String sName = null;
        try {
            soResponse = httpRequester.getInside(sURL, mParam);
            LOG.info("soResponse={}", soResponse);
            Map mReturn = JsonRestUtils.readObject(soResponse, Map.class);
            LOG.info("mReturn={}" + mReturn);
            sName = (String) mReturn.get("sName");
            LOG.info("sName={}", sName);
        } catch (Exception ex) {
            LOG.error("", ex);
        }
        //LOG.info("(soResponse={})", soResponse);
        return sName;//soResponse
    }*/

    private String replaceTags_sURL_FEEDBACK_MESSAGE(String textWithoutTags,
            DelegateExecution execution, Long nID_Order) throws Exception {

        StringBuffer outputTextBuffer = new StringBuffer();
        Matcher oMatcher = TAG_sURL_FEEDBACK_MESSAGE.matcher(textWithoutTags);
        while (oMatcher.find()) {
            String tag_sURL_FEEDBACK_MESSAGE = oMatcher.group();
            List<String> aPreviousUserTask_ID = getPreviousTaskId(execution);
            Map<String, FormProperty> mProperty = new HashMap<>();
            loadPropertiesFromTasks(execution, aPreviousUserTask_ID, mProperty);

            String sAuthorMail = "";
            String sAuthorLastName = "";
            String sAuthorFirstName = "";
            String sAuthorMiddleName = "";
            String sAuthorFIO = "";
            String sAuthorFIO_Original = "";

            //FormProperty oFormProperty = null;
            String sPrefix = "";
            Matcher oMatcherPrefix = TAG_PATTERN_PREFIX.matcher(tag_sURL_FEEDBACK_MESSAGE);
            if (oMatcherPrefix.find()) {
                sPrefix = oMatcherPrefix.group();
                LOG.info("Found double bracket tag group: {}", sPrefix);
                /*String form_ID = StringUtils.replace(sPrefix, "{[", "");
                                form_ID = StringUtils.replace(form_ID, "]}", "");
                                LOG.info("(form_ID={})", form_ID);
                                oFormProperty = mProperty.get(form_ID);*/
            }

            for (Entry<String, FormProperty> oFormPropertyEntry : mProperty.entrySet()) {
                FormProperty oFormProperty = oFormPropertyEntry.getValue();

                if (oFormProperty != null) {
                    String sID = oFormProperty.getId();
                    //LOG.info("(id={})", id);
                    if ("email".equalsIgnoreCase(sID) && oFormProperty.getValue() != null && !"null".equalsIgnoreCase(oFormProperty.getValue())) {
                        sAuthorMail = oFormProperty.getValue();
                    }
                    if ("bankIdlastName".equalsIgnoreCase(sID) && oFormProperty.getValue() != null && !"null".equalsIgnoreCase(oFormProperty.getValue())) {
                        sAuthorLastName = oFormProperty.getValue();
                    }
                    if ("bankIdfirstName".equalsIgnoreCase(sID) && oFormProperty.getValue() != null && !"null".equalsIgnoreCase(oFormProperty.getValue())) {
                        sAuthorFirstName = oFormProperty.getValue();
                    }
                    if ("bankIdmiddleName".equalsIgnoreCase(sID) && oFormProperty.getValue() != null && !"null".equalsIgnoreCase(oFormProperty.getValue())) {
                        sAuthorMiddleName = oFormProperty.getValue();
                    }
                    if ("clFIO".equalsIgnoreCase(sID) && oFormProperty.getValue() != null && !"null".equalsIgnoreCase(oFormProperty.getValue())) {
                        sAuthorFIO_Original = oFormProperty.getValue();
                        //LOG.info("(sAuthorFIO_Original={})", sAuthorFIO_Original);

                    }
                }
            }

            if (sAuthorFIO_Original != null && !"".equals(sAuthorFIO_Original.trim())) {
                String[] as = sAuthorFIO_Original.split("\\ ");
                //LOG.info("(as={})", as);
                if (as.length > 0 && (sAuthorLastName == null || "".equals(sAuthorLastName.trim()))) {
                    sAuthorLastName = as[0];
                    //LOG.info("(as[0]={})", as[0]);
                }
                if (as.length > 1 && (sAuthorFirstName == null || "".equals(sAuthorFirstName.trim()))) {
                    sAuthorFirstName = as[1];
                    //LOG.info("(as[1]={})", as[1]);
                }
                if (as.length > 2 && (sAuthorMiddleName == null || "".equals(sAuthorMiddleName.trim()))) {
                    sAuthorMiddleName = as[2];
                    //LOG.info("(as[2]={})", as[2]);
                }
                //sAuthorFIO_Original = bankIdlastName + " " + bankIdfirstName + " " + bankIdmiddleName;
                //sAuthorFIO_Original=sAuthorFIO_Original.substring(0,1)+".";
            }
            if (sAuthorFirstName != null && !"".equals(sAuthorFirstName.trim())) {
                //bankIdfirstName=bankIdfirstName.substring(0,1)+".";
            }
            if (sAuthorMiddleName != null && !"".equals(sAuthorMiddleName.trim())) {
                //bankIdmiddleName=bankIdmiddleName.substring(0,1)+".";
            }
            //sAuthorFIO = bankIdlastName + " " + bankIdfirstName + " " + bankIdmiddleName;
            sAuthorFIO = sAuthorFirstName + " " + sAuthorMiddleName;

            //LOG.info("(sAuthorFIO={})", sAuthorFIO);
            String sPlace = "";
            String sID_Place_UA = "";
            Long nID_Service = 0L;
            try {
                String jsonHistoryEvent = historyEventService.getHistoryEvent(generalConfig.getOrderId_ByOrder(nID_Order));
                LOG.info("get history event for bp: (jsonHistoryEvent={})", jsonHistoryEvent);
                JSONObject historyEvent = new JSONObject(jsonHistoryEvent);
                nID_Service = historyEvent.getLong("nID_Service");
                sID_Place_UA = historyEvent.getString("sID_UA");
                String snID_Process = execution.getProcessInstanceId();
                sPlace = placeService.getPlaceByProcess(snID_Process);
            } catch (Exception oException) {
                LOG.error("ex!: {}", oException.getMessage());
                LOG.debug("FAIL:", oException);
            }

            String sURL_FEEDBACK_MESSAGE = generalConfig.getSelfHostCentral()
                    + "/wf/service/subject/message/setFeedbackExternal";
            String sURI = ToolWeb.deleteContextFromURL(sURL_FEEDBACK_MESSAGE);
            String sQueryParamPattern = "?"
                    + "&sID_Source=" + "self"
                    + "&sAuthorFIO=" + sAuthorFIO
                    + "&sPlace=" + sPlace
                    + "&sID_Place_UA=" + sID_Place_UA
                    + "&sMail=" + sAuthorMail
                    + "&sBody=" + ""
                    + "&nID_Rate=" + sPrefix.replaceAll("_", "")
                    + "&nID_Service=" + nID_Service
                    + "&bSelf=" + true;

            String sQueryParam = String.format(sQueryParamPattern);
            if (nID_Subject != null) {
                sQueryParam = sQueryParam + "&nID_Subject=" + nID_Subject;
            }
            String sID_Order = generalConfig.getOrderId_ByProcess(Long.valueOf(execution.getProcessInstanceId()));
            if (sID_Order != null) {
                sQueryParam = sQueryParam + "&sID_Order=" + sID_Order;
            }
            sQueryParam = sQueryParam
                    // TODO: Need remove in future!!!
                    + "&" + AuthenticationTokenSelector.ACCESS_CONTRACT + "="
                    + AccessContract.RequestAndLoginUnlimited.name();
//					+ AccessContract.RequestAndLogin.name();
            LOG.info("(sURI={},{})", sURI, sQueryParam);
            String sAccessKey = accessCover.getAccessKeyCentral(sURI
                    + sQueryParam, AccessContract.RequestAndLoginUnlimited);
//					+ sQueryParam, AccessContract.RequestAndLogin);
            String sReplacemet = sURL_FEEDBACK_MESSAGE + sQueryParam + "&"
                    + AuthenticationTokenSelector.ACCESS_KEY + "=" + sAccessKey;
            LOG.info("(replacemet URL={}) ", sReplacemet);
            oMatcher.appendReplacement(outputTextBuffer, sReplacemet);
        }
        return oMatcher.appendTail(outputTextBuffer).toString();
    }

    private void loadPropertiesFromTasks(DelegateExecution oDelegateExecution,
            List<String> asID_UserTaskPrevious, Map<String, FormProperty> aFormPropertyReturn) {
        LOG.info("(execution.getId()={})", oDelegateExecution.getId());
        LOG.info("(execution.getProcessDefinitionId()={})",
                oDelegateExecution.getProcessDefinitionId());
        LOG.info("(execution.getProcessInstanceId()={})",
                oDelegateExecution.getProcessInstanceId());
        String[] as = oDelegateExecution.getProcessDefinitionId().split("\\:");
        String s = as[2];
        LOG.info("(s={})", s);

        for (String sID_UserTaskPrevious : asID_UserTaskPrevious) {
            try {
                FormData oFormData = null;
                if (asID_UserTaskPrevious != null && !asID_UserTaskPrevious.isEmpty()) {
                    oFormData = oDelegateExecution.getEngineServices()
                            .getFormService().getTaskFormData(sID_UserTaskPrevious);
                }

                if (oFormData != null && oFormData.getFormProperties() != null) {
                    for (FormProperty oFormProperty : oFormData.getFormProperties()) {
                        aFormPropertyReturn.put(oFormProperty.getId(), oFormProperty);
                        LOG.info("Matching property (Id={},Name={},Type={},Value={})",
                                oFormProperty.getId(), oFormProperty.getName(),
                                oFormProperty.getType().getName(),
                                oFormProperty.getValue());
                    }
                }
            } catch (Exception e) {
                LOG.error(
                        "Error: {}, occured while looking for a form for task:{}",
                        e.getMessage(), sID_UserTaskPrevious);
                LOG.debug("FAIL:", e);
            }
        }
        try {
            FormData oTaskFormData = oDelegateExecution.getEngineServices()
                    .getFormService()
                    .getStartFormData(oDelegateExecution.getProcessDefinitionId());
            if (oTaskFormData != null
                    && oTaskFormData.getFormProperties() != null) {
                for (FormProperty oFormProperty : oTaskFormData.getFormProperties()) {
                    aFormPropertyReturn.put(oFormProperty.getId(), oFormProperty);
                    LOG.info("Matching property (Id={},Name={},Type={},Value={})",
                            oFormProperty.getId(), oFormProperty.getName(),
                            oFormProperty.getType().getName(),
                            oFormProperty.getValue());
                }
            }
        } catch (Exception e) {
            LOG.error(
                    "Error: {}, occured while looking for a start form for a process.",
                    e.getMessage());
            LOG.debug("FAIL:", e);
        }
    }

    private List<String> getPreviousTaskId(DelegateExecution execution) {
        ExecutionEntity ee = (ExecutionEntity) execution;

        List<String> tasksRes = new LinkedList<>();
        List<String> resIDs = new LinkedList<>();

        for (FlowElement flowElement : execution.getEngineServices()
                .getRepositoryService()
                .getBpmnModel(ee.getProcessDefinitionId()).getMainProcess()
                .getFlowElements()) {
            if (flowElement instanceof UserTask) {
                UserTask userTask = (UserTask) flowElement;
                LOG.info("Checking user task with ID={} ", userTask.getId());
                resIDs.add(userTask.getId());

            }
        }

        for (String taskIdInBPMN : resIDs) {
            List<Task> tasks = execution.getEngineServices().getTaskService()
                    .createTaskQuery().executionId(execution.getId())
                    .taskDefinitionKey(taskIdInBPMN).list();
            if (tasks != null) {
                for (Task task : tasks) {
                    LOG.info(
                            "Task with (ID={}, name={}, taskDefinitionKey={})",
                            task.getId(), task.getName(),
                            task.getTaskDefinitionKey());
                    tasksRes.add(task.getId());
                }
            }
        }
        return tasksRes;
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

    protected String populatePatternWithContent(String inputText)
            throws IOException, URISyntaxException {
        StringBuffer outputTextBuffer = new StringBuffer();
        Matcher matcher = TAG_sPATTERN_CONTENT_COMPILED.matcher(inputText);
        while (matcher.find()) {
            matcher.appendReplacement(outputTextBuffer,
                    getPatternContentReplacement(matcher));
        }
        matcher.appendTail(outputTextBuffer);
        return outputTextBuffer.toString();
    }

    public Mail Mail_BaseFromTask(DelegateExecution oExecution)
            throws Exception {

        String sFromMail = getStringFromFieldExpression(from, oExecution);
        String saToMail = getStringFromFieldExpression(to, oExecution);
        String sHead = getStringFromFieldExpression(subject, oExecution);
        String sBodySource = getStringFromFieldExpression(text, oExecution);
        String sBody = replaceTags(sBodySource, oExecution);

        saveServiceMessage(sHead, saToMail, sBody,
                generalConfig.getOrderId_ByProcess(Long.valueOf(oExecution
                        .getProcessInstanceId())));

        Mail oMail = context.getBean(Mail.class);

        oMail._From(mailAddressNoreplay)._To(saToMail)._Head(sHead)
                ._Body(sBody)._AuthUser(mailServerUsername)
                ._AuthPassword(mailServerPassword)._Host(mailServerHost)
                ._Port(Integer.valueOf(mailServerPort))
                // ._SSL(true)
                // ._TLS(true)
                ._SSL(bSSL)._TLS(bTLS);

        return oMail;
    }

    /*
	 * Access modifier changed from private to default to enhance testability
     */
    String getPatternContentReplacement(Matcher matcher) throws IOException,
            URISyntaxException {
        String sPath = matcher.group(1);
        LOG.info("Found content group! (sPath={})", sPath);
        byte[] bytes = getFileData_Pattern(sPath);
        String sData = Tool.sData(bytes);
        LOG.debug("Loaded content from file:" + sData);
        return sData;
    }

    private String getFormattedDate(Date date) {
        if (date == null) {
            return StringUtils.EMPTY;
        }

        Calendar oCalendar = Calendar.getInstance();
        oCalendar.setTime(date);
        SimpleDateFormat oSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return oSimpleDateFormat.format(oCalendar.getTime());
    }

    private String getFormattedDateS(String date) {
        DateTimeFormatter dateStringFormat = DateTimeFormat
                .forPattern("dd/MM/yyyy");
        DateTime dateTime = dateStringFormat.parseDateTime(date);
        Date d = dateTime.toDate();
        return getFormattedDate(d);
    }

    @Override
    public void execute(DelegateExecution oExecution) throws Exception {
    }

    protected void saveServiceMessage(String sHead, String sTo, String sBody,
            String sID_Order) {

        if (sBody != null && sBody.contains("Шановний колего!")) {
            //Не сохраняем в истории заявки гражданина письмо чиновнику //Юлия
            return;
        }

        final Map<String, String> params = new HashMap<>();
        params.put("sID_Order", sID_Order);
        params.put("sHead", "Відправлено листа");
        params.put("sBody", sHead);
        params.put("sMail", sTo);
        params.put("nID_SubjectMessageType", "" + 10L);
        params.put("nID_Subject", "0");
        params.put("sContacts", "0");
        params.put("sData", "0");
        params.put("RequestMethod", RequestMethod.GET.name());
        String key;
        key = durableBytesDataStorage.saveData(sBody.getBytes(Charset
                .forName("UTF-8")));
        params.put("sID_DataLink", key);

        ScheduledExecutorService executor = Executors
                .newSingleThreadScheduledExecutor();
        Runnable task = new Runnable() {

            @Override
            public void run() {
                LOG.info(
                        "try to save service message with params with a delay: (params={})",
                        params);
                String jsonServiceMessage;
                try {
                    jsonServiceMessage = historyEventService
                            .addServiceMessage(params);
                    LOG.info("(jsonServiceMessage={})", jsonServiceMessage);
                } catch (Exception e) {
                    LOG.error("( saveServiceMessage error={})", e.getMessage());
                }
            }
        };
        // run saving message in 10 seconds so history event will be in the
        // database already by that time
        executor.schedule(task, 10, TimeUnit.SECONDS);
        executor.shutdown();

        LOG.info(
                "Configured thread to run in 10 seconds with params: (params={})",
                params);
    }

}
