package com.github.jokerpper.mavenprojectversion.support;

import com.github.jokerpper.mavenprojectversion.model.UpdateMavenVersionEffectModel;
import com.github.jokerpper.mavenprojectversion.util.StringUtils;
import com.github.jokerpper.mavenprojectversion.util.VersionUtils;
import org.jetbrains.idea.maven.dom.model.MavenDomDependency;
import org.jetbrains.idea.maven.dom.model.MavenDomParent;
import org.jetbrains.idea.maven.dom.model.MavenDomProjectModel;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.project.MavenProject;

import java.util.*;

public class UpdateMavenVersionEffectUtils {

    /**
     * 格式化显示
     *
     * @param rootProjectGroupId
     * @param rootProjectArtifactId
     * @param updateMavenVersionEffectModelList
     * @param newVersion
     * @param language
     * @return
     */
    public static String format(String rootProjectGroupId, String rootProjectArtifactId, List<UpdateMavenVersionEffectModel> updateMavenVersionEffectModelList, String newVersion, String language) {
        StringBuilder sb = new StringBuilder();
        sb.append("------------------------------------\r\n");
        if (updateMavenVersionEffectModelList.isEmpty()) {
            sb.append("------------------------------------\r\n");
        }

        for (UpdateMavenVersionEffectModel updateMavenVersionEffectModel : updateMavenVersionEffectModelList) {
            UpdateMavenVersionEffectModel.Detail projectDetail = updateMavenVersionEffectModel.getProjectDetail();

            sb.append(String.format("%s:%s \r\n", projectDetail.getGroupId(), projectDetail.getArtifactId()));
            sb.append(String.format("pom: %s \r\n", updateMavenVersionEffectModel.getPomPath()));

            if (Boolean.TRUE.equals(updateMavenVersionEffectModel.getIsChangeProjectVersion())) {
                if (StringUtils.equals("null", projectDetail.getVersion()) || StringUtils.trimToNull(projectDetail.getVersion()) == null) {
                    //未修改值-继承parent版本
                    sb.append("{");
                    sb.append(LanguageUtils.get(LanguageUtils.Constants.UPDATE_INFO_NOT_CHANGE_VERSION_WITH_EXTEND_PARENT_VERSION_TEXT, language));
                    sb.append("}");
                    sb.append("\r\n");
                } else {
                    //修改版本及显示具体的版本变更值
                    sb.append(String.format("%s: %s -> %s\r\n", LanguageUtils.get(LanguageUtils.Constants.UPDATE_INFO_CHANGE_VERSION_TEXT, language), projectDetail.getVersion(), newVersion));
                }
            } else {
                //未修改版本

                sb.append("{");

                //显示未修改及缘由
                if (!StringUtils.equals(rootProjectGroupId, projectDetail.getGroupId())) {
                    //来自外部groupId
                    sb.append(LanguageUtils.get(LanguageUtils.Constants.UPDATE_INFO_NOT_CHANGE_VERSION_WITH_FROM_OUTSIDE_TEXT, language));
                } else {
                    sb.append(LanguageUtils.get(LanguageUtils.Constants.UPDATE_INFO_NOT_CHANGE_VERSION_WITH_FROM_INSIDE_OTHER_TEXT, language));
                }
                sb.append("}");

                //显示版本值
                sb.append(String.format("\r\n%s: %s\r\n", LanguageUtils.get(LanguageUtils.Constants.UPDATE_INFO_VERSION_TEXT, language), projectDetail.getVersion()));

                sb.append("\r\n");
            }

            UpdateMavenVersionEffectModel.Detail projectParentDetail = updateMavenVersionEffectModel.getProjectParentDetail();
            if (projectParentDetail != null) {
                sb.append("\r\n");
                sb.append("[parent]: \r\n");

                sb.append(String.format("%s:%s\r\n", projectParentDetail.getGroupId(), projectParentDetail.getArtifactId()));
                if (Boolean.TRUE.equals(updateMavenVersionEffectModel.getIsChangeProjectParentVersion())) {
                    //修改版本及显示具体的版本变更值
                    sb.append(String.format("%s: %s -> %s\r\n", LanguageUtils.get(LanguageUtils.Constants.UPDATE_INFO_CHANGE_VERSION_TEXT, language), projectParentDetail.getVersion(), newVersion));
                } else {
                    //未修改版本
                    sb.append("{");
                    //显示未修改及缘由
                    if (!StringUtils.equals(rootProjectGroupId, projectParentDetail.getGroupId())) {
                        //来自外部groupId
                        sb.append(LanguageUtils.get(LanguageUtils.Constants.UPDATE_INFO_NOT_CHANGE_VERSION_WITH_FROM_OUTSIDE_TEXT, language));
                    } else {
                        //是否为root的parent
                        boolean isRootProjectParent = StringUtils.equals(rootProjectGroupId, projectDetail.getGroupId()) && StringUtils.equals(rootProjectArtifactId, projectDetail.getArtifactId());
                        if (isRootProjectParent) {
                            sb.append(LanguageUtils.get(LanguageUtils.Constants.UPDATE_INFO_NOT_CHANGE_VERSION_WITH_DEFAULT_TEXT, language));
                        } else {
                            sb.append(LanguageUtils.get(LanguageUtils.Constants.UPDATE_INFO_NOT_CHANGE_VERSION_WITH_FROM_INSIDE_OTHER_TEXT, language));
                        }
                    }
                    sb.append("}");

                    //显示版本值
                    sb.append(String.format("\r\n%s: %s\r\n", LanguageUtils.get(LanguageUtils.Constants.UPDATE_INFO_VERSION_TEXT, language), projectParentDetail.getVersion()));

                    sb.append("\r\n");
                }
            }

            Map<String, String[]> excerptAndToChangeVersionPropertiesMap = updateMavenVersionEffectModel.getExcerptAndToChangeVersionPropertiesMap();
            if (excerptAndToChangeVersionPropertiesMap != null && !excerptAndToChangeVersionPropertiesMap.isEmpty()) {
                sb.append("\r\n");
                sb.append("[properties]: \r\n");
                excerptAndToChangeVersionPropertiesMap.forEach((excerptAndToChangeVersionProperty, oldVersions) -> {
                    //修改版本及显示具体的版本变更值
                    sb.append(String.format("%s\r\n", excerptAndToChangeVersionProperty));
                    sb.append(String.format("%s: %s -> %s\r\n", LanguageUtils.get(LanguageUtils.Constants.UPDATE_INFO_CHANGE_VERSION_TEXT, language), oldVersions.length == 1 ? oldVersions[0] : Arrays.toString(oldVersions), newVersion));
                });
            }

            List<UpdateMavenVersionEffectModel.Detail> dependencyDetailList = updateMavenVersionEffectModel.getDependencyDetailList();
            if (dependencyDetailList != null && !dependencyDetailList.isEmpty()) {
                sb.append("\r\n");
                sb.append("[dependency]: \r\n");
                for (UpdateMavenVersionEffectModel.Detail dependencyDetail : dependencyDetailList) {
                    sb.append(String.format("%s:%s \r\n", dependencyDetail.getGroupId(), dependencyDetail.getArtifactId()));
                    //修改版本及显示具体的版本变更值
                    sb.append(String.format("%s: %s -> %s\r\n", LanguageUtils.get(LanguageUtils.Constants.UPDATE_INFO_CHANGE_VERSION_TEXT, language), dependencyDetail.getVersion(), newVersion));
                }
            }

            List<UpdateMavenVersionEffectModel.Detail> dependencyManagementDependencyDetailList = updateMavenVersionEffectModel.getDependencyManagementDependencyDetailList();
            if (dependencyManagementDependencyDetailList != null && !dependencyManagementDependencyDetailList.isEmpty()) {
                sb.append("\r\n");
                sb.append("[dependencyManagementDependency]: \r\n");
                for (UpdateMavenVersionEffectModel.Detail dependencyDetail : dependencyManagementDependencyDetailList) {
                    sb.append(String.format("%s:%s \r\n", dependencyDetail.getGroupId(), dependencyDetail.getArtifactId()));
                    //修改版本及显示具体的版本变更值
                    sb.append(String.format("%s: %s -> %s\r\n", LanguageUtils.get(LanguageUtils.Constants.UPDATE_INFO_CHANGE_VERSION_TEXT, language), dependencyDetail.getVersion(), newVersion));
                }
            }
            sb.append("------------------------------------\r\n");
        }
        return sb.toString();
    }

