package com.github.jokerpper.mavenprojectversion.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UpdateMavenVersionEffectModel {

    @NotNull
    private String pomPath;

    @NotNull
    private Detail projectDetail;

    @NotNull
    private Boolean isChangeProjectVersion;

    @Nullable
    private Detail projectParentDetail;

    @NotNull
    private Boolean isChangeProjectParentVersion;

    @Nullable
    private List<Detail> dependencyDetailList;

    @Nullable
    private List<Detail> dependencyManagementDependencyDetailList;

    public String getPomPath() {
        return pomPath;
    }

    public void setPomPath(String pomPath) {
        this.pomPath = pomPath;
    }

    public Detail getProjectDetail() {
        return projectDetail;
    }

    public void setProjectDetail(Detail projectDetail) {
        this.projectDetail = projectDetail;
    }

    public Boolean getIsChangeProjectVersion() {
        return isChangeProjectVersion;
    }

    public void setIsChangeProjectVersion(boolean changeProjectVersion) {
        isChangeProjectVersion = changeProjectVersion;
    }

    public Detail getProjectParentDetail() {
        return projectParentDetail;
    }

    public void setProjectParentDetail(Detail projectParentDetail) {
        this.projectParentDetail = projectParentDetail;
    }

    public Boolean getIsChangeProjectParentVersion() {
        return isChangeProjectParentVersion;
    }

    public void setIsChangeProjectParentVersion(Boolean isChangeProjectParentVersion) {
        this.isChangeProjectParentVersion = isChangeProjectParentVersion;
    }

    public List<Detail> getDependencyDetailList() {
        return dependencyDetailList;
    }

    public void setDependencyDetailList(List<Detail> dependencyDetailList) {
        this.dependencyDetailList = dependencyDetailList;
    }

    public List<Detail> getDependencyManagementDependencyDetailList() {
        return dependencyManagementDependencyDetailList;
    }

    public void setDependencyManagementDependencyDetailList(List<Detail> dependencyManagementDependencyDetailList) {
        this.dependencyManagementDependencyDetailList = dependencyManagementDependencyDetailList;
    }

    public static class Detail {

        @NotNull
        private String groupId;

        @NotNull
        private String artifactId;

        @Nullable
        private String version;

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getArtifactId() {
            return artifactId;
        }

        public void setArtifactId(String artifactId) {
            this.artifactId = artifactId;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }


}
