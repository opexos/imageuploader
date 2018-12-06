package com.opexos.imageuploader.matchers;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.core.SubstringMatcher;

/**
 * Tests if the argument is a string that contains a substring (ignore case). *
 */
public class StringContainsIgnoreCase extends SubstringMatcher {
    public StringContainsIgnoreCase(String substring) {
        super(substring);
    }

    @Override
    protected boolean evalSubstringOf(String s) {
        return s.toLowerCase().contains(substring.toLowerCase());
    }

    @Override
    protected String relationship() {
        return "containing";
    }

    /**
     * Creates a matcher that matches if the examined {@link String} contains the specified
     * {@link String} anywhere (ignore case).
     * <p/>
     * For example:
     * <pre>assertThat("myStringOfNote", containsString("ringof"))</pre>
     *
     * @param substring
     *     the substring that the returned matcher will expect to find within any examined string (ignore case)
     *
     */
    @Factory
    public static Matcher<String> containsStringIgnoreCase(String substring) {
        return new StringContainsIgnoreCase(substring);
    }

}