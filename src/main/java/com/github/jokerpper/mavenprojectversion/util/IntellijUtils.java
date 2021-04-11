package com.github.jokerpper.mavenprojectversion.util;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;

public class IntellijUtils {

    public static MavenProjectsManager getMavenProjectsManager(AnActionEvent event) {
        return MavenActionUtil.getProjectsManager(event.getDataContext());
    }

    public static MavenProjectsManager getMavenProjectsManager(Project project) {
        return MavenProjectsManager.getInstance(project);
    }

    public static boolean isMavenizedProject(AnActionEvent event) {
        return isMavenizedProject(getMavenProjectsManager(event));
    }

    public static boolean isMavenizedProject(final MavenProjectsManager projectsManager) {
        return projectsManager != null && projectsManager.isMavenizedProject();
    }

    public static boolean hasAction(ActionManager actionManager, String actionId) {
        AnAction anAction = actionManager.getAction(actionId);
        return anAction != null;
    }
}
