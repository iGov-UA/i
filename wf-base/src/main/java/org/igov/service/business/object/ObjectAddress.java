package org.igov.service.business.object;

public class ObjectAddress {
	private String code = "";
	private String desc = "";
	private String name = "";

	public ObjectAddress(String code, String desc, String name) {
		if (code != null) {
			this.code = code;
		}

		if (desc != null) {
			this.desc = desc;
		}

		if (name != null) {
			this.name = name;
		}
	}

	public String getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{\"code\":\"");
		sb.append(this.code);
		sb.append(",\"desc\":\"");
		sb.append(this.desc);
		sb.append(",\"name\":\"");
		sb.append(this.name);
		sb.append("}");

		return sb.toString();
	}
	
}
