package org.igov.service.business.action.task.form.ui;

import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;
import org.activiti.engine.form.FormProperty;
import org.activiti.explorer.ui.form.AbstractFormPropertyRenderer;
import org.igov.service.business.action.task.form.HTMLFormType;

public class HTMLFormPropertyRenderer extends AbstractFormPropertyRenderer {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public HTMLFormPropertyRenderer() {
        super(HTMLFormType.class);
    }

    @Override
    public Field getPropertyField(FormProperty formProperty) {
        TextField htmlField = new TextField(getPropertyLabel(formProperty));
        htmlField.setRequired(formProperty.isRequired());
        htmlField.setEnabled(formProperty.isWritable());
        htmlField.setRequiredError(getMessage("form.field.required", new Object[] { getPropertyLabel(formProperty) }));
        
        htmlField.setImmediate(true);
        htmlField.setWidth(Form.HTML.getDimension().getWidth());
        htmlField.setHeight(Form.HTML.getDimension().getHeight());

        if (formProperty.getValue() != null) {
            htmlField.setValue(formProperty.getValue());
        }

        htmlField.setValidationVisible(true);
        
        return htmlField;
    }
}