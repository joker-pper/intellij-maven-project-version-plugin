package com.github.jokerpper.mavenprojectversion.startup;

import com.github.jokerpper.mavenprojectversion.handler.ShowMavenProjectVersionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.navigator.MavenProjectsNavigator;

public class MavenProjectVersionStartupActivity implements StartupActivity {

    @Override
    public void runActivity(@NotNull Project project) {

        MessageBusConnection connection = project.getMessageBus().connect();

        connection.subscribe(ProjectManager.TOPIC, new ProjectManagerListener() {
            @Override
            public void projectClosingBeforeSave(@NotNull Project project) {
                ShowMavenProjectVersionHandler.INSTANCE.syncIsShowStructureView(project);
            }
        });

        connection.subscribe(ToolWindowManagerListener.TOPIC, new ToolWindowManagerListener() {
            @Override
            public void stateChanged(@NotNull ToolWindowManager toolWindowManager) {
                ToolWindow toolWindow = toolWindowManager.getToolWindow(MavenProjectsNavigator.TOOL_WINDOW_ID);
                if (toolWindow != null) {
                    ShowMavenProjectVersionHandler.INSTANCE.syncIsShowStructureView(project);
                }
            }
        });

    }
}
