package com.opexos.imageuploader.image;

import com.opexos.imageuploader.BaseResponse;

/**
 * Describes image upload response
 */
public class ImageUploadResponse extends BaseResponse {
    private int[] idList;

    public int[] getIdList() {
        return idList;
    }

    public void setIdList(int[] idList) {
        this.idList = idList;
    }
}
