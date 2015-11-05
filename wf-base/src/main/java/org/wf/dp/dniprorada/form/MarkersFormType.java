package org.wf.dp.dniprorada.form;

import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.form.AbstractFormType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.wf.dp.dniprorada.engine.task.MarkerService;

/**
 * Created by Dmytro Tsapko on 5/24/2015.
 */
public class MarkersFormType extends AbstractFormType {
	private static final long serialVersionUID = 1L;
	public static final String TYPE_NAME = "markers";

	private final Logger log = LoggerFactory.getLogger(MarkersFormType.class);
	@Override
	public Object convertFormValueToModelValue(String propertyValue) {
		throw new ActivitiIllegalArgumentException("invalid action. Marker fields must have tag writable='false'");
	}

	@Override
	public String convertModelValueToFormValue(Object modelValue) {	
		if (modelValue == null) {
			return null;
		}
		return modelValue.toString();
	}

	@Override
	public String getName() {
		return TYPE_NAME;
	}

	public String getMimeType() {
		return "plain/text";
	}
}
