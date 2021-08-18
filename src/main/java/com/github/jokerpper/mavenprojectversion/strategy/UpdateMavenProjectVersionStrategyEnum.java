package com.github.jokerpper.mavenprojectversion.strategy;

import com.github.jokerpper.mavenprojectversion.ui.UpdateMavenProjectVersionForm;
import com.github.jokerpper.mavenprojectversion.util.VersionUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

public enum UpdateMavenProjectVersionStrategyEnum implements UpdateMavenProjectVersionStrategy {

    DEFAULT {
        @Override
        public boolean checkVersionPass(final Project project, final UpdateMavenProjectVersionForm updateMavenProjectVersionForm) {
            if (updateMavenProjectVersionForm.getNewVersion() == null) {
                Messages.showWarningDialog(project, "New Version Must Be Not Empty", "Warning");
                return false;
            }

            if (VersionUtils.isEquals(updateMavenProjectVersionForm.getOldVersion(), updateMavenProjectVersionForm.getNewVersion())) {
                //新版本必须与之前不一致
                Messages.showMessageDialog(project, "Not Changed Version", "Warning", Messages.getWarningIcon());
                return false;
            }
            return true;
        }

        @Override
        public boolean isUpdateProjectVersion(final Project project, final UpdateMavenProjectVersionForm updateMavenProjectVersionForm, final String beforeVersion) {
            if (beforeVersion == null) {
                //不存在版本时
                return false;
            }

            if (!updateMavenProjectVersionForm.isMustSameVersion() || VersionUtils.isEquals(updateMavenProjectVersionForm.getOldVersion(), beforeVersion)) {
                //不检查版本相等或当前的版本与之前版本一致时
                return true;
            }

            return false;
        }

        @Override
        public boolean isUpdateProjectParentVersion(final Project project, final UpdateMavenProjectVersionForm updateMavenProjectVersionForm, final String parentBeforeVersion) {
            if (parentBeforeVersion == null) {
                //不存在版本时
                return false;
            }

            if (!updateMavenProjectVersionForm.isMustSameVersion() || VersionUtils.isEquals(updateMavenProjectVersionForm.getOldVersion(), parentBeforeVersion)) {
                //不检查版本相等或当前的版本与之前版本一致时
                return true;
            }

            return false;
        }

        @Override
        public boolean isUpdateProjectDependencyVersion(final Project project, final UpdateMavenProjectVersionForm updateMavenProjectVersionForm, final String beforeVersion) {
            if (beforeVersion == null) {
                //不存在版本时
                return false;
            }

            if (VersionUtils.isSpecialVersion(beforeVersion)) {
                //special version not update, e.g: ${project.version} (1.7, 1.8]
                return false;
            }

            if (!updateMavenProjectVersionForm.isMustSameVersion() || VersionUtils.isEquals(updateMavenProjectVersionForm.getOldVersion(), beforeVersion)) {
                //不检查版本相等或当前的版本与之前版本一致时
                return true;
            }

            return false;
        }

        @Override
        public boolean isUpdateProjectDependencyManagementDependencyVersion(final Project project, final UpdateMavenProjectVersionForm updateMavenProjectVersionForm, final String beforeVersion) {
            return isUpdateProjectDependencyVersion(project, updateMavenProjectVersionForm, beforeVersion);
        }

    },

    GENERAL {
        @Override
        public boolean checkVersionPass(final Project project, final UpdateMavenProjectVersionForm updateMavenProjectVersionForm) {
            if (updateMavenProjectVersionForm.getNewVersion() == null) {
                Messages.showWarningDialog(project, "New Version Must Be Not Empty", "Warning");
                return false;
            }
            return true;
        }

        @Override
        public boolean isUpdateProjectVersion(final Project project, final UpdateMavenProjectVersionForm updateMavenProjectVersionForm, final String beforeVersion) {
            if (beforeVersion == null) {
                //不存在版本时
                return false;
            }

            if (!updateMavenProjectVersionForm.isMustSameVersion() || VersionUtils.isEquals(updateMavenProjectVersionForm.getOldVersion(), beforeVersion)) {
                //不检查版本相等或当前的版本与之前版本一致时
                return true;
            }

            return false;
        }

        @Override
        public boolean isUpdateProjectParentVersion(final Project project, final UpdateMavenProjectVersionForm updateMavenProjectVersionForm, final String parentBeforeVersion) {
            if (parentBeforeVersion == null) {
                //不存在版本时
                return false;
            }

            if (!updateMavenProjectVersionForm.isMustSameVersion() || VersionUtils.isEquals(updateMavenProjectVersionForm.getOldVersion(), parentBeforeVersion)) {
                //不检查版本相等或当前的版本与之前版本一致时
                return true;
            }

            return false;
        }

        @Override
        public boolean isUpdateProjectDependencyVersion(final Project project, final UpdateMavenProjectVersionForm updateMavenProjectVersionForm, final String beforeVersion) {
            if (beforeVersion == null) {
                //不存在版本时
                return false;
            }

            if (!updateMavenProjectVersionForm.isMustSameVersion() || VersionUtils.isEquals(updateMavenProjectVersionForm.getOldVersion(), beforeVersion)) {
                //不检查版本相等或当前的版本与之前版本一致时
                return true;
            }

            return false;
        }

        @Override
        public boolean isUpdateProjectDependencyManagementDependencyVersion(final Project project, final UpdateMavenProjectVersionForm updateMavenProjectVersionForm, final String beforeVersion) {
            return isUpdateProjectDependencyVersion(project, updateMavenProjectVersionForm, beforeVersion);
        }

    }

}


