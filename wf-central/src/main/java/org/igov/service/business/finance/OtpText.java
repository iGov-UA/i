package org.igov.service.business.finance;

public class OtpText {
    private String text;

    public OtpText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "text:" + text;
    }
}
