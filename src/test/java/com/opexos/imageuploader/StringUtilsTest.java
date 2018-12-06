package com.opexos.imageuploader;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class StringUtilsTest {

    @Test
    public void testSubstringAfterLast1() {
        String result = StringUtils.substringAfterLast("123:456:7890", ":");
        assertThat(result, equalTo("7890"));
    }

    @Test
    public void testSubstringAfterLast2() {
        String result = StringUtils.substringAfterLast("1234567890", ":");
        assertThat(result, equalTo("1234567890"));
    }

    @Test
    public void testSubstringBeforeFirst1() {
        String result = StringUtils.substringBeforeFirst("123:456:7890", ":");
        assertThat(result, equalTo("123"));
    }

    @Test
    public void testSubstringBeforeFirst2() {
        String result = StringUtils.substringBeforeFirst("1234567890", ":");
        assertThat(result, equalTo("1234567890"));
    }
}