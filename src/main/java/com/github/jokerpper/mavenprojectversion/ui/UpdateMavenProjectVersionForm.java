package com.github.jokerpper.mavenprojectversion.ui;

import com.github.jokerpper.mavenprojectversion.strategy.impl.UpdateMavenProjectVersionStrategyEnum;
import com.github.jokerpper.mavenprojectversion.support.LanguageUtils;
import com.github.jokerpper.mavenprojectversion.support.UserConfUtils;
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
import java.util.List;
import java.util.*;

public class UpdateMavenProjectVersionForm implements Disposable {

    private static final String MIN_WIDTH_KEY = "update_version_form.min_width";

    private static final String MAX_WIDTH_KEY = "update_version_form.max_width";

    private static final int DEFAULT_MIN_WIDTH = 300;

    private static final int DEFAULT_MAX_WIDTH = 420;

    private static final int DIALOG_PANEL_HEIGTH = 120;

    /**
     * 最小宽度
     */
    private final int MIN_WIDTH;

    /**
     * 最大宽度，-1时无限制
     */
    private final int MAX_WIDTH;

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
        String mustSameVersionText = LanguageUtils.get(LanguageUtils.Constants.UPDATE_FORM_MUST_SAME_VERSION_TEXT);
        this.mustSameVersionCheckBox = new JCheckBox(mustSameVersionText);

        MIN_WIDTH = UserConfUtils.getProperty(int.class, MIN_WIDTH_KEY, DEFAULT_MIN_WIDTH);
        MAX_WIDTH = UserConfUtils.getProperty(int.class, MAX_WIDTH_KEY, DEFAULT_MAX_WIDTH);

