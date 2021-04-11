package com.github.jokerpper.mavenprojectversion.ui;

import com.github.jokerpper.mavenprojectversion.constants.Constants;
import com.github.jokerpper.mavenprojectversion.handler.ShowMavenProjectVersionHandler;
import com.github.jokerpper.mavenprojectversion.state.ShowMavenProjectVersionState;
import com.github.jokerpper.mavenprojectversion.util.StringUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ShowMavenProjectVersionDialog extends DialogWrapper {

    private static final String title = "Show Maven Project Version";

    private final Project project;

    private final ShowMavenProjectVersionForm showMavenProjectVersionForm;

    public ShowMavenProjectVersionDialog(@Nullable Project project, boolean canBeParent) {
        super(project, canBeParent);

        this.project = project;

        this.showMavenProjectVersionForm = new ShowMavenProjectVersionForm(project);

        setTitle(title);
        init();
    }

    @Override
    @Nullable
    protected JComponent createCenterPanel() {
        return showMavenProjectVersionForm.getJPanelContent();
    }

    @Override
    protected void doOKAction() {
        String projectViewVersionRule = StringUtils.trim(showMavenProjectVersionForm.getProjectViewVersionRuleTextField().getText());
        if (!ShowMavenProjectVersionHandler.INSTANCE.isAllowViewVersionRule(projectViewVersionRule)) {
            Messages.showWarningDialog(project, String.format("project view version rule is not support, default rule: %s", Constants.DEFAULT_PROJECT_VIEW_VERSION_RULE), "Warning");
            return;
        }

        ShowMavenProjectVersionState showMavenProjectVersionState = ShowMavenProjectVersionState.getInstance(project);

        showMavenProjectVersionState.setShowProjectView(showMavenProjectVersionForm.getFirstCheckBox().isSelected());
        showMavenProjectVersionState.setShowStructureView(showMavenProjectVersionForm.getSecondCheckBox().isSelected());
        showMavenProjectVersionState.setProjectViewVersionRule(projectViewVersionRule);

        ShowMavenProjectVersionHandler.INSTANCE.refreshMavenProjectView(project);
        ShowMavenProjectVersionHandler.INSTANCE.refreshMavenStructureView(project);

        this.close(DialogWrapper.OK_EXIT_CODE);
    }


}
