package org.igov.service.business.action.task.form;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.form.AbstractFormType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;


/**
 * @author Tom Baeyens
 */
public class DateFormType extends AbstractFormType {
  
  private static final long serialVersionUID = 1L;
  
  protected String datePattern; 
  protected Format dateFormat; 

  public DateFormType(String datePattern) {
    this.datePattern = datePattern;
    this.dateFormat = FastDateFormat.getInstance(datePattern);
  }
  
  public String getName() {
    return "date";
  }
  
  public Object getInformation(String key) {
    if ("datePattern".equals(key)) {
      return datePattern;
    }
    return null;
  }

  public Object convertFormValueToModelValue(String propertyValue) {
	  DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
    if (StringUtils.isEmpty(propertyValue)) {
      return null;
    }
    try {
      return dateFormat.parseObject(propertyValue);
    } catch (ParseException e) {
    	try {
			return df.parse(propertyValue);
		} catch (ParseException e1) {
			throw new ActivitiIllegalArgumentException("invalid date value "+propertyValue);
		}
    }
  }
  
  public String convertModelValueToFormValue(Object modelValue) {
    if (modelValue == null) {
      return null;
    }
    return dateFormat.format(modelValue);
  }
}