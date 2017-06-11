package org.igov.service.business.action.task.form.ui;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.activiti.engine.form.FormProperty;

import org.activiti.explorer.Messages;
import org.activiti.explorer.ui.form.AbstractFormPropertyRenderer;

import com.vaadin.ui.Field;
import com.vaadin.ui.PopupDateField;
import org.igov.service.business.action.task.form.DateFormType;

/**
 * @author Frederik Heremans
 */
public class DateFormPropertyRenderer extends AbstractFormPropertyRenderer {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

public DateFormPropertyRenderer() {
    super(DateFormType.class);
  }

  @Override
  public Field getPropertyField(FormProperty formProperty) {
    // Writable string
    PopupDateField dateField = new PopupDateField(getPropertyLabel(formProperty));
    String datePattern = (String) formProperty.getType().getInformation("datePattern");
    dateField.setDateFormat(datePattern);
    dateField.setRequired(formProperty.isRequired());
    dateField.setRequiredError(getMessage(Messages.FORM_FIELD_REQUIRED, getPropertyLabel(formProperty)));
    dateField.setEnabled(formProperty.isWritable());

    if (formProperty.getValue() != null) {
      // Try parsing the current value
      SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);

      try {
        Date date = dateFormat.parse(formProperty.getValue());
        dateField.setValue(date);
      } catch (ParseException e) {
        // TODO: what happens if current value is illegal date?
      }
    }
    return dateField;
  }
  
  @Override
  public String getFieldValue(FormProperty formProperty, Field field) {
    PopupDateField dateField = (PopupDateField) field;
    Date selectedDate = (Date) dateField.getValue();
    
    if(selectedDate != null) {
      // Use the datePattern specified in the form property type
      String datePattern = (String) formProperty.getType().getInformation("datePattern");
      SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
      return dateFormat.format(selectedDate);
    }
    
    return null;
  }

}