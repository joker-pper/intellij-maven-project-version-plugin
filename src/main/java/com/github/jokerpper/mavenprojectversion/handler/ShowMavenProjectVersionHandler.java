package com.github.jokerpper.mavenprojectversion.handler;

import com.github.jokerpper.mavenprojectversion.constants.SystemConstants;
import com.github.jokerpper.mavenprojectversion.state.ShowMavenProjectVersionState;
import com.github.jokerpper.mavenprojectversion.util.IntellijUtils;
import com.github.jokerpper.mavenprojectversion.util.StringUtils;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.SimpleTextAttributes;
import org.jetbrains.idea.maven.navigator.MavenProjectsNavigator;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

public class ShowMavenProjectVersionHandler {

    public static final ShowMavenProjectVersionHandler INSTANCE = new ShowMavenProjectVersionHandler();

    private ShowMavenProjectVersionHandler() {
    }

    /**
     * 处理项目视图展示
     *
     * @param node
     * @param data
     */
    public void resolveMavenProjectView(ProjectViewNode node, PresentationData data) {
        Project project = node.getProject();
        MavenProjectsManager mavenProjectsManager = IntellijUtils.getMavenProjectsManager(project);
        if (!IntellijUtils.isMavenizedProject(mavenProjectsManager)) {
            return;
        }

        ShowMavenProjectVersionState showMavenProjectVersionState = ShowMavenProjectVersionState.getInstance(project);

        if (!showMavenProjectVersionState.isShowProjectView()) {
            return;
        }

        VirtualFile virtualFile = node.getVirtualFile();
        VirtualFile virtualPomFile = virtualFile != null && virtualFile.isDirectory() ? virtualFile.findChild("pom.xml") : null;
        if (virtualPomFile == null) {
            return;
        }
        MavenProject mavenProject = mavenProjectsManager.findProject(virtualPomFile);
        if (mavenProject == null) {
            return;
        }

        String versionRule;
        try {
            String projectViewVersionRule = showMavenProjectVersionState.getProjectViewVersionRule();
            if (!isAllowViewVersionRule(projectViewVersionRule)) {
                projectViewVersionRule = SystemConstants.DEFAULT_PROJECT_VIEW_VERSION_RULE;
            }
            versionRule = projectViewVersionRule.replace(SystemConstants.DEFAULT_VERSION_RULE, "%s");
        } catch (Exception e) {
            versionRule = "%s";
        }
        String version = StringUtils.trimToNull(mavenProject.getMavenId().getVersion());
        addColoredText(data, String.format("%s " + versionRule, "\t", version));
    }

    /**
     * 处理Maven项目结构视图展示
     *
     * @param project
     * @param isShow
     */
    public void resolveMavenStructureView(Project project, boolean isShow) {
        MavenProjectsNavigator mavenProjectsNavigator = MavenProjectsNavigator.getInstance(project);
        if (mavenProjectsNavigator == null) {
            return;
        }

        if (isShow) {
            if (!mavenProjectsNavigator.getShowVersions()) {
                mavenProjectsNavigator.setShowVersions(true);
            }
        } else {
            if (mavenProjectsNavigator.getShowVersions()) {
                mavenProjectsNavigator.setShowVersions(false);
            }
        }

    }

    /**
     * 刷新Maven项目视图
     *
     * @param project
     */
    public void refreshMavenProjectView(Project project) {
        ProjectView projectView = ProjectView.getInstance(project);
        if (projectView == null) {
            return;
        }
        projectView.refresh();
    }


    /**
     * 刷新Maven项目结构视图
     *
     * @param project
     */
    public void refreshMavenStructureView(Project project) {
        resolveMavenStructureView(project, ShowMavenProjectVersionState.getInstance(project).isShowStructureView());
    }

    /**
     * 同步Maven项目的结构视图的显示状态
     *
     * @param project
     */
    public void syncIsShowStructureView(Project project) {
        MavenProjectsNavigator mavenProjectsNavigator = MavenProjectsNavigator.getInstance(project);
        if (mavenProjectsNavigator != null) {
            ShowMavenProjectVersionState showMavenProjectVersionState = ShowMavenProjectVersionState.getInstance(project);
            showMavenProjectVersionState.setShowStructureView(mavenProjectsNavigator.getShowVersions());
        }
    }

    /**
     * 是否为允许的项目视图版本规则
     *
     * @param viewVersionRule
     * @return
     */
    public boolean isAllowViewVersionRule(String viewVersionRule) {
        if (StringUtils.isEmpty(viewVersionRule) || !viewVersionRule.contains(SystemConstants.DEFAULT_VERSION_RULE)) {
            return false;
        }
        return true;
    }

    private void addColoredText(PresentationData data, String text) {
        addColoredText(data, text, SimpleTextAttributes.GRAYED_ATTRIBUTES);
    }

    private void addColoredText(PresentationData data, String text, SimpleTextAttributes simpleTextAttributes) {
        if (data.getColoredText().isEmpty()) {
            data.addText(data.getPresentableText(), SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES);
        }
        data.addText(text, simpleTextAttributes);
    }

}
