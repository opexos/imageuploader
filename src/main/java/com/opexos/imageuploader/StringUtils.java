package com.opexos.imageuploader;

/**
 * Helper methods for strings
 */
public class StringUtils {
    /**
     * Returns a substring after the last occurrence of delimiter.
     * If the string does not contain the delimiter, returns source string.
     *
     * @param str       string
     * @param delimiter delimiter
     */
    public static String substringAfterLast(String str, String delimiter) {
        if (str == null)
            throw new IllegalArgumentException("str cannot be null");
        if (delimiter == null)
            throw new IllegalArgumentException("delimiter cannot be null");

        int lastIndex = str.lastIndexOf(delimiter);
        return lastIndex == -1 ? str : str.substring(lastIndex + 1);
    }

    /**
     * Returns a substring before the first occurrence of delimiter.
     * If the string does not contain the delimiter, returns source string.
     *
     * @param str       string
     * @param delimiter delimiter
     */
    public static String substringBeforeFirst(String str, String delimiter) {
        if (str == null)
            throw new IllegalArgumentException("str cannot be null");
        if (delimiter == null)
            throw new IllegalArgumentException("delimiter cannot be null");

        int firstIndex = str.indexOf(delimiter);
        return firstIndex == -1 ? str : str.substring(0, firstIndex);
    }
}
