package org.igov.io.sms;

public class SMS_Text {
    private String type = "0";
    private String text = null;

    public SMS_Text(String text) {
	this.setText(text);
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public String getText() {
	return text;
    }

    public void setText(String text) {
	this.text = text;
    }

    @Override
    public String toString() {
	return String.format("{type:%s, text:%s}", type, text);
    }

}
