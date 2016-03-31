package org.igov.model.subject;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * User: lyashenkoGS
 * Date: 31.03.2016
 * Time: 21:40
 */
public class NewSubject {

    @JsonProperty(value="aSubjectLogin")
    private String aSubjectLogin;

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
        return aSubjectLogin;
    }

    public void setaSubjectLogin(String aSubjectLogin) {
        this.aSubjectLogin = aSubjectLogin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        NewSubject that = (NewSubject) o;

        return !(aSubjectLogin != null ? !aSubjectLogin.equals(that.aSubjectLogin) : that.aSubjectLogin != null);

    }

    @Override
    public int hashCode() {
        return aSubjectLogin != null ? aSubjectLogin.hashCode() : 0;
    }
}
