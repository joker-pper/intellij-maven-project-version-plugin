package com.github.jokerpper.mavenprojectversion.util;


public class StringUtils {

    public static String trim(String value) {
        return value != null ? value.trim() : null;
    }

    public static String trimToNull(String value) {
        String result = trim(value);
        if (result != null && result.length() == 0) {
            result = null;
        }
        return result;
    }

    public static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

    public static boolean equals(String value1, String value2) {
        if (value1 == value2) {
            return true;
        }
        if (value1 != null) {
            return value1.equals(value2);
        }
        return false;
    }
}