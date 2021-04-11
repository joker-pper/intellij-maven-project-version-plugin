package com.github.jokerpper.mavenprojectversion.strategy;

import com.github.jokerpper.mavenprojectversion.ui.UpdateMavenProjectVersionForm;
import com.intellij.openapi.project.Project;

public interface UpdateMavenProjectVersionStrategy {

    boolean checkVersion(final Project project, final UpdateMavenProjectVersionForm updateMavenProjectVersionForm);

    boolean isUpdateProjectVersion(final Project project, final UpdateMavenProjectVersionForm updateMavenProjectVersionForm, final String beforeVersion);

    boolean isUpdateProjectParentVersion(final Project project, final UpdateMavenProjectVersionForm updateMavenProjectVersionForm, final String parentBeforeVersion);

    boolean isUpdateProjectDependency(final Project project, final UpdateMavenProjectVersionForm updateMavenProjectVersionForm, final String beforeVersion);

    boolean isUpdateProjectDependencyManagementDependency(final Project project, final UpdateMavenProjectVersionForm updateMavenProjectVersionForm, final String beforeVersion);
}
