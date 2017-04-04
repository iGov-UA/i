package org.igov.service.business.object;

public class ObjectAddress {
	private String code = "";
	private String desc = "";
	private String type = "";

	public ObjectAddress(String code, String desc, String type) {
		if (code != null) {
			this.code = code;
		}

		if (desc != null) {
			this.desc = desc;
		}

		if (type != null) {
			this.type = type;
		}
	}

	public String getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}

	public String getType() {
		return type;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{\"code\":\"");
		sb.append(this.code);
		sb.append("\",\"desc\":\"");
		sb.append(this.desc);
		sb.append("\",\"type\":\"");
		sb.append(this.type);
		sb.append("\"}");

		return sb.toString();
	}
	
}
