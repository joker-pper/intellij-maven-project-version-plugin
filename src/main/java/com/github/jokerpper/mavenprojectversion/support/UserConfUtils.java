package com.github.jokerpper.mavenprojectversion.support;

import java.util.Properties;

public class UserConfUtils {

    static Properties USER_CONF_CACHE = new Properties();

    /**
     * 初始化用户定义配置
     *
     * @param userConfProperties
     */
    static void initUserConfCache(Properties userConfProperties) {
        USER_CONF_CACHE.clear();
        USER_CONF_CACHE.putAll(userConfProperties);
    }

    public static String getProperty(String key) {
        return USER_CONF_CACHE.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return USER_CONF_CACHE.getProperty(key, defaultValue);
    }

    public static <T> T getProperty(Class<T> tClass, String key, T defaultValue) {
        if (tClass == null) {
            throw new IllegalArgumentException("tClass must be not null");
        }

        if (tClass == String.class) {
            return (T) getProperty(key, (String) defaultValue);
        }

        String value = getProperty(key);
        if (tClass == int.class || tClass == Integer.class) {
            if (value == null || value.isEmpty()) {
                return defaultValue;
            }

            try {
                return (T) Integer.valueOf(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }

        if (tClass == long.class || tClass == Long.class) {
            if (value == null || value.isEmpty()) {
                return defaultValue;
            }

            try {
                return (T) Long.valueOf(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }

        if (tClass == float.class || tClass == Float.class) {
            if (value == null || value.isEmpty()) {
                return defaultValue;
            }

            try {
                return (T) Float.valueOf(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }

        if (tClass == double.class || tClass == Double.class) {
            if (value == null || value.isEmpty()) {
                return defaultValue;
            }

            try {
                return (T) Double.valueOf(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }

        if (tClass == boolean.class || tClass == Boolean.class) {
            if (value == null || value.isEmpty()) {
                return defaultValue;
            }
            return (T) Boolean.valueOf(value);
        }

        throw new IllegalArgumentException(String.format("tClass %s not support", tClass.getName()));
    }


}
