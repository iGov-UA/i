package org.igov.model.subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class VSubjectGroupTreeResult implements IVisitor  {
	
	private static final Log LOG = LogFactory.getLog(VSubjectGroupTreeResult.class);

	@Override
	public void deepLevel(VSubjectGroupResult vSubjectGroupResult) {
		LOG.info("VSubjectGroupResultttttttt "+vSubjectGroupResult.toString());
		
	}

	@Override
	public void deepLevel(VSubjectGroupNode vSubjectGroupNode) {
		LOG.info("VSubjectGroupNodeeeeeeeee "+vSubjectGroupNode.toString());
		
	}

	@Override
	public void deepLevel(SubjectGroup subjectGroup) {
		LOG.info("SubjectGroupppppppppppp "+subjectGroup.toString());
		
	}

}
