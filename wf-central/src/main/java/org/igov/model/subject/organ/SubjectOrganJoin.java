package org.igov.model.subject.organ;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.igov.model.core.Entity;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.FetchType;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Type;

/**
 * @author dgroup
 * @since 05.07.2015
 */
@javax.persistence.Entity
public class SubjectOrganJoin extends Entity {

    @Column(name = "nID_SubjectOrgan")
    private Long subjectOrganId;

    // TODO > replace via Localization

    @JsonProperty(value = "sNameUa")
    @Column(name = "sNameUa")
    private String nameUa;

    @JsonProperty(value = "sNameRu")
    @Column(name = "sNameRu")
    private String nameRu;

    // TODO < end

    @JsonProperty(value = "sID_Privat")
    @Column(name = "sID_Privat")
    private String privatId;      // ИД-строка ключ-частный <60 символов //опциональный

    @JsonProperty(value = "sID_Public")
    @Column(name = "sID_Public")
    private String publicId;      // строка ключ-публичный <60 символов

    @JsonProperty(value = "sGeoLongitude")
    @Column(name = "sGeoLongitude")
    private String geoLongitude;   // строка долготы //опциональный

    @JsonProperty(value = "sGeoLatitude")
    @Column(name = "sGeoLatitude")
    private String geoLatitude;    // строка широты //опциональный

    @JsonProperty(value = "nID_Region")
    @Column(name = "nID_Region")
    private Long regionId;

    @JsonProperty(value = "nID_City")
    @Column(name = "nID_City")
    private Long cityId;

    @JsonProperty(value = "sID_UA")
    @Column(name = "sID_UA")
    private String uaId;

    //@OneToMany(targetEntity = SubjectOrganJoinAttribute.class, mappedBy = "subjectOrganJoinId", fetch = FetchType.EAGER)
    //private List<SubjectOrganJoinAttribute> aAttribute = new ArrayList<>();

    public Long getSubjectOrganId() {
        return subjectOrganId;
    }

    public void setSubjectOrganId(Long subjectOrganId) {
        this.subjectOrganId = subjectOrganId;
    }

    public String getNameRu() {
        return nameRu;
    }

    public void setNameRu(String nameRu) {
        this.nameRu = nameRu;
    }

    public String getNameUa() {
        return nameUa;
    }

    public void setNameUa(String nameUa) {
        this.nameUa = nameUa;
    }

    public String getPrivatId() {
        return privatId;
    }

    public void setPrivatId(String privatId) {
        this.privatId = privatId;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String getGeoLongitude() {
        return geoLongitude;
    }

    public void setGeoLongitude(String geoLongitude) {
        this.geoLongitude = geoLongitude;
    }

    public String getGeoLatitude() {
        return geoLatitude;
    }

    public void setGeoLatitude(String geoLatitude) {
        this.geoLatitude = geoLatitude;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public String getUaId() {
        return uaId;
    }

    public void setUaId(String uaId) {
        this.uaId = uaId;
    }

    /*public List<SubjectOrganJoinAttribute> getaAttribute() {
        return aAttribute;
    }

    public void addAttributeList(List<SubjectOrganJoinAttribute> aSubjectOrganJoinAttribute){
        if(aSubjectOrganJoinAttribute != null) {
            for(SubjectOrganJoinAttribute oSubjectOrganJoinAttribute: aSubjectOrganJoinAttribute){
            aAttribute.add(oSubjectOrganJoinAttribute);
            }
        }
    }

    public void addAttribute(String sName, String sValue){
        SubjectOrganJoinAttribute oSubjectOrganJoinAttribute = new SubjectOrganJoinAttribute();
        //oSubjectOrganJoinAttribute.setId(getId());
        oSubjectOrganJoinAttribute.setSubjectOrganJoinId(getId());
        oSubjectOrganJoinAttribute.setName(sName);
        oSubjectOrganJoinAttribute.setValue(sValue);
        aAttribute.add(oSubjectOrganJoinAttribute);
    }*/
    
    @Override
    public String toString() {
        return "SubjectOrganJoin{id='" + getId() + '\'' +
                ", subjectOrganId=" + subjectOrganId +
                ", nameUa='" + nameUa + '\'' +
                ", nameRu='" + nameRu + '\'' +
                ", privatId='" + privatId + '\'' +
                ", publicId='" + publicId + '\'' +
                ", geoLongitude='" + geoLongitude + '\'' +
                ", geoLatitude='" + geoLatitude + '\'' +
                ", regionId='" + regionId + '\'' +
                ", cityId='" + cityId + '\'' +
                ", UA_ID='" + uaId + '\'' +
                //", attributes=" + aAttribute +'\'' +
                '}';
    }
}