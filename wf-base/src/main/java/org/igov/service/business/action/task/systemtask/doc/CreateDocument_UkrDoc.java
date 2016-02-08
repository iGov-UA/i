package org.igov.service.business.action.task.systemtask.doc;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.igov.io.GeneralConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class CreateDocument_UkrDoc implements JavaDelegate {

	private final static Logger LOG = LoggerFactory.getLogger(CreateDocument_UkrDoc.class);
	
	private Expression sLoginAuthor;
	private Expression sHead;
	private Expression sBody;
	private Expression nID_Pattern;
	
	 @Autowired
	 GeneralConfig generalConfig; 
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		String sLoginAuthorValue = getStringFromFieldExpression(this.sLoginAuthor, execution);
		String sHeadValue = getStringFromFieldExpression(this.sHead, execution);
		String sBodyValue = getStringFromFieldExpression(this.sBody, execution);
		String nID_PatternValue = getStringFromFieldExpression(this.nID_Pattern, execution);
		
	}

	protected String getStringFromFieldExpression(Expression expression,
			DelegateExecution execution) {
		if (expression != null) {
			Object value = expression.getValue(execution);
			if (value != null) {
				return value.toString();
			}
		}
		return null;
	}
	
}
