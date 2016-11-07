package org.igov.model.action.item;

import org.igov.model.object.place.Place;
import org.igov.model.object.place.City;
import org.igov.model.object.place.Region;
import org.igov.model.subject.organ.SubjectOrgan;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.igov.model.core.AbstractEntity;

import javax.persistence.*;
import org.igov.io.GeneralConfig;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: goodg_000 Date: 04.05.2015 Time: 23:52
 */
@javax.persistence.Entity
public class ServiceData extends AbstractEntity {
    
    //@Autowired
    //private transient GeneralConfig generalConfig;

    @JsonProperty(value = "nID_Service")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nID_Service", nullable = false, updatable = false)
    private Service service;

    @JsonProperty(value = "oPlace")
    @ManyToOne
    @JoinColumn(name = "nID_Place")
    private Place oPlace;

    /**
     * Can be calculated via {@link org.igov.model.PlaceDao#getRoot(Place)}
     */
    @Transient
    @JsonProperty(value = "oPlaceRoot")
    private Place oPlaceRoot;

    @JsonProperty(value = "nID_City")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nID_City")
    private City city;

    @JsonProperty(value = "nID_Region")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nID_Region")
    private Region region;

    @JsonProperty(value = "nID_ServiceType")
    @ManyToOne(fetch = FetchType.EAGER)
    @Cascade({ CascadeType.SAVE_UPDATE })
    @JoinColumn(name = "nID_ServiceType", nullable = false)
    private ServiceType serviceType;

    @JsonProperty(value = "oSubject_Operator")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nID_Subject_Operator", nullable = true)
    private SubjectOrgan subject_Operator;

    @JsonProperty(value = "oData")
    @Column(name = "oData", nullable = false)
    private String data;

    @JsonProperty(value = "sURL")
    @Column(name = "sURL", nullable = false)
    private String url;

    @JsonProperty(value = "bHidden")
    @Column(name = "bHidden", nullable = false)
    private boolean hidden;

    @JsonProperty(value = "bTest")
    @Column(name = "bTest", nullable = false)
    private boolean bTest;

    @JsonProperty(value = "sNote")
    @Column(name = "sNote", nullable = false)
    private String sNote;

    @JsonProperty(value = "asAuth")
    @Column(name = "asAuth", nullable = false)
    private String asAuth;

    @JsonProperty(value = "nID_Server")
    @Column(name = "nID_Server", nullable = false)
    private Long nID_Server;
    
    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean b) {
        this.hidden = b;
    }

    public boolean isTest() {
        return bTest;
    }

    public void setTest(boolean b) {
        this.bTest = b;
    }

    public String getNote() {
        return sNote;
    }

    public void setNote(String s) {
        this.sNote = s;
    }

    public String getAsAuth() {
        return asAuth;
    }

    public void setAsAuth(String asAuth) {
        this.asAuth = asAuth;
    }

    public Place getoPlace() {
        return oPlace;
    }

    public void setoPlace(Place oPlace) {
        this.oPlace = oPlace;
    }

    public Place getoPlaceRoot() {
        return oPlaceRoot;
    }

    public void setoPlaceRoot(Place oPlaceRoot) {
        this.oPlaceRoot = oPlaceRoot;
    }

    public Long getnID_Server() {
        /*System.out.println("!!!!!!!!!!!!!!!!!!!!!! " + GeneralConfig.mServerReplace);
        if(nID_Server != null){
           nID_Server = GeneralConfig.getServerId(nID_Server.intValue()).longValue();
        }*/
        return nID_Server;
    }

    public void setnID_Server(Long nID_Server) {
        this.nID_Server = nID_Server;
    }

    public SubjectOrgan getSubject_Operator() {
        return subject_Operator;
    }

    public void setSubject_Operator(SubjectOrgan subject_Operator) {
        this.subject_Operator = subject_Operator;
    }

}