package com.github.jokerpper.mavenprojectversion.util;

import com.intellij.util.ResourceUtil;

import java.io.InputStream;
import java.net.URL;

public class ResourceUtils {

    @SuppressWarnings("all")
    public static InputStream getResourceAsStream(Class<?> loaderClass, String basePath, String fileName) {
        ClassLoader loader = null;
        try {
            loader = loaderClass.getClassLoader();
        } catch (Throwable ex) {
            //ignore
        }

        if (loader == null) {
            return null;
        }

        try {
            URL url = ResourceUtil.getResource(loader, basePath, fileName);
            if (url != null) {
                return url.openStream();
            }
        } catch (Throwable ex) {
            //ignore
        }


        try {
            return loader.getResourceAsStream(basePath + "/" + fileName);
        } catch (Throwable ex) {
            //ignore
        }


        return null;
    }


}
