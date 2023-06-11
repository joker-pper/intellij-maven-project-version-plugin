package com.github.jokerpper.mavenprojectversion.constants;

import com.github.jokerpper.mavenprojectversion.util.StringUtils;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.util.BuildNumber;

public class SystemConstants {

    public static final String MAVEN_SHOW_SETTINGS_ACTION_ID = "Maven.ShowSettings";

    public static final String UPDATE_MAVEN_PROJECT_VERSION_ACTION_ID = "MavenProjectVersion.Update";

    public static final String SHOW_MAVEN_PROJECT_VERSION_ACTION_ID = "MavenProjectVersion.Show";

    public static final String DEFAULT_VERSION_RULE = "${v}";

    public static final String DEFAULT_PROJECT_VIEW_VERSION_RULE = "(~${v})";

    public static final String USER_HOME = System.getProperty("user.home");

    public static final String PROPERTIES_FILE_SUFFIX = ".properties";

    public static final String IDEA_VERSION_NAME;
    public static final String IDEA_VERSION;
    public static final int IDEA_BASE_LINE_VERSION;
    public static final String IDEA_PRODUCT_CODE;
    public static final String IDEA_PRODUCT_FULL_NAME;

    static {
        ApplicationInfo applicationInfo = ApplicationInfo.getInstance();
        if (applicationInfo != null) {
            IDEA_VERSION_NAME = applicationInfo.getVersionName();
            IDEA_VERSION = applicationInfo.getFullVersion();
            BuildNumber buildNumber = applicationInfo.getBuild();
            IDEA_PRODUCT_CODE = buildNumber.getProductCode();
            IDEA_BASE_LINE_VERSION = buildNumber.getBaselineVersion();
            IDEA_PRODUCT_FULL_NAME = PlatformProduct.getProductFullName(IDEA_VERSION_NAME, IDEA_VERSION, IDEA_PRODUCT_CODE);
        } else {
            IDEA_VERSION_NAME = "unknown";
            IDEA_VERSION = "unknown";
            IDEA_PRODUCT_CODE = "unknown";
            IDEA_BASE_LINE_VERSION = -1;
            IDEA_PRODUCT_FULL_NAME = "unknown";
        }
    }


    enum PlatformProduct {

        IDEA_IU("IU", "Ultimate Edition"),

        IDEA_IC("IC", "Community Edition"),

        IDEA_IE("IE", "Educational Edition");

        private final String productCode;

        private final String productName;

        PlatformProduct(String productCode, String productName) {
            this.productCode = productCode;
            this.productName = productName;
        }

        static String getProductFullName(String versionName, String version, String productCode) {
            PlatformProduct currentPlatformProduct = null;
            for (PlatformProduct platformProduct : PlatformProduct.values()) {
                if (StringUtils.equals(productCode, platformProduct.productCode)) {
                    currentPlatformProduct = platformProduct;
                    break;
                }
            }
            String result = versionName + " " + version;
            if (currentPlatformProduct == null) {
                return result;
            }
            return result + String.format("(%s)", currentPlatformProduct.productName);
        }
    }

}
