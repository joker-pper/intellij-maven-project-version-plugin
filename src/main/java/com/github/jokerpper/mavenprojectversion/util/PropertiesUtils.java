package com.github.jokerpper.mavenprojectversion.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class PropertiesUtils {

    public static void loadProperties(Properties props, InputStream inputStream) throws IOException {
        try {
            props.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        } finally {
            inputStream.close();
        }
    }
}
