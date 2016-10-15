package org.igov.service.business.action.task.form;

import com.google.gson.Gson;
import org.activiti.engine.form.AbstractFormType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author inna
 */
public class QueueDataFormType extends AbstractFormType {

    public static final String TYPE_NAME = "queueData";
    public static final String sDate = "sDate";
    public static final String nID_FlowSlotTicket = "nID_FlowSlotTicket";
    public static final String Flow_sID_Type = "sID_Type";
    //public static final String nSlots = "nSlots";
    private static final long serialVersionUID = 1L;

    public static Map<String, Object> parseQueueData(String queueData) {
        return new Gson().fromJson(queueData, HashMap.class);
    }

    public static Long get_nID_FlowSlotTicket(Map<String, Object> queueDataMap) {
        return !queueDataMap.containsKey(nID_FlowSlotTicket) ? null : ((Number) queueDataMap.get(nID_FlowSlotTicket)).longValue();
    }
    public static String get_sID_Type(Map<String, Object> queueDataMap) {
        return !queueDataMap.containsKey(Flow_sID_Type) ? "iGov" : ((String) queueDataMap.get(Flow_sID_Type));
    }

    /*public static int get_nSlots(Map<String, Object> queueDataMap) {
        final int defaultNSlots = 1;
        return queueDataMap.containsKey(nSlots) ? ((Number) queueDataMap.get(nSlots)).intValue() : defaultNSlots;
    }*/

    public String getName() {
        return TYPE_NAME;
    }

    @Override
    public Object convertFormValueToModelValue(String propertyValue) {
        return propertyValue;
    }

    @Override
    public String convertModelValueToFormValue(Object modelValue) {
        return (String) modelValue;
    }

}