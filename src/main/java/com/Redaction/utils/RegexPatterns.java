package com.Redaction.utils;

import java.util.regex.Pattern;

public class RegexPatterns {

    public static final Pattern EMAIL_PATTERN = Pattern.compile(
            "(?i)[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}"
    );

    public static final Pattern PHONE_PATTERN = Pattern.compile(
            "(?i)(\\+\\d{1,3}[- ]?)?(\\(\\d{2,4}\\)[- ]?)?\\d{3,4}[- ]?\\d{4}"
    );

    public static final Pattern NAME_SIMPLE_PATTERN = Pattern.compile(
            "\\b([A-Z][a-zA-Z'-]+(?:\\s+[A-Z][a-zA-Z'-]+)+)\\b"
    );

    public static final Pattern ADDRESS_SIMPLE_PATTERN = Pattern.compile(
            "(?i)\\b\\d+[A-Za-z0-9/]*\\s+(?:[A-Za-z]+\\s?){1,4}(Road|Street|St|Avenue|Ave|Lane|Ln|Boulevard|Blvd|Nagar|Colony|Sector|Block)\\b"
    );
}