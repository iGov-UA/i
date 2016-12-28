/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.model.process;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Класс - Получение иерархии процессов
 * данной иерархии
 * 
 * @author inna
 */
@JsonRootName(value = "aProcessSubjectTree")
public class ProcessSubjectResultTree implements Serializable {

	private List<ProcessSubject> aProcessSubjectTree;

	public List<ProcessSubject> getaProcessSubjectTree() {
		return aProcessSubjectTree;
	}

	public void setaProcessSubjectTree(List<ProcessSubject> aProcessSubjectTree) {
		this.aProcessSubjectTree = aProcessSubjectTree;
	}

	@Override
	public String toString() {
		return "aProcessSubjectTree=" + aProcessSubjectTree;
	}

	
}
