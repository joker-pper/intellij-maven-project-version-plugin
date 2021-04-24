package com.github.jokerpper.mavenprojectversion.action;

import com.github.jokerpper.mavenprojectversion.support.MavenProjectVersionAction;
import com.github.jokerpper.mavenprojectversion.ui.ShowMavenProjectVersionDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;


public class ShowMavenProjectVersionAction extends MavenProjectVersionAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        ShowMavenProjectVersionDialog showMavenProjectVersionDialog = new ShowMavenProjectVersionDialog(project, true);
        showMavenProjectVersionDialog.setResizable(false);
        showMavenProjectVersionDialog.show();
    }

}
