package com.opexos.imageuploader.image;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ImageStatisticConverter implements Converter<ImageService.Statistic, ImageStatisticDTO> {
    @Override
    public ImageStatisticDTO convert(ImageService.Statistic source) {
        return ImageStatisticDTO.builder()
                .memcachedUsedAmount(source.getMemcachedUsedAmount())
                .redisUsedAmount(source.getRedisUsedAmount())
                .totalPictures(source.getTotalPicturesSaved())
                .build();
    }
}
