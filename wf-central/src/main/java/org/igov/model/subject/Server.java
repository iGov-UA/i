package org.igov.model.subject;

import org.igov.model.core.Entity;

import javax.persistence.Column;

/**
 * User: goodg_000
 * Date: 29.10.2015
 * Time: 21:26
 */
@javax.persistence.Entity
public class Server extends Entity {

    @Column(unique = true, nullable = false)
    private String sID;

    @Column
    private String sType;

    @Column
    private String sURL_Alpha;

    @Column
    private String sURL_Beta;

    @Column
    private String sURL_Omega;

    @Column
    private String sURL;

    public String getsID() {
        return sID;
    }
    public void setsID(String sID) {
        this.sID = sID;
    }

    public String getsType() {
        return sType;
    }
    public void setsType(String sType) {
        this.sType = sType;
    }

    public String getsURL_Alpha() {
        return sURL_Alpha;
    }
    public void setsURL_Alpha(String sURL_Alpha) {
        this.sURL_Alpha = sURL_Alpha;
    }

    public String getsURL_Beta() {
        return sURL_Beta;
    }
    public void setsURL_Beta(String sURL_Beta) {
        this.sURL_Beta = sURL_Beta;
    }

    public String getsURL_Omega() {
        return sURL_Omega;
    }
    public void setsURL_Omega(String sURL_Omega) {
        this.sURL_Omega = sURL_Omega;
    }

    public String getsURL() {
        return sURL;
    }
    public void setsURL(String sURL) {
        this.sURL = sURL;
    }
}

