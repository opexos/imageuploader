package com.opexos.imageuploader;

import org.springframework.lang.NonNull;

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
    public static String substringAfterLast(@NonNull String str, @NonNull String delimiter) {
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
    public static String substringBeforeFirst(@NonNull String str, @NonNull String delimiter) {
        int firstIndex = str.indexOf(delimiter);
        return firstIndex == -1 ? str : str.substring(0, firstIndex);
    }
}
