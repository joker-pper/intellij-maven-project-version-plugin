package com.github.jokerpper.mavenprojectversion.util;

import java.util.Arrays;

public class VersionUtils {

    public static boolean isSpecialVersion(String version) {
        for (String special : Arrays.asList("$", "(", "[", ")", "]")) {
            if (version.contains(special)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEquals(String version1, String version2) {
        return StringUtils.equals(StringUtils.trim(version1), StringUtils.trim(version2));
    }
}

