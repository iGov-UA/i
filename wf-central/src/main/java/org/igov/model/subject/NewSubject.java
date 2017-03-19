package org.igov.model.subject;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * User: lyashenkoGS
 * Date: 31.03.2016
 * Time: 21:40
 */
public class NewSubject {

    @JsonProperty(value = "sID")
    private String sID;

    @JsonProperty(value = "sLabel")
    private String sLabel;

    @JsonProperty(value = "sLabelShort")
    private String sLabelShort;

    @JsonProperty(value = "aSubjectAccountContact")
    private transient List<NewSubjectContact> aSubjectAccountContact;

    @JsonProperty(value = "oSubjectHuman")
    private transient NewSubjectHuman oSubjectHuman;

    @JsonProperty(value = "oSubjectOrgan")
    private transient NewSubjectOrgan oSubjectOrgan;

    public String getsID() {
        return sID;
    }

    public void setsID(String sID) {
        this.sID = sID;
    }

    public String getsLabel() {
        return sLabel;
    }

    public void setsLabel(String sLabel) {
        this.sLabel = sLabel;
    }

    public String getsLabelShort() {
        return sLabelShort;
    }

    public void setsLabelShort(String sLabelShort) {
        this.sLabelShort = sLabelShort;
    }

    public List<NewSubjectContact> getaSubjectAccountContact() {
        return aSubjectAccountContact;
    }

    public void setaSubjectAccountContact(List<NewSubjectContact> aSubjectAccountContact) {
        this.aSubjectAccountContact = aSubjectAccountContact;
    }

    public NewSubjectHuman getoSubjectHuman() {
        return oSubjectHuman;
    }

    public void setoSubjectHuman(NewSubjectHuman oSubjectHuman) {
        this.oSubjectHuman = oSubjectHuman;
    }

    public NewSubjectOrgan getoSubjectOrgan() {
        return oSubjectOrgan;
    }

    public void setoSubjectOrgan(NewSubjectOrgan oSubjectOrgan) {
        this.oSubjectOrgan = oSubjectOrgan;
    }
}
