/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.model.process;

import java.io.Serializable;

/**
 * Класс - юзеры
 * 
 * @author inna
 */
public class ProcessUser implements Serializable {

	private String sLogin;
	private String sFirstName;
	private String sLastName;
	private String sEmail;
	private String sPicture;

	public String getsLogin() {
		return sLogin;
	}

	public void setsLogin(String sLogin) {
		this.sLogin = sLogin;
	}

	public String getsFirstName() {
		return sFirstName;
	}

	public void setsFirstName(String sFirstName) {
		this.sFirstName = sFirstName;
	}

	public String getsLastName() {
		return sLastName;
	}

	public void setsLastName(String sLastName) {
		this.sLastName = sLastName;
	}

	public String getsEmail() {
		return sEmail;
	}

	public void setsEmail(String sEmail) {
		this.sEmail = sEmail;
	}

	public String getsPicture() {
		return sPicture;
	}

	public void setsPicture(String sPicture) {
		this.sPicture = sPicture;
	}

	@Override
	public String toString() {
		return "sLogin=" + sLogin + ", sFirstName=" + sFirstName + ", sLastName=" + sLastName + ", sEmail="
				+ sEmail + ", sPicture=" + sPicture;
	}

	public static class BuilderHelper {
		public static ProcessUser buildSubjectUser(String login, String firstName, String lastName, String email, String picture) {
			final ProcessUser model = new ProcessUser();
			model.setsLogin(login);
			model.setsFirstName(firstName);
			model.setsLastName(lastName);
			model.setsEmail(email);
			model.setsPicture(picture);
			return model;
		}

	}

}