        init();
    }

    private void init() {
        projectComboBox.addActionListener(e -> {
            Object item = projectComboBox.getSelectedItem();
            if (item != null) {
                initRootProjectAndOldVersion((MavenProject) item);
            }
        });

        //获取重复出现的root project (以groupId + artifactId为标识判断)
        Set<String> mavenShowWithoutVersionContents = new HashSet<>(rootProjects.size());
        Set<String> repeatMavenShowRootProjectContents = new HashSet<>(16);
        for (MavenProject mavenProject : rootProjects) {
            String content = formatDefaultContent(mavenProject, false);
            if (mavenShowWithoutVersionContents.contains(content)) {
                repeatMavenShowRootProjectContents.add(content);
            } else {
                mavenShowWithoutVersionContents.add(content);
            }
        }

        projectComboBox.setRenderer(new CustomListCellRenderer(repeatMavenShowRootProjectContents));

        for (MavenProject mavenProject : rootProjects) {
            projectComboBox.addItem(mavenProject);
        }
    }

    private void initRootProjectAndOldVersion(MavenProject project) {
        this.rootProject = project;
        this.oldVersion = StringUtils.trimToNull(project.getMavenId().getVersion());
        this.newVersionTextField.setText(this.oldVersion);
    }

    private Dimension initMyDimension() {

        Dimension projectComboBoxSize = projectComboBox.getSize();
        Dimension projectComboBoxMinimumSize = projectComboBox.getMinimumSize();

        int projectComboBoxWidth = (int) projectComboBoxSize.getWidth();
        int projectComboBoxHeight = (int) projectComboBoxSize.getHeight();
        if (projectComboBoxWidth < projectComboBoxMinimumSize.getWidth()) {
            projectComboBoxWidth = (int) projectComboBoxMinimumSize.getWidth();
        }

        if (projectComboBoxHeight < projectComboBoxMinimumSize.getHeight()) {
            projectComboBoxHeight = (int) projectComboBoxMinimumSize.getHeight();
        }

        Dimension resultSize;
        if (projectComboBoxWidth <= MIN_WIDTH) {
            //最小宽度
            resultSize = new Dimension(MIN_WIDTH, projectComboBoxHeight);
        } else if (projectComboBoxWidth <= MAX_WIDTH || MAX_WIDTH <= 0) {
            //自身宽度
            resultSize = new Dimension(projectComboBoxWidth, projectComboBoxHeight);
        } else {
            //最大宽度
            resultSize = new Dimension(MAX_WIDTH, projectComboBoxHeight);
        }
        return resultSize;
    }

    public JPanel getJPanelContent() {

        dialogPanel.setLayout(new GridLayoutManager(5, 2, JBUI.insets(0), -1, -1));

        String strategyText = LanguageUtils.get(LanguageUtils.Constants.UPDATE_FORM_STRATEGY_TEXT);

        JLabel strategyLabel = new JLabel(strategyText);
        dialogPanel.add(strategyLabel, new GridConstraints(0, 0, 1, 1,
                GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null));

        JPanel strategyRadioDialogPanel = new JPanel();
        strategyRadioDialogPanel.setLayout(new GridLayoutManager(1, 2, JBUI.insets(0), -1, -1));

        String strategyDefaultText = LanguageUtils.get(LanguageUtils.Constants.UPDATE_FORM_STRATEGY_DEFAULT_TEXT);
        String strategyGeneralText = LanguageUtils.get(LanguageUtils.Constants.UPDATE_FORM_STRATEGY_GENERAL_TEXT);

        JRadioButton strategyRadioButton1 = new JRadioButton(strategyDefaultText, true);
        JRadioButton strategyRadioButton2 = new JRadioButton(strategyGeneralText);

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
        String mavenProjectText = LanguageUtils.get(LanguageUtils.Constants.UPDATE_FORM_MAVEN_PROJECT_TEXT);

        JLabel projectLabel = new JLabel(mavenProjectText);
        dialogPanel.add(projectLabel, new GridConstraints(1, 0, 1, 1,
                GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null));

        Dimension myDimension = initMyDimension();

        dialogPanel.add(projectComboBox, new GridConstraints(1, 1, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                myDimension, myDimension, null));

        String newVersionText = LanguageUtils.get(LanguageUtils.Constants.UPDATE_FORM_NEW_VERSION_TEXT);
        JLabel newVersionLabel = new JLabel(newVersionText);
        dialogPanel.add(newVersionLabel, new GridConstraints(2, 0, 1, 1,
                GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null));

        dialogPanel.add(newVersionTextField, new GridConstraints(2, 1, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                myDimension, myDimension, null));


        dialogPanel.add(mustSameVersionCheckBox, new GridConstraints(3, 1, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null));


        dialogPanel.setMinimumSize(new Dimension(MIN_WIDTH, DIALOG_PANEL_HEIGTH));

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

    class CustomListCellRenderer extends DefaultListCellRenderer {

        private Set<String> repeatMavenShowRootProjectContents;

        public CustomListCellRenderer(Set<String> repeatMavenShowRootProjectContents) {
            this.repeatMavenShowRootProjectContents = repeatMavenShowRootProjectContents;
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            MavenProject mavenProject = (MavenProject) value;
            String withoutVersionContent = formatDefaultContent(mavenProject, false);
            String content;
            if (repeatMavenShowRootProjectContents.contains(withoutVersionContent)) {
                content = formatRepeatContent(mavenProject);
            } else {
                content = formatDefaultContent(mavenProject, true);
            }
            return super.getListCellRendererComponent(list, content, index, isSelected, cellHasFocus);
        }
    }

    private String formatDefaultContent(MavenProject mavenProject, boolean withVersion) {
        MavenId mavenId = mavenProject.getMavenId();
        if (withVersion) {
            return String.format("%s:%s(%s)", StringUtils.trim(mavenId.getGroupId()), StringUtils.trim(mavenId.getArtifactId()), StringUtils.trim(mavenId.getVersion()));
        }
        return String.format("%s:%s", StringUtils.trim(mavenId.getGroupId()), StringUtils.trim(mavenId.getArtifactId()));
    }

    private String formatRepeatContent(MavenProject mavenProject) {
        MavenId mavenId = mavenProject.getMavenId();
        return String.format("[%s]%s:%s(%s)", mavenProject.getFile().getParent().getName(), StringUtils.trim(mavenId.getGroupId()), StringUtils.trim(mavenId.getArtifactId()), StringUtils.trim(mavenId.getVersion()));
    }
}
