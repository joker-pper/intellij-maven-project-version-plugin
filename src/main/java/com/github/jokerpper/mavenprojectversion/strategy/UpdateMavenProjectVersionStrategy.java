package com.github.jokerpper.mavenprojectversion.strategy;

import com.github.jokerpper.mavenprojectversion.ui.UpdateMavenProjectVersionForm;
import com.intellij.openapi.project.Project;

public interface UpdateMavenProjectVersionStrategy {

    /**
     * 检查版本是否通过
     *
     * @param project
     * @param updateMavenProjectVersionForm
     * @return
     */
    boolean checkVersionPass(final Project project, final UpdateMavenProjectVersionForm updateMavenProjectVersionForm);

    /**
     * 是否更新project的版本
     *
     * @param project
     * @param updateMavenProjectVersionForm
     * @param beforeVersion
     * @return
     */
    boolean isUpdateProjectVersion(final Project project, final UpdateMavenProjectVersionForm updateMavenProjectVersionForm, final String beforeVersion);

    /**
     * 是否更新project parent的版本
     *
     * @param project
     * @param updateMavenProjectVersionForm
     * @param parentBeforeVersion
     * @return
     */
    boolean isUpdateProjectParentVersion(final Project project, final UpdateMavenProjectVersionForm updateMavenProjectVersionForm, final String parentBeforeVersion);

    /**
     * 是否更新project dependency的版本
     *
     * @param project
     * @param updateMavenProjectVersionForm
     * @param beforeVersion
     * @return
     */
    boolean isUpdateProjectDependencyVersion(final Project project, final UpdateMavenProjectVersionForm updateMavenProjectVersionForm, final String beforeVersion);

    /**
     * 是否更新project dependencyManagement dependency的版本
     *
     * @param project
     * @param updateMavenProjectVersionForm
     * @param beforeVersion
     * @return
     */
    boolean isUpdateProjectDependencyManagementDependencyVersion(final Project project, final UpdateMavenProjectVersionForm updateMavenProjectVersionForm, final String beforeVersion);
}