    public static void initProjectDetail(UpdateMavenVersionEffectModel updateMavenVersionEffectModel, MavenProject mavenProject, MavenId mavenId, MavenDomProjectModel rootElement, boolean isChangeProjectVersion) {
        UpdateMavenVersionEffectModel.Detail projectDetail = new UpdateMavenVersionEffectModel.Detail();
        projectDetail.setGroupId(mavenId.getGroupId());
        projectDetail.setArtifactId(mavenId.getArtifactId());
        projectDetail.setVersion(rootElement.getVersion().getRawText());
        updateMavenVersionEffectModel.setPomPath(mavenProject.getPath());
        updateMavenVersionEffectModel.setIsChangeProjectVersion(isChangeProjectVersion);
        updateMavenVersionEffectModel.setProjectDetail(projectDetail);
    }

    public static void initProjectParentDetail(UpdateMavenVersionEffectModel updateMavenVersionEffectModel, MavenDomParent mavenDomParent, boolean isChangeProjectParentVersion) {
        UpdateMavenVersionEffectModel.Detail projectParentDetail = new UpdateMavenVersionEffectModel.Detail();
        projectParentDetail.setGroupId(mavenDomParent.getGroupId().getRawText());
        projectParentDetail.setArtifactId(mavenDomParent.getArtifactId().getRawText());
        projectParentDetail.setVersion(mavenDomParent.getVersion().getRawText());
        updateMavenVersionEffectModel.setProjectParentDetail(projectParentDetail);
        updateMavenVersionEffectModel.setIsChangeProjectParentVersion(isChangeProjectParentVersion);
    }


