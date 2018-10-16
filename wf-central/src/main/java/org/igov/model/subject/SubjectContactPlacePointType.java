package org.igov.model.subject;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.igov.model.core.AbstractEntity;

import javax.persistence.Column;

/**
 * @author Oleksandr.Gurtovyi
 */

@javax.persistence.Entity
@ApiModel(description = "Тип контакта")
@ToString
public class SubjectContactPlacePointType extends AbstractEntity {
    @Column
    @ApiModelProperty(value = "Название на английском", required = false)
    private @Getter(onMethod = @_(@JsonProperty("sName_EN")))
    @Setter(onMethod = @_(@JsonProperty("sName_EN")))
    String sName_EN;

    @Column
    @ApiModelProperty(value = "Название на украинском", required = false)
    private @Getter(onMethod = @_(@JsonProperty("sName_UA")))
    @Setter(onMethod = @_(@JsonProperty("sName_UA")))
    String sName_UA;

    @Column
    @ApiModelProperty(value = "Название на русском", required = false)
    private @Getter(onMethod = @_(@JsonProperty("sName_RU")))
    @Setter(onMethod = @_(@JsonProperty("sName_RU")))
    String sName_RU;
}
