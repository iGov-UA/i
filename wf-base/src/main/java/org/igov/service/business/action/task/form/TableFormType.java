package org.igov.service.business.action.task.form;

import org.activiti.engine.form.AbstractFormType;

/**
 * Created by Belyavtsev Vladimir (BW)).
 */
public class TableFormType extends AbstractFormType {

    private static final long serialVersionUID = 1L;
    public static final String TYPE_NAME = "table";

    @Override
    public Object convertFormValueToModelValue(String propertyValue) {
		// throw new
        // ActivitiIllegalArgumentException("invalid action. Marker fields must have tag writable='false'");
        return propertyValue;
    }

    @Override
    public String convertModelValueToFormValue(Object modelValue) {
        if (modelValue == null) {
            return null;
        }
        return modelValue.toString();
    }

    @Override
    public String getName() {
        return TYPE_NAME;
    }

    public String getMimeType() {
        return "plain/text";
    }
}
