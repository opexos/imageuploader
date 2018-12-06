package com.opexos.imageuploader;

import java.util.Random;

public class Helpers {

    /**
     * Generates an array of bytes of a given size
     *
     * @param size size of generated array
     */
    public static byte[] getRandomByteArray(int size) {
        byte[] result = new byte[size];
        new Random().nextBytes(result);
        return result;
    }

    /**
     * Generates a Base64 string with a given size random data.
     * Specified size mean data length in bytes, not string length.
     * Length of string in Base64 encode always larger than bytes size.
     *
     * @param dataSize amount of bytes that will need to encode to Base64 string
     */
    public static String getRandomBase64(int dataSize) {
        byte[] data = getRandomByteArray(dataSize);
        return Utils.encodeToBase64(data);
    }

}
