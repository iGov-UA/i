/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.model.subject;

import java.io.Serializable;
import java.util.List;

/**
 * Класс - возвращает список организационной иерархии и список людей подчиненных
 * данной иерархии
 * 
 * @author inna
 */
public class SubjectGroupAndUser implements Serializable {

	private List<SubjectGroup> aSubjectGroup;

	private List<SubjectUser> aSubjectUser;

	public SubjectGroupAndUser() {
	}

	public List<SubjectGroup> getaSubjectGroup() {
		return aSubjectGroup;
	}

	public void setaSubjectGroup(List<SubjectGroup> aSubjectGroup) {
		this.aSubjectGroup = aSubjectGroup;
	}

	public List<SubjectUser> getaSubjectUser() {
		return aSubjectUser;
	}

	public void setaSubjectUser(List<SubjectUser> aSubjectUser) {
		this.aSubjectUser = aSubjectUser;
	}

	@Override
	public String toString() {
		return "aSubjectGroup=" + aSubjectGroup + ", aSubjectUser=" + aSubjectUser;
	}

}
