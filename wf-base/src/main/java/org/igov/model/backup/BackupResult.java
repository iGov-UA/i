package org.igov.model.backup;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BackupResult implements Serializable {
	
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	/**
	 * Статус - ок 
	 */
	public static final String PRSTATE_OK = "r";
	
	/**
	 * Статус - ошибка 
	 */
	public static final String PRSTATE_ERROR = "e";
	
	/**
	 * Код - ок 
	 */
	public static final String PRCODE_OK = "000000";
	
	/**
	 * Код - ошибка  
	 */
	public static final String PRCODE_ERROR = "PERROR";

		 
		 @JsonProperty(value = "state")
	     private String state;
		 
		 @JsonProperty(value = "mess")
	     private String mess;
		 
		 @JsonProperty(value = "code")
	     private String code;

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public String getMess() {
			return mess;
		}

		public void setMess(String mess) {
			this.mess = mess;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}
		
		public static BackupResult fillResult(String mess, String code, String state) {
			BackupResult backupEndResult = new BackupResult();
	         backupEndResult.setMess(mess);
	         backupEndResult.setCode(code);
	         backupEndResult.setState(state);
	         return backupEndResult;
		}

		@Override
		public String toString() {
			return "BackupResult [state=" + state + ", mess=" + mess + ", code=" + code + "]";
		}
}