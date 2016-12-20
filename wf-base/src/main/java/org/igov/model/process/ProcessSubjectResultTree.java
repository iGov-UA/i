/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.model.process;

import java.io.Serializable;

/**
 * Класс - Получение иерархии процессов
 * данной иерархии
 * 
 * @author inna
 */
public class ProcessSubjectResultTree implements Serializable {

	private ProcessSubject aProcessSubjectTree;


	public ProcessSubject getaProcessSubject() {
		return aProcessSubjectTree;
	}


	public void setaProcessSubject(ProcessSubject aProcessSubjectTree) {
		this.aProcessSubjectTree = aProcessSubjectTree;
	}


	@Override
	public String toString() {
		return "aProcessSubjectTree=" + aProcessSubjectTree;
	}

	
}
