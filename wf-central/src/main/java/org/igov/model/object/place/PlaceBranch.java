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
@ApiModel(description = "Бранч")
@ToString
public class PlaceBranch extends AbstractEntity {
    @Column
    @ApiModelProperty(value = "Ключ", required = true)
    private @Getter(onMethod = @_(@JsonProperty("sKey")))
    @Setter(onMethod = @_(@JsonProperty("sKey")))
    String sKey;

    @Column
    @ApiModelProperty(value = "Имя", required = false)
    private @Getter(onMethod = @_(@JsonProperty("sName")))
    @Setter(onMethod = @_(@JsonProperty("sName")))
    String sName;
    @Column
    @ApiModelProperty(value = "Старое имя", required = false)
    private @Getter(onMethod = @_(@JsonProperty("sName_Old")))
    @Setter(onMethod = @_(@JsonProperty("sName_Old")))
    String sName_Old;

    @ManyToOne
    @JoinColumn(name = "nID_PlaceBranchType")
    @ApiModelProperty(value = "Тип здания", required = true)
    private @Getter(onMethod = @_(@JsonProperty("oPlaceBranchType")))
    @Setter(onMethod = @_(@JsonProperty("oPlaceBranchType")))
    PlaceBranchType oPlaceBranchType;

    @ManyToOne
    @JoinColumn(name = "nID_Place")
    @ApiModelProperty(value = "Тип здания", required = true)
    private @Getter(onMethod = @_(@JsonProperty("oPlace")))
    @Setter(onMethod = @_(@JsonProperty("oPlace")))
    Place oPlace;

}
