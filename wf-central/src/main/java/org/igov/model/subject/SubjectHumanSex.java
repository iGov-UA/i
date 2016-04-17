package org.igov.model.subject;

public enum SubjectHumanSex {

	FEMALE(0),
	MALE(1);
	
	 private final int value;

	private SubjectHumanSex(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	 
}
