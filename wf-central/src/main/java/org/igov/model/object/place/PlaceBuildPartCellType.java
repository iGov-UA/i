package org.igov.model.object.place;

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
@ApiModel
@ToString
public class PlaceBuildPartCellType extends AbstractEntity {
    @Column
    @ApiModelProperty(value = "Ключ", required = false)
    private @Getter(onMethod = @_(@JsonProperty("sKey")))
    @Setter(onMethod = @_(@JsonProperty("sKey")))
    String sKey;

    @Column
    @ApiModelProperty(value = "Имя", required = false)
    private @Getter(onMethod = @_(@JsonProperty("sName")))
    @Setter(onMethod = @_(@JsonProperty("sName")))
    String sName;
}
