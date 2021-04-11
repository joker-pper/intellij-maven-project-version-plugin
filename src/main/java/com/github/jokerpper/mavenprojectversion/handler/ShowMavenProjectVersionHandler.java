package com.github.jokerpper.mavenprojectversion.handler;

import com.github.jokerpper.mavenprojectversion.constants.Constants;
import com.github.jokerpper.mavenprojectversion.state.ShowMavenProjectVersionState;
import com.github.jokerpper.mavenprojectversion.util.IntellijUtils;
import com.github.jokerpper.mavenprojectversion.util.StringUtils;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.util.treeView.PresentableNodeDescriptor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.treeStructure.PatchedDefaultMutableTreeNode;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.util.ReflectionUtil;
import org.jetbrains.idea.maven.navigator.MavenProjectsNavigator;
import org.jetbrains.idea.maven.navigator.MavenProjectsStructure;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import javax.swing.tree.DefaultTreeModel;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.List;

public class ShowMavenProjectVersionHandler {

    private static final Logger LOG = Logger.getInstance(ShowMavenProjectVersionHandler.class);

    public static final ShowMavenProjectVersionHandler INSTANCE = new ShowMavenProjectVersionHandler();

    private ShowMavenProjectVersionHandler() {
    }

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
                projectViewVersionRule = Constants.DEFAULT_PROJECT_VIEW_VERSION_RULE;
            }
            versionRule = projectViewVersionRule.replace(Constants.DEFAULT_VERSION_RULE, "%s");
        } catch (Exception e) {
            versionRule = "%s";
        }
        String version = StringUtils.trimToNull(mavenProject.getMavenId().getVersion());
        addColoredText(data, String.format("%s " + versionRule, "\t", version));
    }

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

        if (true) {
            return;
        }

        PatchedDefaultMutableTreeNode patchedDefaultMutableTreeNode = getPatchedDefaultMutableTreeNode(mavenProjectsNavigator);
        if (patchedDefaultMutableTreeNode == null) {
            return;
        }

        Enumeration patchedDefaultMutableTreeNodeChildren = patchedDefaultMutableTreeNode.children();
        while (patchedDefaultMutableTreeNodeChildren != null && patchedDefaultMutableTreeNodeChildren.hasMoreElements()) {
            Object child = patchedDefaultMutableTreeNodeChildren.nextElement();
            Field userObjectField = ReflectionUtil.getDeclaredField(child.getClass(), "userObject");
            Object userObjectTemp = ReflectionUtil.getFieldValue(userObjectField, child);
            MavenProjectsStructure.ProjectNode userObject;
            if (userObjectTemp instanceof MavenProjectsStructure.ProjectNode) {
                userObject = (MavenProjectsStructure.ProjectNode) userObjectTemp;
                String version = StringUtils.trim(userObject.getMavenProject().getMavenId().getVersion());
                PresentationData presentationData = userObject.getPresentation();
                String formatVersion = String.format("\t %s", version);
                boolean hasFormatVersion = false;
                int hasFormatVersionIndex = -1;

                List<PresentableNodeDescriptor.ColoredFragment> coloredFragmentList = presentationData.getColoredText();
                for (PresentableNodeDescriptor.ColoredFragment coloredFragment : coloredFragmentList) {
                    hasFormatVersionIndex++;
                    if (StringUtils.equals(coloredFragment.getText(), formatVersion)) {
                        hasFormatVersion = true;
                        break;
                    }
                }

                if (!hasFormatVersion) {
                    if (isShow) {
                        addColoredText(presentationData, formatVersion, SimpleTextAttributes.GRAY_SMALL_ATTRIBUTES);
                    }
                } else {
                    if (!isShow) {
                        presentationData.getColoredText().remove(hasFormatVersionIndex);
                    }
                }
            }
        }
    }

    public void refreshMavenProjectView(Project project) {
        ProjectView projectView = ProjectView.getInstance(project);
        if (projectView == null) {
            return;
        }
        projectView.refresh();
    }

    public void refreshMavenStructureView(Project project) {
        resolveMavenStructureView(project, ShowMavenProjectVersionState.getInstance(project).isShowStructureView());
    }

    public void syncIsShowStructureView(Project project) {
        MavenProjectsNavigator mavenProjectsNavigator = MavenProjectsNavigator.getInstance(project);
        if (mavenProjectsNavigator != null) {
            ShowMavenProjectVersionState showMavenProjectVersionState = ShowMavenProjectVersionState.getInstance(project);
            showMavenProjectVersionState.setShowStructureView(mavenProjectsNavigator.getShowVersions());
        }
    }

    public boolean isAllowViewVersionRule(String viewVersionRule) {
        if (StringUtils.isEmpty(viewVersionRule) || !viewVersionRule.contains(Constants.DEFAULT_VERSION_RULE)) {
            return false;
        }
        return true;
    }

    /**
     * get simple tree
     *
     * @param mavenProjectsNavigator
     * @return
     */
    private SimpleTree getSimpleTree(MavenProjectsNavigator mavenProjectsNavigator) {
        try {
            Field myTreeField = ReflectionUtil.getDeclaredField(MavenProjectsNavigator.class, "myTree");
            if (myTreeField == null) {
                return null;
            }
            SimpleTree simpleTree = (SimpleTree) ReflectionUtil.getFieldValue(myTreeField, mavenProjectsNavigator);
            if (simpleTree == null) {
                return null;
            }
            return simpleTree;
        } catch (Exception e) {
            LOG.warn("get simple tree error", e);
        }
        return null;
    }

    /**
     * get patched default mutable tree node
     *
     * @param mavenProjectsNavigator
     * @return
     */
    private PatchedDefaultMutableTreeNode getPatchedDefaultMutableTreeNode(MavenProjectsNavigator mavenProjectsNavigator) {
        try {
            SimpleTree simpleTree = getSimpleTree(mavenProjectsNavigator);
            if (simpleTree == null) {
                return null;
            }
            Field treeModelField = ReflectionUtil.getDeclaredField(SimpleTree.class, "treeModel");
            if (treeModelField == null) {
                return null;
            }
            DefaultTreeModel defaultTreeModel = ReflectionUtil.getFieldValue(treeModelField, simpleTree);
            if (defaultTreeModel == null) {
                return null;
            }
            return (PatchedDefaultMutableTreeNode) defaultTreeModel.getRoot();
        } catch (Exception e) {
            LOG.warn("get patched default mutable tree node error", e);
        }
        return null;
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
