package org.igov.io.sms;

import java.util.List;
import java.util.Map;
import org.igov.util.JSON.JsonRestUtils;

import com.google.gson.Gson;

public class SMSCallback {
    private static Gson oGson = new Gson();

    private String msStatus = null;
    private String messageId = null;
    private String msCode = null;
    private String msMessage = null;
    private List<String> schemaList = null;
    private String msChannel = null;
    private MessageHistory messageHistory = null;

    @SuppressWarnings("unchecked")
    public SMSCallback(String soJSON) throws IllegalArgumentException {
	Map<String, Object> moData = null;
	try {
	    moData = JsonRestUtils.readObject(soJSON, Map.class);
	} catch (Exception e) {
	    throw new IllegalArgumentException("Error parse JSON smData: " + e.getMessage());
	}

	if (moData != null) {
	    if (moData.containsKey("msStatus")) {
		this.msStatus = (String) moData.get("msStatus");
	    }
	    if (moData.containsKey("messageId")) {
		this.messageId = (String) moData.get("messageId");
	    }
	    if (moData.containsKey("schemaList")) {
		this.schemaList = (List<String>) moData.get("schemaList");
	    }
	    if (moData.containsKey("msChannel")) {
		this.msChannel = (String) moData.get("msChannel");
	    }
	    if (moData.containsKey("msCode")) {
		this.msCode = (String) moData.get("msCode");
	    }
	    if (moData.containsKey("msMessage")) {
		this.msMessage = (String) moData.get("msMessage");
	    }

	    if (moData.containsKey("messageHistory")) {
		Map<String, Object> moMessageHistory = (Map<String, Object>) moData.get("messageHistory");
		String sender = null;
		String privat24 = null;
		String sms = null;
		if (moMessageHistory.containsKey("sender")) {
		    sender = (String) moMessageHistory.get("sender");
		}
		if (moMessageHistory.containsKey("privat24")) {
		    privat24 = (String) moMessageHistory.get("privat24");
		}
		if (moMessageHistory.containsKey("sms")) {
		    sms = (String) moMessageHistory.get("sms");
		}
		this.messageHistory = new MessageHistory(sender, privat24, sms);
	    }
	}

    }

    public String getMsStatus() {
	return msStatus;
    }

    public String getMessageId() {
	return messageId;
    }

    public String getMsCode() {
	return msCode;
    }

    public String getMsMessage() {
	return msMessage;
    }

    public List<String> getSchemaList() {
	return schemaList;
    }

    public String getMsChannel() {
	return msChannel;
    }

    public MessageHistory getMessageHistory() {
	return messageHistory;
    }

    public class MessageHistory {
	private String sender;
	private String privat24;
	private String sms;

	public MessageHistory(String sender, String privat24, String sms) {
	    this.sender = sender;
	    this.privat24 = privat24;
	    this.sms = sms;
	}

	public String getSender() {
	    return sender;
	}

	public String getPrivat24() {
	    return privat24;
	}

	public String getSms() {
	    return sms;
	}

    }

    @Override
    public String toString() {
	return toJSONString();
    }

    public String toJSONString() {
	return oGson.toJson(this);
    }

//    public static void main(String[] args) {
//	///////////////////////////////
//	String s = "{" + "\"msStatus\": \"delivered\"," + " \"messageId\":\"ref1\","
//		+ "\"schemaList\": [ \"sender\", \"privat24\", \"sms\" ]," + "\"msChannel\": \"sms\","
//		+ " \"messageHistory\": {" + "\"sender\": \"Сообщение не доставлено\","
//		+ "\"privat24\": \"Не дождались отчета о доставке\"," + "\"sms\":\"Сообщение отправлено\"" + " }" + "}";
//	SMSCallback sc = new SMSCallback(s);
//
//	System.out.println(sc);
//
//	///////////////////////////////
//	String s2 = "{" + "\"msStatus\": \"warning\"," + "\"msCode\":\"incorrect_password\","
//		+ "\"msMessage\": \"Указанный пароль не подходит для мерчанта25@sms_service\","
//		+ "\"messageId\": \"ref1\"" + "}";
//
//	sc = new SMSCallback(s2);
//	System.out.println(sc);
//    }

}
