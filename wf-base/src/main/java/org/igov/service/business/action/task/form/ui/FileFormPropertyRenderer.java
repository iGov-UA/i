package org.igov.service.business.action.task.form.ui;

import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;
import org.activiti.engine.form.FormProperty;
import org.activiti.explorer.ui.form.AbstractFormPropertyRenderer;
import org.igov.service.business.action.task.form.FormFileType;

public class FileFormPropertyRenderer extends AbstractFormPropertyRenderer {

    private static final long serialVersionUID = 1L;

    public FileFormPropertyRenderer() {
        super(FormFileType.class);
    }



    @Override
    public Field getPropertyField(FormProperty formProperty) {
    /*SelectFileField selectFileField = new SelectFileField(getPropertyLabel(formProperty));
    selectFileField.setRequired(formProperty.isRequired());
    selectFileField.setRequiredError(getMessage(Messages.FORM_FIELD_REQUIRED,
        getPropertyLabel(formProperty)));
    selectFileField.setEnabled(formProperty.isWritable());
 
    if (formProperty.getValue() != null)
    {
      selectFileField.setValue(formProperty.getValue());
    }
 
    return selectFileField;
  }*/
        TextField textField = new TextField(getPropertyLabel(formProperty));
        textField.setRequired(formProperty.isRequired());
        textField.setEnabled(formProperty.isWritable());
        textField.setRequiredError(getMessage("form.field.required", new Object[] { getPropertyLabel(formProperty) }));
        textField.setImmediate(true);
        textField.setWidth(Form.STRING_W.getDimension().getWidth());

        if (formProperty.getValue() != null) {
            textField.setValue(formProperty.getValue());
        }

        return textField;

    }
}