package com.github.jokerpper.mavenprojectversion.action;

import com.github.jokerpper.mavenprojectversion.handler.ActionPerformedStartHandler;
import com.github.jokerpper.mavenprojectversion.support.MavenProjectVersionAction;
import com.github.jokerpper.mavenprojectversion.ui.UpdateMavenProjectVersionDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;

public class UpdateMavenProjectVersionAction extends MavenProjectVersionAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        ActionPerformedStartHandler.INSTANCE.start(project);

        UpdateMavenProjectVersionDialog updateMavenProjectVersionDialog = new UpdateMavenProjectVersionDialog(project, true);
        updateMavenProjectVersionDialog.setResizable(false);
        updateMavenProjectVersionDialog.show();

    }

}
