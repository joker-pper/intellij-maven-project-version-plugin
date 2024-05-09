package com.github.jokerpper.mavenprojectversion.action;

import com.github.jokerpper.mavenprojectversion.handler.ActionPerformedStartHandler;
import com.github.jokerpper.mavenprojectversion.support.MavenProjectVersionAction;
import com.github.jokerpper.mavenprojectversion.ui.ShowMavenProjectVersionDialog;
import com.github.jokerpper.mavenprojectversion.util.IntellijUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;


public class ShowMavenProjectVersionAction extends MavenProjectVersionAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        //此时能被触发一定会存在project
        Project project = IntellijUtils.getProject(event);
        if (project == null) {
            //兜底报错..
            throw new RuntimeException("Not Get Project, Has Error!");
        }

        ActionPerformedStartHandler.INSTANCE.start(project);

        ShowMavenProjectVersionDialog showMavenProjectVersionDialog = new ShowMavenProjectVersionDialog(project, true);
        showMavenProjectVersionDialog.setResizable(false);
        showMavenProjectVersionDialog.show();
    }

}
