package org.igov.model.subject;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.Type;
import org.igov.model.core.AbstractEntity;
import org.igov.model.object.place.*;
import org.igov.util.JSON.JsonDateTimeDeserializer;
import org.igov.util.JSON.JsonDateTimeSerializer;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * @author Oleksandr.Gurtovyi
 */

@javax.persistence.Entity
@ApiModel(description = "Контакт здания")
public class SubjectContactPlacePoint extends AbstractEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nID_Subject")
    @JsonBackReference
    @ApiModelProperty(value = "Номер-ИД субьекта (на что 'вяжется' запись)", required = true)
    private
    Subject oSubject;

    @ManyToOne
    @JoinColumn(name = "nID_SubjectContactPlacePointType")
    @ApiModelProperty(value = "Тип контакта", required = true)
    private
    SubjectContactPlacePointType subjectContactPlacePointType;

    @ManyToOne
    @JoinColumn(name = "nID_Place")
    @ApiModelProperty(value = "Населений пункт", required = true)
    private
    Place oPlace;

    @ManyToOne
    @JoinColumn(name = "nID_Place_Region")
    @ApiModelProperty(value = "Населений пункт", required = true)
    private
    Place oPlace_Region;

    @ManyToOne
    @JoinColumn(name = "nID_PlaceBranch")
    @ApiModelProperty(value = "Назва проїзду", required = true)
    private
    PlaceBranch oPlaceBranch;

    @ManyToOne
    @JoinColumn(name = "nID_PlaceBuild")
    @ApiModelProperty(value = "Здание", required = true)
    private
    PlaceBuild oPlaceBuild;

    @ManyToOne
    @JoinColumn(name = "nID_PlaceBuildPart")
    @ApiModelProperty(value = "Номер корпусу", required = true)
    private
    PlaceBuildPart oPlaceBuildPart;

    @ManyToOne
    @JoinColumn(name = "nID_PlaceBuildPartCell")
    @ApiModelProperty(value = "Номер помешкання", required = true)
    private
    PlaceBuildPartCell oPlaceBuildPartCell;

    @JsonIgnore
    @ApiModelProperty(value = "Массив контактов субьекта", required = false)
    @Transient
    private transient String sValue;

    @JsonProperty(value = "sDate")
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    @JsonDeserialize(using = JsonDateTimeDeserializer.class)
    @Type(type = DATETIME_TYPE)
    @Column(name = "sDate")
    @ApiModelProperty(value = "Дата актуальности", required = false)
    private
    DateTime sDate;

    public String getsValue() {
        return sValue;
    }

    public Subject getoSubject() {
        return oSubject;
    }

    public void setoSubject(Subject oSubject) {
        this.oSubject = oSubject;
    }

    public SubjectContactPlacePointType getSubjectContactPlacePointType() {
        return subjectContactPlacePointType;
    }

    public void setSubjectContactPlacePointType(SubjectContactPlacePointType subjectContactPlacePointType) {
        this.subjectContactPlacePointType = subjectContactPlacePointType;
    }

    public Place getoPlace() {
        return oPlace;
    }

    public void setoPlace(Place oPlace) {
        this.oPlace = oPlace;
    }

    public PlaceBranch getoPlaceBranch() {
        return oPlaceBranch;
    }

    public void setoPlaceBranch(PlaceBranch oPlaceBranch) {
        this.oPlaceBranch = oPlaceBranch;
    }

    public PlaceBuild getoPlaceBuild() {
        return oPlaceBuild;
    }

    public void setoPlaceBuild(PlaceBuild oPlaceBuild) {
        this.oPlaceBuild = oPlaceBuild;
    }

    public PlaceBuildPart getoPlaceBuildPart() {
        return oPlaceBuildPart;
    }

    public void setoPlaceBuildPart(PlaceBuildPart oPlaceBuildPart) {
        this.oPlaceBuildPart = oPlaceBuildPart;
    }

    public PlaceBuildPartCell getoPlaceBuildPartCell() {
        return oPlaceBuildPartCell;
    }

    public void setoPlaceBuildPartCell(PlaceBuildPartCell oPlaceBuildPartCell) {
        this.oPlaceBuildPartCell = oPlaceBuildPartCell;
    }

    public void setsValue(String sValue) {
        this.sValue = sValue;
    }

    public DateTime getsDate() {
        return sDate;
    }

    public void setsDate(DateTime sDate) {
        this.sDate = sDate;
    }

    public Place getoPlace_Region() {
        return oPlace_Region;
    }

    public void setoPlace_Region(Place oPlace_Region) {
        this.oPlace_Region = oPlace_Region;
    }
}
