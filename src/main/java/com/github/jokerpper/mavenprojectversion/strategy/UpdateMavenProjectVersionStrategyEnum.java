package com.github.jokerpper.mavenprojectversion.strategy;

import com.github.jokerpper.mavenprojectversion.ui.UpdateMavenProjectVersionForm;
import com.github.jokerpper.mavenprojectversion.util.VersionUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

public enum UpdateMavenProjectVersionStrategyEnum implements UpdateMavenProjectVersionStrategy {

    DEFAULT {
        @Override
        public boolean checkVersion(final Project project, final UpdateMavenProjectVersionForm updateMavenProjectVersionForm) {
            if (updateMavenProjectVersionForm.getNewVersion() == null) {
                Messages.showWarningDialog(project, "New Version Must Be Not Empty", "Warning");
                return false;
            }

            if (VersionUtils.isEquals(updateMavenProjectVersionForm.getOldVersion(), updateMavenProjectVersionForm.getNewVersion())) {
                Messages.showMessageDialog(project, "Not Changed Version", "Warning", Messages.getWarningIcon());
                return false;
            }
            return true;

        }

        @Override
        public boolean isUpdateProjectVersion(final Project project, final UpdateMavenProjectVersionForm updateMavenProjectVersionForm, final String beforeVersion) {
            if (beforeVersion == null) {
                return false;
            }

            if (!updateMavenProjectVersionForm.isMustSameVersion() || VersionUtils.isEquals(updateMavenProjectVersionForm.getOldVersion(), beforeVersion)) {
                return true;
            }

            return false;
        }

        @Override
        public boolean isUpdateProjectParentVersion(final Project project, final UpdateMavenProjectVersionForm updateMavenProjectVersionForm, final String parentBeforeVersion) {
            if (parentBeforeVersion == null) {
                return false;
            }

            if (!updateMavenProjectVersionForm.isMustSameVersion() || VersionUtils.isEquals(updateMavenProjectVersionForm.getOldVersion(), parentBeforeVersion)) {
                return true;
            }

            return false;
        }

        @Override
        public boolean isUpdateProjectDependency(final Project project, final UpdateMavenProjectVersionForm updateMavenProjectVersionForm, final String beforeVersion) {
            if (beforeVersion == null) {
                return false;
            }

            if (VersionUtils.isSpecialVersion(beforeVersion)) {
                //special version not update, e.g: ${project.version} (1.7, 1.8]
                return false;
            }

            if (!updateMavenProjectVersionForm.isMustSameVersion() || VersionUtils.isEquals(updateMavenProjectVersionForm.getOldVersion(), beforeVersion)) {
                return true;
            }

            return false;
        }

        @Override
        public boolean isUpdateProjectDependencyManagementDependency(final Project project, final UpdateMavenProjectVersionForm updateMavenProjectVersionForm, final String beforeVersion) {
            return isUpdateProjectDependency(project, updateMavenProjectVersionForm, beforeVersion);
        }

    },

    PROJECT_DEPENDENCIES {

        @Override
        public boolean checkVersion(final Project project, final UpdateMavenProjectVersionForm updateMavenProjectVersionForm) {
            if (updateMavenProjectVersionForm.getNewVersion() == null) {
                Messages.showWarningDialog(project, "New Version Must Be Not Empty", "Warning");
                return false;
            }
            return true;
        }

        @Override
        public boolean isUpdateProjectVersion(final Project project, final UpdateMavenProjectVersionForm updateMavenProjectVersionForm, final String beforeVersion) {
            return false;
        }

        @Override
        public boolean isUpdateProjectParentVersion(final Project project, final UpdateMavenProjectVersionForm updateMavenProjectVersionForm, final String parentBeforeVersion) {
            return false;
        }

        @Override
        public boolean isUpdateProjectDependency(final Project project, final UpdateMavenProjectVersionForm updateMavenProjectVersionForm, final String beforeVersion) {
            if (beforeVersion == null) {
                return false;
            }

            if (!updateMavenProjectVersionForm.isMustSameVersion() || VersionUtils.isEquals(updateMavenProjectVersionForm.getOldVersion(), beforeVersion)) {
                return true;
            }

            return false;
        }

        @Override
        public boolean isUpdateProjectDependencyManagementDependency(final Project project, final UpdateMavenProjectVersionForm updateMavenProjectVersionForm, final String beforeVersion) {
            return isUpdateProjectDependency(project, updateMavenProjectVersionForm, beforeVersion);
        }

    }

}


