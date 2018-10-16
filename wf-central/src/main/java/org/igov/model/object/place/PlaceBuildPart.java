package org.igov.model.object.place;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.igov.model.core.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * @author Oleksandr.Gurtovyi
 */

@javax.persistence.Entity
@ApiModel(description = "Корпус")
@ToString
public class PlaceBuildPart extends AbstractEntity {
    @Column
    @ApiModelProperty(value = "Ключ", required = false)
    private @Getter(onMethod = @_(@JsonProperty("sKey")))
    @Setter(onMethod = @_(@JsonProperty("sKey")))
    String sKey;

    @ManyToOne
    @JoinColumn(name = "nID_PlaceBuildPartType")
    @ApiModelProperty(value = "Тип здания", required = true)
    private @Getter(onMethod = @_(@JsonProperty("oPlaceBuildPartType")))
    @Setter(onMethod = @_(@JsonProperty("oPlaceBuildPartType")))
    PlaceBuildPartType oPlaceBuildPartType;

    @ManyToOne
    @JoinColumn(name = "nID_PlaceBuild")
    @ApiModelProperty(value = "Тип здания", required = true)
    private @Getter(onMethod = @_(@JsonProperty("oPlaceBuild")))
    @Setter(onMethod = @_(@JsonProperty("oPlaceBuild")))
    PlaceBuild oPlaceBuild;
}
