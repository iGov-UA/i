package org.igov.service.business.action.task.form;

import org.activiti.engine.form.AbstractFormType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author inna
 */
public class InvisibleFormType extends AbstractFormType {

    public static final String TYPE_NAME = "invisible";
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final transient Logger LOG = LoggerFactory.getLogger(InvisibleFormType.class);
    
    public String getName() {
        return TYPE_NAME;
    }

    @Override
    public Object convertFormValueToModelValue(String propertyValue) {
        return propertyValue;
    }

    @Override
    public String convertModelValueToFormValue(Object modelValue) {
        String s=null;
        try{
            s = (String) modelValue;
        }catch(Exception oException){
            LOG.error(oException.getMessage());
        }
        return s;
    }
}