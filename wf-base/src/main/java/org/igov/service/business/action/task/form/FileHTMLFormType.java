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
        String sReturn = null;
        try{
            sReturn=modelValue==null?null:modelValue.toString();
        }catch(Exception oException){
            throw new RuntimeException("Can't convert Value of type "+TYPE_NAME+". Error: "+oException.getMessage());
        }
        return sReturn;
    }

    @Override
    public String getName() {
        return TYPE_NAME;
    }
}
