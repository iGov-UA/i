package org.igov.model.action.event;

import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class HistoryEventMessage {
    public static final String SERVICE_NAME = "%Назва послуги%";
    public static final String SERVICE_STATE = "%статус%";
    public static final String TASK_NUMBER = "%nTask%";
    public static final String FIO = "%Ім’я того, кому надають доступ%";
    public static final String TELEPHONE = "%телефон%";
    public static final String DOCUMENT_TYPE = "%Тип документу%";
    public static final String DOCUMENT_NAME = "%Назва документу%";
    public static final String ORGANIZATION_NAME = "%Назва органу%";
    public static final String EMAIL = "%email%";
    public static final String DAYS = "%кількість днів%";
    public static final String TABLE_BODY = "%tableBody%";
    public static final String S_BODY = "%sBody%";
    public static final String OLD_DATA = "%oldData%";
    public static final String NEW_DATA = "%newData%";
    public static final String ORDER_ID = "%sID_Order%";
    public static final String LINKED_ORDER_ID = "%sID_Order_Link%";
    public static final String BP_ID = "%sID_BP%";
    public static final String LOGIN = "%sLogin%";
    public static final String PIP = "%ПІБ того хто викликав сервiс%";
    
    private static final Logger LOG = LoggerFactory.getLogger(HistoryEventMessage.class);

    public static String createJournalMessage(HistoryEventType eventType, Map<String, String> values) {
        LOG.info("Method createJournalMessage started");
        String eventMessage = "";
        LOG.info("Method createJournalMessage map: "+values);
        try {
            eventMessage = eventType.getsTemplate();
            LOG.info("Template: "+eventMessage);
            LOG.info("Starting loop");
            for (String key : values.keySet()) {
                LOG.info("Inside loop key: "+key+" value: "+values.get(key));
                eventMessage = eventMessage.replaceAll(key, values.get(key));
                LOG.info("Changed eventMessage: "+eventMessage);
            }
            LOG.info("Loop ended");
        } catch (Exception e) {
            LOG.warn("error : {}", e.getMessage());
            LOG.trace("FAIL:", e);
        }
        return eventMessage;
    }

}