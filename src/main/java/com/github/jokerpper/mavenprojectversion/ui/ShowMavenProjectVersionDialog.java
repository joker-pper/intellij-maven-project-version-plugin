package com.github.jokerpper.mavenprojectversion.ui;

import com.github.jokerpper.mavenprojectversion.constants.SystemConstants;
import com.github.jokerpper.mavenprojectversion.handler.ShowMavenProjectVersionHandler;
import com.github.jokerpper.mavenprojectversion.state.ShowMavenProjectVersionState;
import com.github.jokerpper.mavenprojectversion.support.LanguageUtils;
import com.github.jokerpper.mavenprojectversion.support.MessagesUtils;
import com.github.jokerpper.mavenprojectversion.util.StringUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ShowMavenProjectVersionDialog extends DialogWrapper {

    private final Project project;

    private final ShowMavenProjectVersionForm showMavenProjectVersionForm;

    public ShowMavenProjectVersionDialog(@NotNull Project project, boolean canBeParent) {
        super(project, canBeParent);

        this.project = project;

        this.showMavenProjectVersionForm = new ShowMavenProjectVersionForm(project);

        String title = LanguageUtils.get(LanguageUtils.Constants.SHOW_MAVEN_PROJECT_VERSION_TITLE);
        setTitle(title);
        setOKButtonText(LanguageUtils.get(LanguageUtils.Constants.OK_BUTTON_TEXT));
        setCancelButtonText(LanguageUtils.get(LanguageUtils.Constants.CANCEL_BUTTON_TEXT));
        init();
    }

    @Override
    @Nullable
    protected JComponent createCenterPanel() {
        return showMavenProjectVersionForm.getJPanelContent();
    }

    @Override
    protected void doOKAction() {
        try {
            String projectViewVersionRule = StringUtils.trim(showMavenProjectVersionForm.getProjectViewVersionRuleTextField().getText());
            if (!ShowMavenProjectVersionHandler.INSTANCE.isAllowViewVersionRule(projectViewVersionRule)) {
                String message = LanguageUtils.parseTemplateValueByKey(LanguageUtils.Constants.SHOW_FORM_TIP_PROJECT_VIEW_VERSION_RULE_NOT_SUPPORT_TEMPLATE_TEXT, SystemConstants.DEFAULT_VERSION_RULE, SystemConstants.DEFAULT_PROJECT_VIEW_VERSION_RULE);
                MessagesUtils.showWarningDialog(project, message, LanguageUtils.get(LanguageUtils.Constants.MESSAGES_WARNING_TITLE));
                return;
            }

            ShowMavenProjectVersionState showMavenProjectVersionState = ShowMavenProjectVersionState.getInstance(project);

            showMavenProjectVersionState.setShowProjectView(showMavenProjectVersionForm.getFirstCheckBox().isSelected());
            showMavenProjectVersionState.setShowStructureView(showMavenProjectVersionForm.getSecondCheckBox().isSelected());
            showMavenProjectVersionState.setProjectViewVersionRule(projectViewVersionRule);

            ShowMavenProjectVersionHandler.INSTANCE.refreshMavenProjectView(project);
            ShowMavenProjectVersionHandler.INSTANCE.refreshMavenStructureView(project);

            MessagesUtils.showInfoDialog(project, LanguageUtils.get(LanguageUtils.Constants.SHOW_INFO_SUCCESS_TEXT), LanguageUtils.get(LanguageUtils.Constants.MESSAGES_SUCCESS_TITLE));
            this.close(DialogWrapper.OK_EXIT_CODE);
        } catch (Throwable throwable) {
            MessagesUtils.showErrorDetailInfoDialog(project, throwable);
        }
    }


}
