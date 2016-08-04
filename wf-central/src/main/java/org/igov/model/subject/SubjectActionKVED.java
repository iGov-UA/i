package org.igov.model.subject;

import javax.persistence.Column;

import org.igov.model.core.AbstractEntity;
import org.igov.model.object.place.Country;

@javax.persistence.Entity
public class SubjectActionKVED extends AbstractEntity {

    private static final long serialVersionUID = -3659824409047841264L;

    @Column
    private String sID;

    @Column
    private String sNote;

    public String getsID() {
	return sID;
    }

    public void setsID(String sID) {
	this.sID = sID;
    }

    public String getsNote() {
	return sNote.trim();
    }

    public void setsNote(String sNote) {
	this.sNote = sNote.trim();
    }

    @Override
    public String toString() {
	StringBuffer sb = new StringBuffer(230);
	sb.append("SubjectActionKVED={nID=");
	sb.append(getId());
	sb.append(",sID=");
	sb.append(sID);
	sb.append(",sNote=");
	sb.append(sNote);
	sb.append("}");
	return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        SubjectActionKVED subjectActionKVED = (SubjectActionKVED) o;

        if (sID != null ? !sID.equals(subjectActionKVED.sID) : subjectActionKVED.sID != null)
            return false;
        if (sNote != null ? !sNote.equals(subjectActionKVED.sNote) : subjectActionKVED.sNote != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
	int result = getId() != null ? getId().hashCode() : 0;
	result = 31 * result + (sID != null ? sID.hashCode() : 0);
	result = 31 * result + (sNote != null ? sNote.hashCode() : 0);
	return result;
    }

}
