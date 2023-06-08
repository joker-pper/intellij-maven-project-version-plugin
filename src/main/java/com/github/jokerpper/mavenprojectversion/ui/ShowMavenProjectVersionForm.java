package com.github.jokerpper.mavenprojectversion.ui;

import com.github.jokerpper.mavenprojectversion.state.ShowMavenProjectVersionState;
import com.github.jokerpper.mavenprojectversion.support.LanguageUtils;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;

public class ShowMavenProjectVersionForm implements Disposable {

    private final JPanel dialogPanel;

    private final JCheckBox firstCheckBox;

    private final JCheckBox secondCheckBox;

    private final JTextField projectViewVersionRuleTextField;

    public ShowMavenProjectVersionForm(Project project) {
        this.dialogPanel = new JPanel();
        String showProjectViewText = LanguageUtils.get(LanguageUtils.Constants.SHOW_FORM_SHOW_PROJECT_VIEW_TEXT);
        String showStructureViewText = LanguageUtils.get(LanguageUtils.Constants.SHOW_FORM_SHOW_STRUCTURE_VIEW_TEXT);

        ShowMavenProjectVersionState showMavenProjectVersionState = ShowMavenProjectVersionState.getInstance(project);
        this.firstCheckBox = new JCheckBox(showProjectViewText, showMavenProjectVersionState.isShowProjectView());
        this.secondCheckBox = new JCheckBox(showStructureViewText, showMavenProjectVersionState.isShowStructureView());
        this.projectViewVersionRuleTextField = new JTextField(showMavenProjectVersionState.getProjectViewVersionRule());
    }

    public JPanel getJPanelContent() {

        dialogPanel.setLayout(new GridLayoutManager(3, 2, JBUI.insets(0), -1, -1));

        String projectViewVersionRuleText = LanguageUtils.get(LanguageUtils.Constants.SHOW_FORM_PROJECT_VIEW_VERSION_RULE_TEXT);
        JLabel projectViewVersionRuleLabel = new JLabel(projectViewVersionRuleText);
        dialogPanel.add(projectViewVersionRuleLabel, new GridConstraints(0, 0, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null));

        dialogPanel.add(projectViewVersionRuleTextField, new GridConstraints(0, 1, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                new Dimension(180, 30), null, null));

        dialogPanel.add(firstCheckBox, new GridConstraints(1, 0, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null));

        dialogPanel.add(secondCheckBox, new GridConstraints(2, 0, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null));

        dialogPanel.setMinimumSize(new Dimension(300, 120));

        return dialogPanel;
    }

    public JCheckBox getFirstCheckBox() {
        return firstCheckBox;
    }

    public JCheckBox getSecondCheckBox() {
        return secondCheckBox;
    }

    public JTextField getProjectViewVersionRuleTextField() {
        return projectViewVersionRuleTextField;
    }

    @Override
    public void dispose() {
        dialogPanel.removeAll();
    }

}
