package com.opexos.imageuploader.image;

/**
 * Image entity. Business layer.
 */
public class Image {
    private int id;
    private byte[] data;

    public Image(int id, byte[] data) {
        this.id = id;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
