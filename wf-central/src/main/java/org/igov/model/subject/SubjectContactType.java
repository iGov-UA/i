package org.igov.model.subject;

import org.igov.model.core.Entity;

import javax.persistence.Column;

/**
 * User: goodg_000
 * Date: 27.12.2015
 * Time: 13:35
 */
@javax.persistence.Entity
public class SubjectContactType extends Entity {

    @Column
    private String sName_EN;

    @Column
    private String sName_UA;

    @Column
    private String sName_RU;

    public String getsName_EN() {
        return sName_EN;
    }

    public void setsName_EN(String sName_EN) {
        this.sName_EN = sName_EN;
    }

    public String getsName_UA() {
        return sName_UA;
    }

    public void setsName_UA(String sName_UA) {
        this.sName_UA = sName_UA;
    }

    public String getsName_RU() {
        return sName_RU;
    }

    public void setsName_RU(String sName_RU) {
        this.sName_RU = sName_RU;
    }
}
