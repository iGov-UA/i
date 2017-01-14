package org.igov.model.subject;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using=SubjectHumanSexSerializer.class)
public enum SubjectHumanSex {

	FEMALE("female", "0"), MALE("male", "1");

	private String sID_Sex;
	private String nID_Sex;

	private SubjectHumanSex(String sID_Sex, String nID_Sex) {
		this.sID_Sex = sID_Sex;
		this.nID_Sex = nID_Sex;
	}

	@Override
	public String toString() {
		return this.nID_Sex;
	}

	public String getsID_Sex() {
		return sID_Sex;
	}

	public String getnID_Sex() {
		return nID_Sex;
	}

}
