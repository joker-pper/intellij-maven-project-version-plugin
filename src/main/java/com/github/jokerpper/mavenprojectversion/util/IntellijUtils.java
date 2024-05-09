package com.github.jokerpper.mavenprojectversion.util;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;

public class IntellijUtils {

    /**
     * 通过AnActionEvent事件获取当前的Project对象
     *
     * @param event 一个不可为null的AnActionEvent对象
     * @return 返回Project对象
     */
    @Nullable
    public static Project getProject(@NotNull AnActionEvent event) {
        return event.getData(PlatformDataKeys.PROJECT);
    }

    /**
     * 获取AnActionEvent事件关联的Maven项目管理器
     *
     * @param event 一个不可为null的AnActionEvent对象
     * @return 返回一个MavenProjectsManager实例，用于管理Maven项目
     */
    @Nullable
    public static MavenProjectsManager getMavenProjectsManager(@NotNull AnActionEvent event) {
        return MavenActionUtil.getProjectsManager(event.getDataContext());
    }

    /**
     * 获取指定项目的Maven项目管理器
     *
     * @param project IntelliJ IDEA中的项目对象
     * @return 返回该项目的Maven项目管理器实例。如果输入的项目为null，则返回null
     */
    @Nullable
    public static MavenProjectsManager getMavenProjectsManager(@Nullable Project project) {
        if (project == null) {
            return null;
        }
        return MavenProjectsManager.getInstance(project);
    }

    /**
     * 通过AnActionEvent事件获取是否是一个Maven项目
     *
     * @param event
     * @return 返回是否是一个Maven项目
     */
    public static boolean isMavenizedProject(@NotNull AnActionEvent event) {
        return isMavenizedProject(getMavenProjectsManager(event));
    }

    /**
     * 检查当前项目是否是一个Maven项目
     *
     * @param projectsManager Maven项目管理器对象
     * @return 如果projectsManager不为空且该项目是Maven项目，则返回true；否则返回false
     */
    public static boolean isMavenizedProject(@Nullable MavenProjectsManager projectsManager) {
        return projectsManager != null && projectsManager.isMavenizedProject();
    }

    /**
     * 检查ActionManager中是否存在指定的Action
     *
     * @param actionManager ActionManager对象
     * @param actionId      想要检查的动作的ID
     * @return 返回true如果指定的Action存在，否则返回false
     */
    public static boolean hasAction(@NotNull ActionManager actionManager, @NotNull String actionId) {
        AnAction anAction = actionManager.getAction(actionId);
        return anAction != null;
    }
}
