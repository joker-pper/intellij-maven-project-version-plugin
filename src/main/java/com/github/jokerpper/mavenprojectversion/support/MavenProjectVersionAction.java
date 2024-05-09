package com.github.jokerpper.mavenprojectversion.support;

import com.github.jokerpper.mavenprojectversion.util.IntellijUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

public abstract class MavenProjectVersionAction extends MavenAction {

    @Override
    protected boolean isAvailable(@NotNull AnActionEvent event) {
        if (!super.isAvailable(event)) {
            return false;
        }
        Project project = IntellijUtils.getProject(event);
        MavenProjectsManager mavenProjectsManager = IntellijUtils.getMavenProjectsManager(project);
        return mavenProjectsManager != null && !mavenProjectsManager.getProjects().isEmpty();
    }

    @Override
    protected boolean isVisible(@NotNull AnActionEvent event) {
        if (!super.isVisible(event)) {
            return false;
        }
        return IntellijUtils.isMavenizedProject(event);
    }
}
