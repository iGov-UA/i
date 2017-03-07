package org.igov.model.subject;

import java.util.List;

import javax.persistence.Column;
import org.igov.model.core.AbstractEntity;

import com.fasterxml.jackson.annotation.JsonProperty;

@javax.persistence.Entity
public class Subject extends AbstractEntity {

    @JsonProperty(value = "sID")
    @Column(name = "sID", nullable = true)
    private String sID;

    @JsonProperty(value = "sLabel")
    @Column(name = "sLabel", nullable = true)
    private String sLabel;

    @JsonProperty(value = "sLabelShort")
    @Column(name = "sLabelShort", nullable = true)
    private String sLabelShort;

    @JsonProperty(value = "aSubjectAccountContact")
    private transient List<SubjectContact> aSubjectAccountContact;
    
/*    @JsonProperty(value = "oSubjectHuman")
    @OneToOne
    @Cascade({CascadeType.SAVE_UPDATE})
    @JoinColumn(name = "nID_SubjectHuman")
    private SubjectHuman oSubjectHuman;
    
    @JsonProperty(value = "oSubjectOrgan")
    @OneToOne
    @Cascade({CascadeType.SAVE_UPDATE})
    @JoinColumn(name = "nID_SubjectOrgan")
    private SubjectOrgan oSubjectOrgan;*/
    
    
/*	public SubjectHuman getoSubjectHuman() {
		return oSubjectHuman;
	}

	public void setoSubjectHuman(SubjectHuman oSubjectHuman) {
		this.oSubjectHuman = oSubjectHuman;
	}

	public SubjectOrgan getoSubjectOrgan() {
		return oSubjectOrgan;
	}

	public void setoSubjectOrgan(SubjectOrgan oSubjectOrgan) {
		this.oSubjectOrgan = oSubjectOrgan;
	}*/

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

    public void setaSubjectAccountContact(List<SubjectContact> aSubjectAccountContact) {
        this.aSubjectAccountContact = aSubjectAccountContact;
    }

    public List<SubjectContact> getaSubjectAccountContact() {
        return aSubjectAccountContact;
    }
}
