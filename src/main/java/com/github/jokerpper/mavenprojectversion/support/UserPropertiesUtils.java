package com.github.jokerpper.mavenprojectversion.support;

import com.github.jokerpper.mavenprojectversion.constants.SystemConstants;
import com.github.jokerpper.mavenprojectversion.util.PropertiesUtils;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class UserPropertiesUtils {

    public static final String MPVP_DIR = "mpvp";
    public static final String IDEA_DIR = ".idea";

    public static final String CONF_FILE = "conf.properties";
    public static final String LANGUAGE_DIR = "language";


    static Properties GLOBAL_CONF_PROPERTIES;

    static Map<String, Properties> GLOBAL_LAN_CACHE;

    static Properties PROJECT_CONF_PROPERTIES;

    static Map<String, Properties> PROJECT_LAN_CACHE;

    /**
     * 初始化
     *
     * @param project
     */
    public static void init(@NotNull Project project) {

        GLOBAL_CONF_PROPERTIES = new Properties();
        GLOBAL_LAN_CACHE = new HashMap<>(32);

        PROJECT_CONF_PROPERTIES = new Properties();
        PROJECT_LAN_CACHE = new HashMap<>(32);

        //用户主目录
        String userHome = SystemConstants.USER_HOME;
        File globalParentDir = new File(userHome, MPVP_DIR);
        init(globalParentDir, GLOBAL_CONF_PROPERTIES, GLOBAL_LAN_CACHE);

        //用户当前工作目录
        File projectParentDir = new File(project.getBasePath(), IDEA_DIR + File.separator + MPVP_DIR);
        init(projectParentDir, PROJECT_CONF_PROPERTIES, PROJECT_LAN_CACHE);

        //初始化配置
        UserConfUtils.initUserConfCache(getUserConfProperties());

        //初始化语言资源及使用的语言
        LanguageUtils.Constants.initUserCache(getUserLanguageMap());
        LanguageUtils.Constants.initUsedLanguage();
    }

    /**
     * 初始化
     *
     * @param parentDir
     * @param confProperties
     * @param languagePropertiesMap
     */
    static void init(File parentDir, Properties confProperties, Map<String, Properties> languagePropertiesMap) {
        if (parentDir == null || !parentDir.isDirectory()) {
            return;
        }

        //解析配置
        File confFile = new File(parentDir, CONF_FILE);
        if (confFile != null && confFile.exists()) {
            //存在时
            try {
                PropertiesUtils.loadProperties(confProperties, new FileInputStream(confFile));
            } catch (Exception e) {
                //ignore
            }
        }

        File languageDir = new File(parentDir, LANGUAGE_DIR);
        File[] languageFiles = null;
        if (languageDir != null && languageDir.isDirectory()) {
            languageFiles = languageDir.listFiles();
        }

        if (languageFiles != null && languageFiles.length > 0) {
            String languageFileSuffix = SystemConstants.PROPERTIES_FILE_SUFFIX;
            for (File languageFile : languageFiles) {
                if (!languageFile.getName().endsWith(languageFileSuffix)) {
                    continue;
                }

                //获取语言
                String language = languageFile.getName().replace(languageFileSuffix, "");
                Properties properties = new Properties();
                try {
                    PropertiesUtils.loadProperties(properties, new FileInputStream(languageFile));
                } catch (Exception e) {
                    //ignore
                }
                languagePropertiesMap.put(language, properties);
            }
        }

    }

    /**
     * 获取用户配置
     *
     * @return
     */
    static Properties getUserConfProperties() {
        Properties properties = new Properties();
        properties.putAll(GLOBAL_CONF_PROPERTIES);
        if (!PROJECT_CONF_PROPERTIES.isEmpty()) {
            properties.putAll(PROJECT_CONF_PROPERTIES);
        }
        return properties;
    }

    /**
     * 获取用户语言资源
     *
     * @return
     */
    static Map<String, Properties> getUserLanguageMap() {
        Map<String, Properties> resultMap = new HashMap<>(32);
        resultMap.putAll(GLOBAL_LAN_CACHE);

        Set<Map.Entry<String, Properties>> projectLanCacheEntrySet = PROJECT_LAN_CACHE.entrySet();
        for (Map.Entry<String, Properties> projectLanCacheEntry : projectLanCacheEntrySet) {
            String key = projectLanCacheEntry.getKey();
            Properties value = projectLanCacheEntry.getValue();
            if (!resultMap.containsKey(key)) {
                //不包含时
                resultMap.put(key, value);
                continue;
            }

            Properties beforeValue = resultMap.get(key);
            if (beforeValue == null || beforeValue.isEmpty()) {
                resultMap.put(key, value);
            } else {
                beforeValue.putAll(value);
            }
        }
        return resultMap;
    }


}
