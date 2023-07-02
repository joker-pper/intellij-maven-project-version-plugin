package com.github.jokerpper.mavenprojectversion.support;

import com.github.jokerpper.mavenprojectversion.constants.SystemConstants;
import com.github.jokerpper.mavenprojectversion.util.PropertiesUtils;
import com.github.jokerpper.mavenprojectversion.util.ResourceUtils;
import com.github.jokerpper.mavenprojectversion.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LanguageUtils {

    /**
     * 获取值
     *
     * @param key
     * @return
     */
    public static String get(String key) {
        return get(key, getCurrentLanguage());
    }

    /**
     * 获取转换后的值
     *
     * @param source
     * @param values
     * @return
     */
    public static String parseTemplateValue(String source, Object... values) {
        return String.format(source.replace("{}", "%s"), values);
    }

    /**
     * 获取转换后的值
     *
     * @param key
     * @param values
     * @return
     */
    public static String parseTemplateValueByKey(String key, Object... values) {
        String currentLanguage = getCurrentLanguage();
        String source = get(key, currentLanguage);
        try {
            return parseTemplateValue(source, values);
        } catch (Exception exception) {
            throw new RuntimeException(String.format("Parse Template Value Fail, Please Check Language Configuration And To Fix! Language: %s, Language Key: %s, Language Key Value: %s", currentLanguage, key, source), exception);
        }
    }

    /**
     * 获取当前生效的语言
     *
     * @return
     */
    public static String getCurrentLanguage() {
        return Constants.USED_LANGUAGE;
    }

    /**
     * 获取值
     *
     * @param key
     * @param language
     * @return
     */
    public static String get(String key, String language) {
        String result = Constants.get(key, language);
        if (StringUtils.isNotEmpty(result)) {
            return result;
        }

        String defaultLanguage = Constants.EN_LANGUAGE;
        if (StringUtils.equals(language, defaultLanguage)) {
            return result;
        }

        //未找到值时进行兜底
        return Constants.get(key, defaultLanguage);
    }

    public static class Constants {

        public static final String SYS_LANGUAGE_DIR = "/META-INF/language";

        public static final String USER_CONF_LANGUAGE_KEY = "my.language";

        public static String ZH_LANGUAGE = "zh";
        public static String ZH_CN_LANGUAGE = "zh_CN";
        public static String ZH_TW_LANGUAGE = "zh_TW";
        public static String EN_LANGUAGE = "en";
        public static String[] SYS_LANGUAGES = new String[]{ZH_LANGUAGE, ZH_CN_LANGUAGE, ZH_TW_LANGUAGE, EN_LANGUAGE};

        public static String MESSAGES_SUCCESS_TITLE = "messages_success_title";
        public static String MESSAGES_WARNING_TITLE = "messages_warning_title";
        public static String MESSAGES_ERROR_TITLE = "messages_error_title";
        public static String OK_BUTTON_TEXT = "ok_button_text";
        public static String CANCEL_BUTTON_TEXT = "cancel_button_text";
        public static String MESSAGES_TIP_OK_TEXT = "messages_tip_ok_text";
        public static String MESSAGES_SHOW_DEFAULT_ERROR_MORE_INFO_DIALOG_MESSAGE_TEXT = "messages_show_default_error_more_info_dialog_message_text";


        public static String UPDATE_MAVEN_PROJECT_VERSION_TITLE = "update_maven_project_version.title";

        public static String UPDATE_FORM_STRATEGY_TEXT = "update_form_strategy_text";
        public static String UPDATE_FORM_STRATEGY_DEFAULT_TEXT = "update_form_strategy_default_text";
        public static String UPDATE_FORM_STRATEGY_GENERAL_TEXT = "update_form_strategy_general_text";
        public static String UPDATE_FORM_MAVEN_PROJECT_TEXT = "update_form_maven_project_text";
        public static String UPDATE_FORM_NEW_VERSION_TEXT = "update_form_new_version_text";
        public static String UPDATE_FORM_MUST_SAME_VERSION_TEXT = "update_form_must_same_version_text";

        public static String UPDATE_FORM_TIP_NEW_VERSION_MUST_BE_NOT_EMPTY_TEXT = "update_form_tip_new_version_must_be_not_empty_text";
        public static String UPDATE_FORM_TIP_NOT_CHANGED_VERSION_TEXT = "update_form_tip_not_changed_version_text";

        public static String SHOW_MAVEN_PROJECT_VERSION_TITLE = "show_maven_project_version.title";

        public static String SHOW_FORM_PROJECT_VIEW_VERSION_RULE_TEXT = "show_form_project_view_version_rule_text";
        public static String SHOW_FORM_SHOW_PROJECT_VIEW_TEXT = "show_form_show_project_view_text";
        public static String SHOW_FORM_SHOW_STRUCTURE_VIEW_TEXT = "show_form_show_structure_view_text";
        public static String SHOW_FORM_TIP_PROJECT_VIEW_VERSION_RULE_NOT_SUPPORT_TEMPLATE_TEXT = "show_form_tip_project_view_version_rule_not_support_template_text";
        public static String SHOW_INFO_SUCCESS_TEXT = "show_info_success_text";


        public static String UPDATE_INFO_VERSION_TEXT = "update_info_version_text";

        public static String UPDATE_INFO_CHANGE_VERSION_TEXT = "update_info_change_version_text";

        public static String UPDATE_INFO_NOT_CHANGE_VERSION_WITH_DEFAULT_TEXT = "update_info_not_change_version_with_default_text";
        public static String UPDATE_INFO_NOT_CHANGE_VERSION_WITH_EXTEND_PARENT_VERSION_TEXT = "update_info_not_change_version_with_extend_parent_version_text";

        public static String UPDATE_INFO_NOT_CHANGE_VERSION_WITH_FROM_OUTSIDE_TEXT = "update_info_not_change_version_with_from_outside_text";

        public static String UPDATE_INFO_NOT_CHANGE_VERSION_WITH_FROM_INSIDE_OTHER_TEXT = "update_info_not_change_version_with_from_inside_other_text";

        public static String UPDATE_INFO_UPDATE_VERSION_EFFECT_SIZE_IS_ZERO_TEXT = "update_info_update_version_effect_size_is_zero_text";

        public static String UPDATE_INFO_SUCCESS_TEMPLATE = "update_info_success_template";

        static Map<String, Properties> SYS_LAN_CACHE = new HashMap<>(32);

        static Map<String, Properties> USER_LAN_CACHE = new HashMap<>(64);

        /**
         * 使用的语言
         */
        static String USED_LANGUAGE = null;

        static {
            init();
        }

        static void init() {
            //加载系统内部语言资源缓存
            for (String language : SYS_LANGUAGES) {
                initSysCache(language);
            }

        }

        static void initSysCache(String language) {
            String languageFile = language + SystemConstants.PROPERTIES_FILE_SUFFIX;
            InputStream inputStream = ResourceUtils.getResourceAsStream(LanguageUtils.class, SYS_LANGUAGE_DIR, languageFile);
            if (inputStream != null) {
                Properties properties = new Properties();
                try {
                    PropertiesUtils.loadProperties(properties, inputStream);
                } catch (IOException e) {
                    //ignore
                }
                SYS_LAN_CACHE.put(language, properties);
            }
        }

        /**
         * 初始化用户定义语言资源
         *
         * @param languagePropertiesMap
         */
        static void initUserCache(Map<String, Properties> languagePropertiesMap) {
            USER_LAN_CACHE.clear();
            USER_LAN_CACHE.putAll(languagePropertiesMap);
        }


        /**
         * 初始化用户使用的语言
         */
        static void initUsedLanguage() {
            USED_LANGUAGE = getUsedLanguage();
        }

        /**
         * 获取用户使用的语言
         *
         * @return
         */
        private static String getUsedLanguage() {
            String language = UserConfUtils.getProperty(USER_CONF_LANGUAGE_KEY);
            String connector = "_";

            if (StringUtils.isEmpty(language)) {
                //当配置文件中不存在时获取系统设置的语言
                Locale locale = Locale.getDefault();
                language = Stream.of(locale.getLanguage(), locale.getCountry()).filter(StringUtils::isNotEmpty).collect(Collectors.joining(connector));
            }

            if (SYS_LAN_CACHE.containsKey(language) || USER_LAN_CACHE.containsKey(language)) {
                //若存在该语言资源时直接返回
                return language;
            }

            if (language.contains(connector) && !language.endsWith(connector)) {
                //尝试获取不带国家的语言资源
                String currentLanguage = language.substring(0, language.indexOf(connector));
                if (SYS_LAN_CACHE.containsKey(currentLanguage) || USER_LAN_CACHE.containsKey(currentLanguage)) {
                    return currentLanguage;
                }
            }

            return EN_LANGUAGE;
        }

        /**
         * 获取值 (用户定义资源>系统定义资源)
         *
         * @param key
         * @param language
         * @return
         */
        private static String get(String key, String language) {
            Properties properties = USER_LAN_CACHE.get(language);
            if (properties != null) {
                String value = properties.getProperty(key);
                if (StringUtils.isNotEmpty(value)) {
                    return value;
                }
            }

            properties = SYS_LAN_CACHE.get(language);
            if (properties == null) {
                return null;
            }
            return properties.getProperty(key);
        }


    }
}
