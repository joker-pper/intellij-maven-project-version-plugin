package com.github.jokerpper.mavenprojectversion.ui;

import com.github.jokerpper.mavenprojectversion.strategy.impl.UpdateMavenProjectVersionStrategyEnum;
import com.github.jokerpper.mavenprojectversion.util.StringUtils;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.ui.JBUI;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.project.MavenProject;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

public class UpdateMavenProjectVersionForm implements Disposable {

    private final List<MavenProject> rootProjects;
    private final JPanel dialogPanel;
    private final ButtonGroup strategyRadioBtnGroup;
    private final ComboBox<MavenProject> projectComboBox;
    private final JTextField newVersionTextField;
    private final JCheckBox mustSameVersionCheckBox;

    private MavenProject rootProject;

    private String oldVersion;

    public UpdateMavenProjectVersionForm(List<MavenProject> rootProjects) {
        this.rootProjects = rootProjects;

        this.dialogPanel = new JPanel();

        this.strategyRadioBtnGroup = new ButtonGroup();

        this.projectComboBox = new ComboBox<>();
        this.newVersionTextField = new JTextField();
        this.mustSameVersionCheckBox = new JCheckBox(Constants.MUST_SAME_VERSION_TEXT);

        init();
    }

    private void init() {
        projectComboBox.addActionListener(e -> {
            Object item = projectComboBox.getSelectedItem();
            if (item != null) {
                initRootProjectAndOldVersion((MavenProject) item);
            }
        });
        projectComboBox.setRenderer(new CustomListCellRenderer());

        for (MavenProject mavenProject : rootProjects) {
            projectComboBox.addItem(mavenProject);
        }

        initStyle();
    }

    private void initRootProjectAndOldVersion(MavenProject project) {
        this.rootProject = project;
        this.oldVersion = StringUtils.trimToNull(project.getMavenId().getVersion());
        this.newVersionTextField.setText(this.oldVersion);
    }

    private void initStyle() {
        Dimension projectComboBoxDimension = projectComboBox.getSize();
        if (projectComboBoxDimension.getWidth() > 0) {
            Dimension newVersionTextFieldSize = newVersionTextField.getSize();
            if (newVersionTextFieldSize.getWidth() < projectComboBoxDimension.getWidth()) {
                newVersionTextField.setSize(projectComboBoxDimension);
            }
        } else {
            newVersionTextField.setPreferredSize(projectComboBox.getMinimumSize());
        }
    }

    public JPanel getJPanelContent() {

        dialogPanel.setLayout(new GridLayoutManager(5, 2, JBUI.insets(0), -1, -1));

        JLabel strategyLabel = new JLabel(Constants.STRATEGY_TEXT);
        dialogPanel.add(strategyLabel, new GridConstraints(0, 0, 1, 1,
                GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null));

        JPanel strategyRadioDialogPanel = new JPanel();
        strategyRadioDialogPanel.setLayout(new GridLayoutManager(1, 2, JBUI.insets(0), -1, -1));

        JRadioButton strategyRadioButton1 = new JRadioButton(Constants.STRATEGY_DEFAULT_TEXT, true);
        JRadioButton strategyRadioButton2 = new JRadioButton(Constants.STRATEGY_GENERAL_TEXT);

        strategyRadioBtnGroup.add(strategyRadioButton1);
        strategyRadioBtnGroup.add(strategyRadioButton2);


        strategyRadioDialogPanel.add(strategyRadioButton1, new GridConstraints(0, 0, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null));

        strategyRadioDialogPanel.add(strategyRadioButton2, new GridConstraints(0, 1, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null));

        dialogPanel.add(strategyRadioDialogPanel, new GridConstraints(0, 1, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null));

        JLabel projectLabel = new JLabel(Constants.MAVEN_PROJECT_TEXT);
        dialogPanel.add(projectLabel, new GridConstraints(1, 0, 1, 1,
                GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null));

        dialogPanel.add(projectComboBox, new GridConstraints(1, 1, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                new Dimension(300, 30), null, null));

        JLabel newVersionLabel = new JLabel(Constants.NEW_VERSION_TEXT);
        dialogPanel.add(newVersionLabel, new GridConstraints(2, 0, 1, 1,
                GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null));

        dialogPanel.add(newVersionTextField, new GridConstraints(2, 1, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                new Dimension(300, 30), null, null));


        dialogPanel.add(mustSameVersionCheckBox, new GridConstraints(3, 1, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null));


        dialogPanel.setMinimumSize(new Dimension(300, 120));

        return dialogPanel;
    }

    public MavenProject getRootProject() {
        return rootProject;
    }

    public String getOldVersion() {
        return oldVersion;
    }

    public boolean isMustSameVersion() {
        return mustSameVersionCheckBox.isSelected();
    }

    public String getNewVersion() {
        return StringUtils.trimToNull(newVersionTextField.getText());
    }

    public UpdateMavenProjectVersionStrategyEnum getUpdateMavenProjectVersionStrategy() {
        List<UpdateMavenProjectVersionStrategyEnum> strategyEnumList = Arrays.asList(UpdateMavenProjectVersionStrategyEnum.DEFAULT, UpdateMavenProjectVersionStrategyEnum.GENERAL);
        int index = 0;
        Enumeration<AbstractButton> buttonEnumeration = strategyRadioBtnGroup.getElements();
        while (buttonEnumeration.hasMoreElements()) {
            JRadioButton jRadioButton = (JRadioButton) buttonEnumeration.nextElement();
            if (jRadioButton.isSelected()) {
                return strategyEnumList.get(index);
            }
            index++;
        }
        return null;
    }


    @Override
    public void dispose() {
        dialogPanel.removeAll();
    }

    class Constants {

        static final String STRATEGY_TEXT = "Strategy:";

        static final String STRATEGY_DEFAULT_TEXT = "Default";

        static final String STRATEGY_GENERAL_TEXT = "General";

        static final String MAVEN_PROJECT_TEXT = "Maven Project:";

        static final String NEW_VERSION_TEXT = "New Version:";

        static final String MUST_SAME_VERSION_TEXT = "Must Same Version";

    }

    class CustomListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            MavenProject mavenProject = (MavenProject) value;
            MavenId mavenId = mavenProject.getMavenId();
            String content = String.format("%s:%s(%s)", StringUtils.trim(mavenId.getGroupId()), StringUtils.trim(mavenId.getArtifactId()), StringUtils.trim(mavenId.getVersion()));
            return super.getListCellRendererComponent(list, content, index, isSelected, cellHasFocus);
        }
    }

}
