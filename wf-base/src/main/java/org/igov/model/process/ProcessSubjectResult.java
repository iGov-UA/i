/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.model.process;

import java.io.Serializable;
import java.util.List;

/**
 * Класс - Получение иерархии процессов
 * данной иерархии
 * 
 * @author inna
 */
public class ProcessSubjectResult implements Serializable {

	private List< ProcessSubject> aProcessSubject;

	public List<ProcessSubject> getaProcessSubject() {
		return aProcessSubject;
	}

	public void setaProcessSubject(List<ProcessSubject> aProcessSubject) {
		this.aProcessSubject = aProcessSubject;
	}

	@Override
	public String toString() {
		return "aProcessSubject=" + aProcessSubject;
	}

	
}
