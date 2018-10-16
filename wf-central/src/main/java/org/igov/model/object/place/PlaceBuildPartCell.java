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
@ApiModel
@ToString
public class PlaceBuildPartCell extends AbstractEntity {
    @Column
    @ApiModelProperty(value = "Ключ", required = false)
    private @Getter(onMethod = @_(@JsonProperty("sKey")))
    @Setter(onMethod = @_(@JsonProperty("sKey")))
    String sKey;

    @Column
    @ApiModelProperty(value = "Имя", required = false)
    private @Getter(onMethod = @_(@JsonProperty("sNote")))
    @Setter(onMethod = @_(@JsonProperty("sNote")))
    String sNote;

    @ManyToOne
    @JoinColumn(name = "nID_PlaceBuildPartCellType")
    @ApiModelProperty(value = "Тип здания", required = true)
    private @Getter(onMethod = @_(@JsonProperty("oPlaceBuildPartCellType")))
    @Setter(onMethod = @_(@JsonProperty("oPlaceBuildPartCellType")))
    PlaceBuildPartCellType oPlaceBuildPartCellType;

    @ManyToOne
    @JoinColumn(name = "nID_PlaceBuildPart")
    @ApiModelProperty(value = "Тип здания", required = true)
    private @Getter(onMethod = @_(@JsonProperty("oPlaceBuildPart")))
    @Setter(onMethod = @_(@JsonProperty("oPlaceBuildPart")))
    PlaceBuildPart oPlaceBuildPart;
}

