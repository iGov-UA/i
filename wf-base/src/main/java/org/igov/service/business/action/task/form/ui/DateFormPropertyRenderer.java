package org.igov.service.business.action.task.form.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.activiti.engine.form.FormProperty;
import org.activiti.engine.impl.form.DateFormType;
import org.activiti.explorer.Messages;
import org.activiti.explorer.ui.form.AbstractFormPropertyRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Field;
import com.vaadin.ui.PopupDateField;

public class DateFormPropertyRenderer extends AbstractFormPropertyRenderer {
	
	private static final Logger LOG = LoggerFactory.getLogger(DateFormPropertyRenderer.class);

	  public DateFormPropertyRenderer() {
	    super(DateFormType.class);
	  }

	  @Override
	  public Field getPropertyField(FormProperty formProperty) {
	    // Writable string
	    PopupDateField dateField = new PopupDateField(getPropertyLabel(formProperty));
	    String datePattern = "dd MMMM yyyy";
	    dateField.setDateFormat(datePattern);
	    dateField.setRequired(formProperty.isRequired());
	    dateField.setRequiredError(getMessage(Messages.FORM_FIELD_REQUIRED, getPropertyLabel(formProperty)));
	    dateField.setEnabled(formProperty.isWritable());

	    if (formProperty.getValue() != null) {
	      // Try parsing the current value
	    	SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern, new Locale("uk"));

	        String date = dateFormat.format(formProperty.getValue());
	        dateField.setValue(date);
	    }
	    LOG.info("DateFormPropertyRenderer getPropertyField>>>> " + dateField);
	    return dateField;
	  }
	  
	  @Override
	  public String getFieldValue(FormProperty formProperty, Field field) {
	    PopupDateField dateField = (PopupDateField) field;
	    Date selectedDate = (Date) dateField.getValue();
	    LOG.info("DateFormPropertyRenderer getFieldValue>>>> " + selectedDate);
	    
	    if(selectedDate != null) {
	      // Use the datePattern specified in the form property type
	     // String datePattern = (String) formProperty.getType().getInformation("datePattern");
	     // SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
	    	SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("uk"));
	    	String dateRes = dateFormat.format(selectedDate);
	    	 LOG.info("DateFormPropertyRenderer getFieldValue>>>> " + dateRes);
	      return dateRes;
	    }
	    
	    return null;
	  }

	}