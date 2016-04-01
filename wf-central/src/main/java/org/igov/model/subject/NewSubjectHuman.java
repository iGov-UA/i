package org.igov.model.subject;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.igov.model.core.Entity;

import javax.persistence.Column;
/**
 * User: lyashenkoGS
 * Date: 31.03.2016
 * Time: 21:40
 */
public class NewSubjectHuman  {

    @JsonProperty(value = "sID")
    private String sID;

    @JsonProperty(value = "sLabel")
    private String sLabel;

    @JsonProperty(value = "sLabelShort")
    private String sLabelShort;

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
}
