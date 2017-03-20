package org.igov.model.subject;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.igov.model.core.NamedEntity;

public class NewSubjectHuman extends NamedEntity{
      @JsonProperty(value = "sINN")
    private String sINN;

    @JsonProperty(value = "sSB")
    private String sSB;

    @JsonProperty(value = "sPassportSeria")
    private String sPassportSeria;

    @JsonProperty(value = "sPassportNumber")
    private String sPassportNumber;

    @JsonProperty(value = "sFamily")
    private String sFamily;

    @JsonProperty(value = "sSurname")
    private String sSurname;

    @JsonProperty(value = "oDefaultEmail")
    private NewSubjectContact defaultEmail;

    @JsonProperty(value = "oDefaultPhone")
    private NewSubjectContact defaultPhone;

    public String getsINN() {
        return sINN;
    }

    public void setsINN(String sINN) {
        this.sINN = sINN;
    }

    public String getsSB() {
        return sSB;
    }

    public void setsSB(String sSB) {
        this.sSB = sSB;
    }

    public String getsPassportSeria() {
        return sPassportSeria;
    }

    public void setsPassportSeria(String sPassportSeria) {
        this.sPassportSeria = sPassportSeria;
    }

    public String getsPassportNumber() {
        return sPassportNumber;
    }

    public void setsPassportNumber(String sPassportNumber) {
        this.sPassportNumber = sPassportNumber;
    }

    public String getsFamily() {
        return sFamily;
    }

    public void setsFamily(String sFamily) {
        this.sFamily = sFamily;
    }

    public String getsSurname() {
        return sSurname;
    }

    public void setsSurname(String sSurname) {
        this.sSurname = sSurname;
    }

    public NewSubjectContact getDefaultEmail() {
        return defaultEmail;
    }

    public void setDefaultEmail(NewSubjectContact defaultEmail) {
        this.defaultEmail = defaultEmail;
    }

    public NewSubjectContact getDefaultPhone() {
        return defaultPhone;
    }

    public void setDefaultPhone(NewSubjectContact defaultPhone) {
        this.defaultPhone = defaultPhone;
    }
}
