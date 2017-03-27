package org.igov.service.business.action.task.form;

import org.activiti.engine.form.AbstractFormType;

/**
 * Created by Olga.
 */
public class FileHTMLFormType extends AbstractFormType {

    public static final String TYPE_NAME = "fileHTML";

    @Override
    public String convertFormValueToModelValue(String propertyValue) {
        return propertyValue;
    }

    @Override
    public String convertModelValueToFormValue(Object modelValue) {
        return modelValue.toString();
    }

    @Override
    public String getName() {
        return TYPE_NAME;
    }
}
