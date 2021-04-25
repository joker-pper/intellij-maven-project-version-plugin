package com.github.jokerpper.mavenprojectversion.startup;

import com.github.jokerpper.mavenprojectversion.handler.ShowMavenProjectVersionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

public class MavenProjectVersionStartupActivity implements StartupActivity {

    @Override
    public void runActivity(@NotNull Project project) {
        ShowMavenProjectVersionHandler.INSTANCE.syncIsShowStructureView(project);

        MessageBusConnection connection = project.getMessageBus().connect();
        connection.subscribe(ProjectManager.TOPIC, new ProjectManagerListener() {
            @Override
            public void projectClosingBeforeSave(@NotNull Project project) {
                ShowMavenProjectVersionHandler.INSTANCE.syncIsShowStructureView(project);
            }
        });

    }
}
