package org.igov.model.subject;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * User: lyashenkoGS
 * Date: 31.03.2016
 * Time: 21:40
 */
public class NewSubject {

    @JsonProperty(value="sLogin")
    private String sLogin;

    @JsonProperty(value="aSubjectHuman")
    private NewSubjectHuman aSubjectHuman;

    @JsonProperty(value = "aSubjectAccountContact")
    private transient List<NewSubjectContact> aSubjectAccountContact;

    public List<NewSubjectContact> getaSubjectAccountContact() {
        return aSubjectAccountContact;
    }

    public void setaSubjectAccountContact(List<NewSubjectContact> aSubjectAccountContact) {
        this.aSubjectAccountContact = aSubjectAccountContact;
    }

    public NewSubjectHuman getaSubjectHuman() {
        return aSubjectHuman;
    }

    public void setaSubjectHuman(NewSubjectHuman aSubjectHuman) {
        this.aSubjectHuman = aSubjectHuman;
    }

    public String getaSubjectLogin() {
        return sLogin;
    }

    public void setaSubjectLogin(String aSubjectLogin) {
        this.sLogin = aSubjectLogin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        NewSubject that = (NewSubject) o;

        return !(sLogin != null ? !sLogin.equals(that.sLogin) : that.sLogin != null);

    }

    @Override
    public int hashCode() {
        return sLogin != null ? sLogin.hashCode() : 0;
    }
}
