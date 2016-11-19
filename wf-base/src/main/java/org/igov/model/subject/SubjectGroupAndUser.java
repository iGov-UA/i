/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.model.subject;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Класс - возвращает список организационной иерархии и список людей подчиненных данной иерархии
 * @author inna
 */
public class SubjectGroupAndUser implements Serializable {

	private List<SubjectGroup> aSubjectGroup;

	private List<Map<String, String>> aSubjectUser;


	public SubjectGroupAndUser() {
	}


	public List<SubjectGroup> getaSubjectGroup() {
		return aSubjectGroup;
	}


	public void setaSubjectGroup(List<SubjectGroup> aSubjectGroup) {
		this.aSubjectGroup = aSubjectGroup;
	}


	public List<Map<String, String>> getaSubjectUser() {
		return aSubjectUser;
	}


	public void setaSubjectUser(List<Map<String, String>> aSubjectUser) {
		this.aSubjectUser = aSubjectUser;
	}


	@Override
	public String toString() {
		return "aSubjectGroup=" + aSubjectGroup + ", aSubjectUser=" + aSubjectUser;
	}





}
