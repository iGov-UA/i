package org.igov.model.subject;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.igov.model.core.NamedEntity;

import java.util.List;

public class NewSubjectOrgan extends NamedEntity {
    @JsonProperty(value = "sOKPO")
    private String sOKPO;

    @JsonProperty(value = "sFormPrivacy")
    private String sFormPrivacy;

    @JsonProperty(value = "sNameFull")

    private String sNameFull;

    @JsonProperty(value = "aContact")
    private transient List<NewSubjectContact> aContact;

    public String getsOKPO() {
        return sOKPO;
    }

    public void setsOKPO(String sOKPO) {
        this.sOKPO = sOKPO;
    }

    public String getsFormPrivacy() {
        return sFormPrivacy;
    }

    public void setsFormPrivacy(String sFormPrivacy) {
        this.sFormPrivacy = sFormPrivacy;
    }

    public String getsNameFull() {
        return sNameFull;
    }

    public void setsNameFull(String sNameFull) {
        this.sNameFull = sNameFull;
    }

    public List<NewSubjectContact> getaContact() {
        return aContact;
    }

    public void setaContact(List<NewSubjectContact> aContact) {
        this.aContact = aContact;
    }
}
