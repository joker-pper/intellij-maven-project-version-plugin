package com.github.jokerpper.mavenprojectversion.support;

import com.github.jokerpper.mavenprojectversion.util.IntellijUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

public class MavenProjectVersionActionGroup extends DefaultActionGroup implements DumbAware {

    @Override
    public void update(@NotNull AnActionEvent event) {
        Presentation p = event.getPresentation();

        //设置是否显示ActionGroup
        p.setVisible(isVisible(event));

        //设置是否可用ActionGroup
        p.setEnabled(isAvailable(event));
    }


    /**
     * 是否支持该动作
     *
     * @param event
     * @return
     */
    protected boolean isAvailable(@NotNull AnActionEvent event) {
        Project project = IntellijUtils.getProject(event);
        MavenProjectsManager mavenProjectsManager = IntellijUtils.getMavenProjectsManager(project);
        return mavenProjectsManager != null && !mavenProjectsManager.getProjects().isEmpty();
    }

    /**
     * 是否显示ActionGroup
     *
     * @param event
     * @return
     */
    protected boolean isVisible(@NotNull AnActionEvent event) {
        return IntellijUtils.isMavenizedProject(event);
    }

}
