package org.igov.model.action.vo;


import java.util.Date;

import org.igov.model.document.DocumentStepSubjectRight;
import org.joda.time.DateTime;

public class DocumentSubmitedUnsignedVO {
	
	
	
	DocumentStepSubjectRight oDocumentStepSubjectRight;

	private String sNameBP;

	private String sUserTaskName;
	
	private String sDateCreateProcess;

	private String sDateCreateUserTask;

	private String sDateSubmit;

	private String sID_Order;

	public DocumentSubmitedUnsignedVO() {

	}

	public DocumentStepSubjectRight getoDocumentStepSubjectRight() {
		return oDocumentStepSubjectRight;
	}

	public void setoDocumentStepSubjectRight(DocumentStepSubjectRight oDocumentStepSubjectRight) {
		this.oDocumentStepSubjectRight = oDocumentStepSubjectRight;
	}

	public String getsNameBP() {
		return sNameBP;
	}

	public void setsNameBP(String sNameBP) {
		this.sNameBP = sNameBP;
	}

	public String getsUserTaskName() {
		return sUserTaskName;
	}

	public void setsUserTaskName(String sUserTaskName) {
		this.sUserTaskName = sUserTaskName;
	}

	public String getsDateCreateProcess() {
		return sDateCreateProcess;
	}

	public void setsDateCreateProcess(String sDateCreateProcess) {
		this.sDateCreateProcess = sDateCreateProcess;
	}

	public String getsDateCreateUserTask() {
		return sDateCreateUserTask;
	}

	public void setsDateCreateUserTask(String sDateCreateUserTask) {
		this.sDateCreateUserTask = sDateCreateUserTask;
	}

	public String getsDateSubmit() {
		return sDateSubmit;
	}

	public void setsDateSubmit(String sDateSubmit) {
		this.sDateSubmit = sDateSubmit;
	}

	public String getsID_Order() {
		return sID_Order;
	}

	public void setsID_Order(String sID_Order) {
		this.sID_Order = sID_Order;
	}

}