    public static void initDependencyDetail(UpdateMavenVersionEffectModel updateMavenVersionEffectModel, MavenDomDependency mavenDomDependency) {
        List<UpdateMavenVersionEffectModel.Detail> detailList = updateMavenVersionEffectModel.getDependencyDetailList();
        if (detailList == null) {
            detailList = new ArrayList<>(32);
            updateMavenVersionEffectModel.setDependencyDetailList(detailList);
        }

        UpdateMavenVersionEffectModel.Detail detail = new UpdateMavenVersionEffectModel.Detail();
        detail.setGroupId(mavenDomDependency.getGroupId().getRawText());
        detail.setArtifactId(mavenDomDependency.getArtifactId().getRawText());
        detail.setVersion(mavenDomDependency.getVersion().getRawText());
        detailList.add(detail);
    }

    public static void initDependencyManagementDependencyDetail(UpdateMavenVersionEffectModel updateMavenVersionEffectModel, MavenDomDependency mavenDomDependency) {
        List<UpdateMavenVersionEffectModel.Detail> detailList = updateMavenVersionEffectModel.getDependencyManagementDependencyDetailList();
        if (detailList == null) {
            detailList = new ArrayList<>(32);
            updateMavenVersionEffectModel.setDependencyManagementDependencyDetailList(detailList);
        }

        UpdateMavenVersionEffectModel.Detail detail = new UpdateMavenVersionEffectModel.Detail();
        detail.setGroupId(mavenDomDependency.getGroupId().getRawText());
        detail.setArtifactId(mavenDomDependency.getArtifactId().getRawText());
        detail.setVersion(mavenDomDependency.getVersion().getRawText());
        detailList.add(detail);
    }

    public static void initExcerptAndShouldChangeVersionProperty(UpdateMavenVersionEffectModel updateMavenVersionEffectModel, String excerptVersionRawText) {
        Set<String> excerptAndToChangeVersionProperties = updateMavenVersionEffectModel.getExcerptAndShouldChangeVersionProperties();
        if (excerptAndToChangeVersionProperties == null) {
            excerptAndToChangeVersionProperties = new LinkedHashSet<>(32);
            updateMavenVersionEffectModel.setExcerptAndShouldChangeVersionProperties(excerptAndToChangeVersionProperties);
        }
        excerptAndToChangeVersionProperties.add(VersionUtils.getExcerptVariableProperty(excerptVersionRawText));
    }
}
