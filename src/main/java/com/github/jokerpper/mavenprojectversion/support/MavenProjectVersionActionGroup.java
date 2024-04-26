package com.github.jokerpper.mavenprojectversion.support;

import com.github.jokerpper.mavenprojectversion.util.IntellijUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

public class MavenProjectVersionActionGroup extends DefaultActionGroup implements DumbAware {

    @Override
    public void update(@NotNull AnActionEvent e) {
        Presentation p = e.getPresentation();
        p.setEnabled(isAvailable(e));
        p.setVisible(isVisible(e));
    }

    protected boolean isAvailable(@NotNull AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        MavenProjectsManager mavenProjectsManager = IntellijUtils.getMavenProjectsManager(project);
        if (mavenProjectsManager == null || mavenProjectsManager.getProjects().isEmpty()) {
            return false;
        }
        return true;
    }

    protected boolean isVisible(@NotNull AnActionEvent e) {
        if (!IntellijUtils.isMavenizedProject(e)) {
            return false;
        }
        return true;
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

}
