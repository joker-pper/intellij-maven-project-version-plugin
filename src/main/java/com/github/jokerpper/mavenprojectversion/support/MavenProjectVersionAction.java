package com.github.jokerpper.mavenprojectversion.support;

import com.github.jokerpper.mavenprojectversion.handler.ShowMavenProjectVersionHandler;
import com.github.jokerpper.mavenprojectversion.util.IntellijUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

public abstract class MavenProjectVersionAction extends MavenAction {

    @Override
    protected boolean isAvailable(@NotNull AnActionEvent e) {
        if (!super.isAvailable(e)) {
            return false;
        }
        Project project = e.getData(PlatformDataKeys.PROJECT);
        MavenProjectsManager mavenProjectsManager = IntellijUtils.getMavenProjectsManager(project);
        if (mavenProjectsManager == null || mavenProjectsManager.getProjects().isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    protected boolean isVisible(@NotNull AnActionEvent e) {
        if (!super.isVisible(e)) {
            return false;
        }

        if (!IntellijUtils.isMavenizedProject(e)) {
            return false;
        }

        Project project = e.getProject();
        ShowMavenProjectVersionHandler.INSTANCE.syncIsShowStructureView(project);

        return true;
    }
}
