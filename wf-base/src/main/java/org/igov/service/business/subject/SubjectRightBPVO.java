package org.igov.service.business.subject;

import org.igov.model.subject.SubjectRightBP;

/**
 *
 * @author Elena
 */
public class SubjectRightBPVO {
	
	private SubjectRightBP oSubjectRightBP;

	/*private String sID_BP;

	private String sID_Place_UA;

	private String sID_Group;
*/
	private String sName_BP;

	public SubjectRightBPVO() {

	}

	/*public String getsID_BP() {
		return sID_BP;
	}

	public void setsID_BP(String sID_BP) {
		this.sID_BP = sID_BP;
	}

	public String getsID_Place_UA() {
		return sID_Place_UA;
	}

	public void setsID_Place_UA(String sID_Place_UA) {
		this.sID_Place_UA = sID_Place_UA;
	}

	public String getsID_Group() {
		return sID_Group;
	}

	public void setsID_Group(String sID_Group) {
		this.sID_Group = sID_Group;
	}*/
	
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
