package com.opexos.imageuploader.image;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * POJO that contains server statistics
 */
@Builder
@Data
@ApiModel("Service statistics")
public class ImageStatisticDTO {
    @ApiModelProperty("How many times was the Redis cache used")
    private Long redisUsedAmount;
    @ApiModelProperty("How many times was the Memcached cache used")
    private Long memcachedUsedAmount;
    @ApiModelProperty("How many images are stored in the service")
    private Long totalPictures;
}
