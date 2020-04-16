package com.opexos.imageuploader.commondto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * POJO that contains list of identifiers
 */
@Data
@AllArgsConstructor
@ApiModel("List of ids")
public class IdsDTO {
    @ApiModelProperty("Identifiers")
    private List<Long> ids;
}
