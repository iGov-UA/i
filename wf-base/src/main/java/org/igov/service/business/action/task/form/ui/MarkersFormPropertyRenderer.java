package org.igov.service.business.action.task.form.ui;

import org.activiti.engine.form.FormProperty;
import org.activiti.explorer.ui.form.AbstractFormPropertyRenderer;
import org.igov.service.business.action.task.form.MarkersFormType;

import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;

/**
 * Created by user on 5/24/2015.
 */
public class MarkersFormPropertyRenderer extends AbstractFormPropertyRenderer {

    private static final long serialVersionUID = 1L;

    public MarkersFormPropertyRenderer() {
        super(MarkersFormType.class);
    }

    @Override
    public Field getPropertyField(FormProperty formProperty) {
        TextField textField = new TextField(getPropertyLabel(formProperty));
        textField.setRequired(formProperty.isRequired());
        textField.setEnabled(formProperty.isWritable());
        textField.setRequiredError(getMessage("form.field.required", new Object[] {getPropertyLabel(formProperty)}));
        textField.setImmediate(true);
        textField.setWidth(Form.STRING_W.getDimension().getWidth());

        if (formProperty.getValue() != null) {
            textField.setValue(formProperty.getValue());
        }

        return textField;
    }
}
