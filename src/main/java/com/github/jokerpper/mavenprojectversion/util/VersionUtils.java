package com.github.jokerpper.mavenprojectversion.util;

import com.intellij.psi.xml.XmlTag;
import org.jetbrains.idea.maven.dom.model.MavenDomProperties;

import java.util.*;

public class VersionUtils {

    /**
     * 是否为特殊版本值
     *
     * @param version
     * @return
     */
    public static boolean isSpecialVersion(String version) {
        for (String special : Arrays.asList("$", "(", "[", ")", "]")) {
            if (version.contains(special)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否为引用版本值
     *
     * @param version
     * @return
     */
    public static boolean isExcerptVersion(String version) {
        return StringUtils.isNotEmpty(version) && version.startsWith("${") && version.endsWith("}");
    }

    /**
     * 是否为引用的project.version
     *
     * @param version
     * @return
     */
    public static boolean isExcerptProjectVersion(String version) {
        return StringUtils.isNotEmpty(version) && version.equals("${project.version}");
    }

    /**
     * 获取引用的变量属性
     *
     * @param version
     * @return
     */
    public static String getExcerptVariableProperty(String version) {
        return version.substring(2, version.length() - 1);
    }

    /**
     * 版本是否相等
     *
     * @param version1
     * @param version2
     * @return
     */
    public static boolean isEquals(String version1, String version2) {
        return StringUtils.equals(StringUtils.trim(version1), StringUtils.trim(version2));
    }

    /**
     * 根据依赖直接引用修改版本的属性列表进行获取当前项目中对应的实际存在引用版本的变量属性Map
     *
     * @param excerptAndShouldChangeVersionProperties
     * @param mavenDomProperties
     * @return
     */
    public static Map<String, String[]> getCurrentProjectExistExcerptPropertiesMap(Collection<String> excerptAndShouldChangeVersionProperties,
                                                                                   MavenDomProperties mavenDomProperties) {
        if (!mavenDomProperties.exists() || excerptAndShouldChangeVersionProperties.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, String[]> resultMap = new LinkedHashMap<>(16);
        XmlTag mavenDomPropertiesXmlTag = mavenDomProperties.getXmlTag();
        excerptAndShouldChangeVersionProperties.forEach(excerptAndShouldChangeVersionProperty -> {
            XmlTag[] xmlTags = mavenDomPropertiesXmlTag.findSubTags(excerptAndShouldChangeVersionProperty);
            if (xmlTags == null || xmlTags.length == 0) {
                return;
            }
            String[] values = new String[xmlTags.length];
            int i = 0;
            for (XmlTag xmlTag : xmlTags) {
                values[i++] = xmlTag.getValue().getTrimmedText();
            }
            resultMap.put(excerptAndShouldChangeVersionProperty, values);
        });

        return resultMap;
    }

}

