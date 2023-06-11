package com.github.jokerpper.mavenprojectversion.util;


import org.apache.http.util.CharArrayBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

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

    public static String toString(InputStream inStream, Charset charset) throws IOException {
        if (inStream == null) {
            return null;
        }

        try {
            int capacity = 4096;
            final Reader reader = new InputStreamReader(inStream, charset);
            final CharArrayBuffer buffer = new CharArrayBuffer(capacity);
            final char[] tmp = new char[1024];
            int l;
            while ((l = reader.read(tmp)) != -1) {
                buffer.append(tmp, 0, l);
            }
            return buffer.toString();
        } finally {
            inStream.close();
        }
    }
}