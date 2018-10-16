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
@ApiModel(description = "Здание")
@ToString
public class PlaceBuild extends AbstractEntity {

    @Column
    @ApiModelProperty(value = "Ключ", required = false)
    private @Getter(onMethod = @_(@JsonProperty("sKey")))
    @Setter(onMethod = @_(@JsonProperty("sKey")))
    String sKey;

    @ManyToOne
    @JoinColumn(name = "nID_PlaceBuildType")
    @ApiModelProperty(value = "Тип здания", required = true)
    private @Getter(onMethod = @_(@JsonProperty("oPlaceBuildType")))
    @Setter(onMethod = @_(@JsonProperty("oPlaceBuildType")))
    PlaceBuildType oPlaceBuildType;

    @ManyToOne
    @JoinColumn(name = "nID_PlaceBranch")
    @ApiModelProperty(value = "Тип здания", required = true)
    private @Getter(onMethod = @_(@JsonProperty("oPlaceBranch")))
    @Setter(onMethod = @_(@JsonProperty("oPlaceBranch")))
    PlaceBranch oPlaceBranch;

    @Column
    @ApiModelProperty(value = "Ключ", required = false)
    private @Getter(onMethod = @_(@JsonProperty("sKey_MailIndex")))
    @Setter(onMethod = @_(@JsonProperty("sKey_MailIndex")))
    String sKey_MailIndex;
}
