package org.igov.service.business.subject;

import org.igov.model.subject.SubjectRightBP;

/**
*
* @author Elena
*/
public class SubjectRightBPVO {
	
	SubjectRightBP oSubjectRightBP;

	private String sName_BP;

	
	public SubjectRightBPVO() {

	}

	public SubjectRightBP getoSubjectRightBP() {
		return oSubjectRightBP;
	}

	public void setoSubjectRightBP(SubjectRightBP oSubjectRightBP) {
		this.oSubjectRightBP = oSubjectRightBP;
	}

	public String getsName_BP() {
		return sName_BP;
	}

	public void setsName_BP(String sName_BP) {
		this.sName_BP = sName_BP;
	}
}
