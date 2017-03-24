package org.igov.service.business.action.task.form;

import org.activiti.engine.form.AbstractFormType;

/**
 * Created by Olga.
 */
public class HTMLFormType extends AbstractFormType {

    public static final String TYPE_NAME = "html";

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
