package com.github.jokerpper.mavenprojectversion.ui;

import com.github.jokerpper.mavenprojectversion.strategy.UpdateMavenProjectVersionStrategyEnum;
import com.github.jokerpper.mavenprojectversion.util.IntellijUtils;
import com.github.jokerpper.mavenprojectversion.util.MatchUtils;
import com.github.jokerpper.mavenprojectversion.util.StringUtils;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import com.intellij.util.xml.GenericDomValue;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.dom.model.MavenDomDependencies;
import org.jetbrains.idea.maven.dom.model.MavenDomDependency;
import org.jetbrains.idea.maven.dom.model.MavenDomParent;
import org.jetbrains.idea.maven.dom.model.MavenDomProjectModel;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class UpdateMavenProjectVersionDialog extends DialogWrapper {

    private static final String title = "Update Maven Project Version";

    private final Project project;
    private final MavenProjectsManager projectsManager;
    private final List<MavenProject> projects;
    private final List<MavenProject> rootProjects;
    private final DomManager domManager;
    private final PsiManager psiManager;

    private final UpdateMavenProjectVersionForm updateMavenProjectVersionForm;

    private MavenProject rootProject;
    private UpdateMavenProjectVersionStrategyEnum updateMavenProjectVersionStrategyEnum;

    private String newVersion;

    private Exception updateVersionException;

    public UpdateMavenProjectVersionDialog(@Nullable Project project, boolean canBeParent) {
        super(project, canBeParent);

        this.project = project;
        this.projectsManager = IntellijUtils.getMavenProjectsManager(project);

        this.projects = projectsManager.getProjects();
        this.rootProjects = projectsManager.getRootProjects();
        this.domManager = DomManager.getDomManager(project);
        this.psiManager = PsiManager.getInstance(project);

        this.updateMavenProjectVersionForm = new UpdateMavenProjectVersionForm(this.rootProjects);

        setTitle(title);
        init();
    }

    @Override
    @Nullable
    protected JComponent createCenterPanel() {
        return updateMavenProjectVersionForm.getJPanelContent();
    }

    @Override
    protected void doOKAction() {
        this.rootProject = updateMavenProjectVersionForm.getRootProject();
        this.updateMavenProjectVersionStrategyEnum = updateMavenProjectVersionForm.getUpdateMavenProjectVersionStrategy();
        this.newVersion = updateMavenProjectVersionForm.getNewVersion();
        this.updateVersionException = null;

        if (updateMavenProjectVersionStrategyEnum.checkVersion(project, updateMavenProjectVersionForm)) {
            WriteCommandAction.runWriteCommandAction(project, this::updateVersion);
        }

        if (updateVersionException != null) {
            Messages.showErrorDialog(project, this.updateVersionException.getMessage(), "Error");
        }
    }


    private void updateVersion() {
        boolean hasException = false;

        final List<String> projectAllArtifactIdList = new ArrayList<>(32);

        try {
            final String rootProjectGroupId = StringUtils.trim(rootProject.getMavenId().getGroupId());
            final String rootProjectDirectory = rootProject.getDirectory();
            for (MavenProject mavenProject : projects) {
                MavenId mavenId = mavenProject.getMavenId();
                if (rootProjectGroupId.equals(mavenId.getGroupId()) && mavenProject.getDirectory().contains(rootProjectDirectory)) {
                    String mavenProjectArtifactId = StringUtils.trim(mavenId.getArtifactId());
                    projectAllArtifactIdList.add(mavenProjectArtifactId);
                }
            }

            for (MavenProject mavenProject : projects) {
                boolean isMatchProject = MatchUtils.isMatchProject(rootProjectGroupId, projectAllArtifactIdList, mavenProject.getMavenId());
                if (!isMatchProject) {
                    continue;
                }

                VirtualFile virtualFile = mavenProject.getFile();
                PsiFile psiFile = psiManager.findFile(virtualFile);
                DomFileElement<MavenDomProjectModel> domFileElement = domManager.getFileElement((XmlFile) psiFile, MavenDomProjectModel.class);
                if (domFileElement == null) {
                    continue;
                }

                MavenDomProjectModel rootElement = domFileElement.getRootElement();

                //resolve project version
                GenericDomValue<String> version = rootElement.getVersion();
                if (version.exists() && updateMavenProjectVersionStrategyEnum.isUpdateProjectVersion(project, updateMavenProjectVersionForm, version.getRawText())) {
                    version.setValue(newVersion);
                }

                //resolve parent project version
                MavenDomParent mavenDomParent = rootElement.getMavenParent();
                if (mavenDomParent.exists() && MatchUtils.isMatchProjectParent(rootProjectGroupId, projectAllArtifactIdList, mavenDomParent)) {
                    //parent match to set value
                    GenericDomValue<String> parentVersion = mavenDomParent.getVersion();
                    if (parentVersion.exists() && updateMavenProjectVersionStrategyEnum.isUpdateProjectParentVersion(project, updateMavenProjectVersionForm, parentVersion.getRawText())) {
                        parentVersion.setValue(newVersion);
                    }
                }

                //resolve dependencies
                MavenDomDependencies mavenDomDependencies = rootElement.getDependencies();
                if (mavenDomDependencies.exists()) {
                    List<MavenDomDependency> dependencies = mavenDomDependencies.getDependencies();
                    for (MavenDomDependency domDependency : dependencies) {
                        if (MatchUtils.isMatchMavenDomDependency(rootProjectGroupId, projectAllArtifactIdList, domDependency)) {
                            //match dependency
                            GenericDomValue<String> domDependencyVersion = domDependency.getVersion();
                            if (domDependencyVersion.exists() && updateMavenProjectVersionStrategyEnum.isUpdateProjectDependency(project, updateMavenProjectVersionForm, domDependencyVersion.getRawText())) {
                                domDependencyVersion.setValue(newVersion);
                            }
                        }
                    }
                }

                //resolve dependencyManagement dependencies
                MavenDomDependencies mavenDependencyManagementDomDependencies = rootElement.getDependencyManagement().getDependencies();
                if (mavenDependencyManagementDomDependencies.exists()) {
                    List<MavenDomDependency> dependencies = mavenDependencyManagementDomDependencies.getDependencies();
                    for (MavenDomDependency domDependency : dependencies) {
                        if (MatchUtils.isMatchMavenDomDependency(rootProjectGroupId, projectAllArtifactIdList, domDependency)) {
                            //match dependency
                            GenericDomValue<String> domDependencyVersion = domDependency.getVersion();
                            if (domDependencyVersion.exists() && updateMavenProjectVersionStrategyEnum.isUpdateProjectDependencyManagementDependency(project, updateMavenProjectVersionForm, domDependencyVersion.getRawText())) {
                                domDependencyVersion.setValue(newVersion);
                            }
                        }
                    }
                }
            }
        } catch (Exception exception) {
            hasException = true;
            updateVersionException = exception;
        } finally {
            if (!hasException) {
                FileDocumentManager.getInstance().saveAllDocuments();
            }
            projectsManager.forceUpdateProjects(projects);
            this.close(DialogWrapper.OK_EXIT_CODE);
        }
    }

}
