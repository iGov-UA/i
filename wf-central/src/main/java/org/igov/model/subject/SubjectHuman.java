package org.igov.model.subject;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.igov.model.core.NamedEntity;

import javax.persistence.*;

@javax.persistence.Entity
@AttributeOverrides({
    @AttributeOverride(name = "name",
            column = @Column(name = "sName", nullable = true))})
public class SubjectHuman extends NamedEntity {

    public static String getSubjectId(SubjectHumanIdType subjectHumanIdType, String sCode_Subject) {
        String res = sCode_Subject;
        if (subjectHumanIdType != SubjectHumanIdType.INN) {
            res = String.format("_%s;%s", subjectHumanIdType.getId(), sCode_Subject);
        }
        return res;
    }

    //    public enum SubjectHumanSex {
//        FEMALE("Female"),
//        MALE("Male");
//        private String sID_Sex;
//
//        private SubjectHumanSex(String sID_Sex) {
//            this.sID_Sex = sID_Sex;
//        }
//        
//        public String getsID_Sex(){
//            return this.sID_Sex;
//        }
//
//        public static SubjectHumanSex getBynID_Sex(int nID_Sex) {
//            if (nID_Sex > 1 || nID_Sex < 0) {
//                throw new IllegalArgumentException(
//                        String.format("nID_Sex [%d] is out of possible range!", nID_Sex));
//            }
//            SubjectHumanSex result = null;
//
//            if (nID_Sex == 0) {
//                result = FEMALE;
//            } else if (nID_Sex == 1) {
//                result = MALE;
//            }
//            return result;
//        }
//    }
    public static NewSubjectHuman getNewSubjectHuman(SubjectHuman subjectHuman) {
        NewSubjectHuman newSubjectHuman = new NewSubjectHuman();
        newSubjectHuman.setsSurname(subjectHuman.getsSurname());
        newSubjectHuman.setsFamily(subjectHuman.getsFamily());
        newSubjectHuman.setsINN(subjectHuman.getsINN());
        newSubjectHuman.setsSB(subjectHuman.getsSB());
        newSubjectHuman.setsPassportSeria(subjectHuman.getsPassportSeria());
        newSubjectHuman.setsPassportNumber(subjectHuman.getsPassportNumber());
        newSubjectHuman.setDefaultEmail(SubjectContact
                .getNewSubjectContact(subjectHuman.getDefaultEmail()));
        newSubjectHuman.setDefaultPhone(SubjectContact
                .getNewSubjectContact(subjectHuman.getDefaultPhone()));
        return newSubjectHuman;
    }

    @JsonProperty(value = "oSubject")
    @OneToOne
    @Cascade({CascadeType.SAVE_UPDATE})
    @JoinColumn(name = "nID_Subject", nullable = false)
    private Subject oSubject;

    @JsonProperty(value = "sINN")
    @Column(name = "sINN")
    private String sINN;

    @JsonProperty(value = "sSB")
    @Column(name = "sSB")
    private String sSB;

    @JsonProperty(value = "sPassportSeria")
    @Column(name = "sPassportSeria")
    private String sPassportSeria;

    @JsonProperty(value = "sPassportNumber")
    @Column(name = "sPassportNumber")
    private String sPassportNumber;

    @JsonProperty(value = "sFamily")
    @Column(name = "sFamily")
    private String sFamily;

    @JsonProperty(value = "sSurname")
    @Column(name = "sSurname")
    private String sSurname;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "nID_SubjectHumanIdType", nullable = false)
    private SubjectHumanIdType subjectHumanIdType = SubjectHumanIdType.INN;

    @JsonProperty(value = "oDefaultEmail")
    @ManyToOne
    @JoinColumn(name = "nID_SubjectContact_DefaultEmail")
    @Cascade({CascadeType.SAVE_UPDATE})
    private SubjectContact defaultEmail;

    @JsonProperty(value = "oDefaultPhone")
    @ManyToOne
    @JoinColumn(name = "nID_SubjectContact_DefaultPhone")
    @Cascade({CascadeType.SAVE_UPDATE})
    private SubjectContact defaultPhone;

    private transient List<SubjectContact> aContact;

    @JsonProperty(value = "nID_Sex")
    @Column(name = "nID_Sex")
    private Integer nID_Sex;

    public int getnID_Sex() {
        return nID_Sex;
    }

    public void setnID_Sex(Integer nID_Sex) {
        this.nID_Sex = nID_Sex;
    }

//    public String getsID_Sex() {
//        if (nID_Sex == null) {
//            return null;
//        } else {
//            return SubjectHumanSex.getBynID_Sex(nID_Sex).getsID_Sex();
//        }
//    }
    public Subject getoSubject() {
        return oSubject;
    }

    public void setoSubject(Subject oSubject) {
        this.oSubject = oSubject;
    }

    public String getsSB() {
        return sSB;
    }

    public void setsSB(String sSB) {
        this.sSB = sSB;
    }

    public String getsINN() {
        return sINN;
    }

    public void setsINN(String sINN) {
        this.sINN = sINN;
    }

    public String getsPassportSeria() {
        return sPassportSeria;
    }

    public void setsPassportSeria(String sPassportSeria) {
        this.sPassportSeria = sPassportSeria;
    }

    public String getsPassportNumber() {
        return sPassportNumber;
    }

    public void setsPassportNumber(String sPassportNumber) {
        this.sPassportNumber = sPassportNumber;
    }

    public String getsFamily() {
        return sFamily;
    }

    public void setsFamily(String sFamily) {
        this.sFamily = sFamily;
    }

    public String getsSurname() {
        return sSurname;
    }

    public void setsSurname(String sSurname) {
        this.sSurname = sSurname;
    }

    public SubjectHumanIdType getSubjectHumanIdType() {
        return subjectHumanIdType;
    }

    public void setSubjectHumanIdType(SubjectHumanIdType subjectHumanIdType) {
        this.subjectHumanIdType = subjectHumanIdType;
    }

    public SubjectContact getDefaultEmail() {
        return defaultEmail;
    }

    public void setDefaultEmail(SubjectContact defaultEmail) {
        this.defaultEmail = defaultEmail;
    }

    public SubjectContact getDefaultPhone() {
        return defaultPhone;
    }

    public void setDefaultPhone(SubjectContact defaultPhone) {
        this.defaultPhone = defaultPhone;
    }

    public List<SubjectContact> getaContact() {
        return aContact;
    }

    public void setaContact(List<SubjectContact> aContact) {
        this.aContact = aContact;
    }
}
