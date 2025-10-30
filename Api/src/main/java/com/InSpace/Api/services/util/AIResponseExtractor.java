package com.InSpace.Api.services.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class AIResponseExtractor {

    // Fix: escape backslash in the Java string literal so the regex engine receives a single-escaped
    // closing bracket (\]) inside the character class. This avoids the "illegal escape character" compile error.
    private static final Pattern TEXT_CONTENT_PATTERN = Pattern.compile("textContent=(.+?)(?:, metadata|\\])");

    public static Pattern getTEXT_CONTENT_PATTERN() {
        return TEXT_CONTENT_PATTERN;
    }

    private AIResponseExtractor() {
    }

    public static String extractTextContent(String response) {
        if (response == null || response.isEmpty()) {
            return null;
        }

        Matcher matcher = TEXT_CONTENT_PATTERN.matcher(response);

        if (matcher.find()) {
            String content = matcher.group(1);
            // Remove any trailing metadata or brackets and trim
            return content.trim();
        }

        return null;
    }
}
