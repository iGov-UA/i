package org.igov.model.arm;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DboTkResult implements Serializable {
	
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		 
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

		@Override
		public String toString() {
			return "DboTkResult [state=" + state + ", mess=" + mess + ", code=" + code + "]";
		}
}