package org.igov.model.subject;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * User: lyashenkoGS
 * Date: 31.03.2016
 * Time: 21:40
 */
public class NewSubjectAccount {

    @JsonProperty(value="sLogin")
    private String sLogin;

    @JsonProperty(value="oSubject")
    private NewSubject oSubject;




    public NewSubject getoSubject() {
        return oSubject;
    }

    public void setoSubject(NewSubject oSubject) {
        this.oSubject = oSubject;
    }

    public String getsLogin() {
        return sLogin;
    }

    public void setsLogin(String sLogin) {
        this.sLogin = sLogin;
    }
}
