package com.github.jokerpper.mavenprojectversion.action;

import com.github.jokerpper.mavenprojectversion.constants.Constants;
import com.github.jokerpper.mavenprojectversion.handler.ShowMavenProjectVersionHandler;
import com.github.jokerpper.mavenprojectversion.ui.ShowMavenProjectVersionDialog;
import com.github.jokerpper.mavenprojectversion.util.IntellijUtils;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.utils.actions.MavenAction;


public class ShowMavenProjectVersionAction extends MavenAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        ShowMavenProjectVersionDialog showMavenProjectVersionDialog = new ShowMavenProjectVersionDialog(project, true);
        showMavenProjectVersionDialog.setResizable(false);
        showMavenProjectVersionDialog.show();
    }

    @Override
    protected boolean isAvailable(@NotNull AnActionEvent e) {
        if (!super.isAvailable(e)) {
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

        ActionManager actionManager = e.getActionManager();
        if (!IntellijUtils.hasAction(actionManager, Constants.MAVEN_SHOW_SETTINGS_ACTION_ID)) {
            return false;
        }

        return true;
    }
}
