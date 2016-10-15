package org.igov.model.object.place;

import javax.persistence.Column;
import org.igov.model.core.AbstractEntity;

@javax.persistence.Entity
public class ObjectPlace_UA  extends AbstractEntity {

    private static final long serialVersionUID = -6252907186874683521L;

    @Column
    private String sID;

    @Column
    private String sName_UA;

    @Column
    private Long nID_PlaceType;

    public String getsID() {
        return sID;
    }

    public void setsID(String sID) {
        this.sID = sID;
    }

    public String getsName_UA() {
        return sName_UA.trim();
    }

    public void setsName_UA(String sName_UA) {
        this.sName_UA = sName_UA.trim();
    }

    public Long getnID_PlaceType() {
        return nID_PlaceType;
    }

    public void setnID_PlaceType(Long nID_PlaceType) {
        this.nID_PlaceType = nID_PlaceType;
    }

    @Override
    public String toString() {
	StringBuffer sb = new StringBuffer(230);
	sb.append("ObjectPlace_UA={nID=");
	sb.append(getId());
	sb.append(",sID=");
	sb.append(sID);
	sb.append(",sName_UA=");
	sb.append(sName_UA);
	sb.append(",nID_PlaceType=");
	sb.append(nID_PlaceType);
	sb.append("}");

	return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ObjectPlace_UA objectPlace_UA = (ObjectPlace_UA) o;
        
        if (getId() != null ? !getId().equals(objectPlace_UA.getId()) : objectPlace_UA.getId() != null)
            return false;
        if (sID != null ? !sID.equals(objectPlace_UA.sID) : objectPlace_UA.sID != null)
            return false;
        if (sName_UA != null ? !sName_UA.equals(objectPlace_UA.sName_UA) : objectPlace_UA.sName_UA != null)
            return false;
        if (nID_PlaceType != null ? !nID_PlaceType.equals(objectPlace_UA.nID_PlaceType) : objectPlace_UA.nID_PlaceType != null)
            return false;

        return true;
    }
    
    @Override
    public int hashCode() {
	int result = getId() != null ? getId().hashCode() : 0;
	result = 31 * result + (sID != null ? sID.hashCode() : 0);
	result = 31 * result + (sName_UA != null ? sName_UA.hashCode() : 0);
	result = 31 * result + (nID_PlaceType != null ? nID_PlaceType.hashCode() : 0);
	return result;
    }
}
