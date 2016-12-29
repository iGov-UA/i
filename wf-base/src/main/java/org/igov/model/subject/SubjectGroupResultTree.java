/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.model.subject;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Класс - Получение иерархии процессов
 * данной иерархии
 * 
 * @author inna
 */
@JsonRootName(value = "aSubjectGroupTree")
public class SubjectGroupResultTree implements Serializable {

	private List<SubjectGroup> aSubjectGroupTree;

	public List<SubjectGroup> getaSubjectGroupTree() {
		return aSubjectGroupTree;
	}

	public void setaSubjectGroupTree(List<SubjectGroup> aSubjectGroupTree) {
		this.aSubjectGroupTree = aSubjectGroupTree;
	}

	@Override
	public String toString() {
		return "SubjectGroupResultTree [aSubjectGroupTree=" + aSubjectGroupTree + "]";
	}

}
