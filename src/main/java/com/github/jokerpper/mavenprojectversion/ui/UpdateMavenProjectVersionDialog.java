package com.github.jokerpper.mavenprojectversion.ui;

import com.github.jokerpper.mavenprojectversion.model.UpdateMavenProjectOptions;
import com.github.jokerpper.mavenprojectversion.model.UpdateMavenVersionEffectModel;
import com.github.jokerpper.mavenprojectversion.strategy.impl.UpdateMavenProjectVersionStrategyEnum;
import com.github.jokerpper.mavenprojectversion.support.LanguageUtils;
import com.github.jokerpper.mavenprojectversion.support.MessagesUtils;
import com.github.jokerpper.mavenprojectversion.support.UpdateMavenVersionEffectUtils;
import com.github.jokerpper.mavenprojectversion.util.IntellijUtils;
import com.github.jokerpper.mavenprojectversion.util.MatchUtils;
import com.github.jokerpper.mavenprojectversion.util.StringUtils;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
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
import java.util.Set;

public class UpdateMavenProjectVersionDialog extends DialogWrapper {

    private final Project project;
    private final MavenProjectsManager projectsManager;
    private final List<MavenProject> projects;
    private final List<MavenProject> rootProjects;
    private final DomManager domManager;
    private final PsiManager psiManager;

    private final UpdateMavenProjectVersionForm updateMavenProjectVersionForm;

    private MavenProject rootProject;
    private String rootProjectGroupId;
    private UpdateMavenProjectVersionStrategyEnum updateMavenProjectVersionStrategyEnum;

    private String newVersion;
    private Exception updateVersionException;
    private int updateVersionEffectSize;
    private List<UpdateMavenVersionEffectModel> updateMavenVersionEffectModelList;


    public UpdateMavenProjectVersionDialog(@Nullable Project project, boolean canBeParent) {
        super(project, canBeParent);

        this.project = project;
        this.projectsManager = IntellijUtils.getMavenProjectsManager(project);

        this.projects = projectsManager.getProjects();
        this.rootProjects = projectsManager.getRootProjects();
        this.domManager = DomManager.getDomManager(project);
        this.psiManager = PsiManager.getInstance(project);

        this.updateMavenProjectVersionForm = new UpdateMavenProjectVersionForm(this.rootProjects);
        String title = LanguageUtils.get(LanguageUtils.Constants.UPDATE_MAVEN_PROJECT_VERSION_TITLE);

        setTitle(title);
        setOKButtonText(LanguageUtils.get(LanguageUtils.Constants.OK_BUTTON_TEXT));
        setCancelButtonText(LanguageUtils.get(LanguageUtils.Constants.CANCEL_BUTTON_TEXT));
        init();
    }

    @Override
    @Nullable
    protected JComponent createCenterPanel() {
        return updateMavenProjectVersionForm.getJPanelContent();
    }


    private void doOKActionInit() {
        this.rootProject = updateMavenProjectVersionForm.getRootProject();
        this.rootProjectGroupId = StringUtils.trim(rootProject.getMavenId().getGroupId());
        this.updateMavenProjectVersionStrategyEnum = updateMavenProjectVersionForm.getUpdateMavenProjectVersionStrategy();
        this.newVersion = updateMavenProjectVersionForm.getNewVersion();
        this.updateVersionException = null;
        this.updateVersionEffectSize = 0;
        this.updateMavenVersionEffectModelList = new ArrayList<>(64);
    }

    @Override
    protected void doOKAction() {
        doOKActionInit();

        if (!updateMavenProjectVersionStrategyEnum.checkVersionPass(project, updateMavenProjectVersionForm)) {
            //检查版本不通过
            return;
        }

        //检查版本通过后执行更新版本操作
        WriteCommandAction.runWriteCommandAction(project, this::updateVersion);
        if (updateVersionException != null) {
            MessagesUtils.showErrorDialog(project, this.updateVersionException.getMessage(), LanguageUtils.get(LanguageUtils.Constants.MESSAGES_ERROR_TITLE));
        } else {
            if (updateVersionEffectSize == 0) {
                MessagesUtils.showErrorDialog(project, LanguageUtils.get(LanguageUtils.Constants.UPDATE_INFO_UPDATE_VERSION_EFFECT_SIZE_IS_ZERO_TEXT), LanguageUtils.get(LanguageUtils.Constants.MESSAGES_ERROR_TITLE));
            } else {
                String effectDetail = UpdateMavenVersionEffectUtils.format(rootProjectGroupId, updateMavenVersionEffectModelList, newVersion, LanguageUtils.getCurrentLanguage());
                //更新版本影响个数: xxx, 详细: xxx
                String message = LanguageUtils.parseValue(LanguageUtils.get(LanguageUtils.Constants.UPDATE_INFO_SUCCESS_TEMPLATE), updateVersionEffectSize, "\r\n" + effectDetail);
                MessagesUtils.showMoreInfoDialog(project, message, LanguageUtils.get(LanguageUtils.Constants.MESSAGES_SUCCESS_TITLE));
            }
        }
    }


    private void updateVersion() {
        boolean hasException = false;

        final Set<String> rootProjectModulePaths = rootProject.getModulePaths();
        final List<String> rootProjectAllArtifactIdList = new ArrayList<>(64);
        final List<MavenProject> toResolveMavenProjects = new ArrayList<>(64);

        try {
            toResolveMavenProjects.add(rootProject);
            rootProjectAllArtifactIdList.add(StringUtils.trim(rootProject.getMavenId().getArtifactId()));

            for (MavenProject mavenProject : projects) {
                MavenId mavenId = mavenProject.getMavenId();
                //是否需要处理的mavenProject（属于rootProject的module）
                boolean isMatchToResolve = rootProjectModulePaths.contains(mavenProject.getPath());
                if (!isMatchToResolve) {
                    continue;
                }
                toResolveMavenProjects.add(mavenProject);
                boolean isSameProjectGroupId = StringUtils.equals(rootProjectGroupId, StringUtils.trim(mavenProject.getMavenId().getGroupId()));
                if (isSameProjectGroupId) {
                    String mavenProjectArtifactId = StringUtils.trim(mavenId.getArtifactId());
                    rootProjectAllArtifactIdList.add(mavenProjectArtifactId);
                }
            }


            for (MavenProject toResolveMavenProject : toResolveMavenProjects) {
                VirtualFile virtualFile = toResolveMavenProject.getFile();
                PsiFile psiFile = psiManager.findFile(virtualFile);
                DomFileElement<MavenDomProjectModel> domFileElement = domManager.getFileElement((XmlFile) psiFile, MavenDomProjectModel.class);
                if (domFileElement == null) {
                    continue;
                }

                UpdateMavenVersionEffectModel updateMavenVersionEffectModel = new UpdateMavenVersionEffectModel();
                updateMavenVersionEffectModelList.add(updateMavenVersionEffectModel);
                UpdateMavenProjectOptions updateMavenProjectOptions = new UpdateMavenProjectOptions();
                updateMavenProjectOptions.setMavenProject(toResolveMavenProject);
                updateMavenProjectOptions.setEffectModel(updateMavenVersionEffectModel);

                MavenDomProjectModel rootElement = domFileElement.getRootElement();
                MavenId toResolveMavenProjectMavenId = toResolveMavenProject.getMavenId();

                //resolve current project version
                updateProjectVersion(rootProjectGroupId, rootProjectAllArtifactIdList, toResolveMavenProjectMavenId, rootElement, updateMavenProjectOptions);

                //resolve current project parent project version
                updateProjectParentVersion(rootProjectGroupId, rootProjectAllArtifactIdList, rootElement, updateMavenProjectOptions);

                //resolve current project dependencies
                updateProjectDependencyVersion(rootProjectGroupId, rootProjectAllArtifactIdList, rootElement, updateMavenProjectOptions);

                //resolve current project dependencyManagement dependencies
                updateProjectDependencyManagementDependencyVersion(rootProjectGroupId, rootProjectAllArtifactIdList, rootElement, updateMavenProjectOptions);
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

    /**
     * 修改project版本
     *
     * @param rootProjectGroupId
     * @param rootProjectAllArtifactIdList
     * @param toResolveMavenProjectMavenId
     * @param rootElement
     * @param updateMavenProjectOptions
     */
    private void updateProjectVersion(String rootProjectGroupId, List<String> rootProjectAllArtifactIdList, MavenId toResolveMavenProjectMavenId, MavenDomProjectModel rootElement, UpdateMavenProjectOptions updateMavenProjectOptions) {

        boolean isMatchProject = MatchUtils.isMatchProject(rootProjectGroupId, rootProjectAllArtifactIdList, toResolveMavenProjectMavenId);
        UpdateMavenVersionEffectUtils.initProjectDetail(updateMavenProjectOptions.getEffectModel(), updateMavenProjectOptions.getMavenProject(), toResolveMavenProjectMavenId, rootElement, isMatchProject);
        if (!isMatchProject) {
            //非同groupId且未在rootProjectAllArtifactIdList范围内退出
            return;
        }

        GenericDomValue<String> version = rootElement.getVersion();
        if (version.exists() && updateMavenProjectVersionStrategyEnum.isUpdateProjectVersion(project, updateMavenProjectVersionForm, version.getRawText())) {
            //版本存在且通过更新版本条件时进行更新
            version.setValue(newVersion);
            this.updateVersionEffectSize++;
        }
    }

    /**
     * 修改project parent版本
     *
     * @param rootProjectGroupId
     * @param rootProjectAllArtifactIdList
     * @param rootElement
     * @param updateMavenProjectOptions
     */
    private void updateProjectParentVersion(String rootProjectGroupId, List<String> rootProjectAllArtifactIdList, MavenDomProjectModel rootElement, UpdateMavenProjectOptions updateMavenProjectOptions) {
        MavenDomParent mavenDomParent = rootElement.getMavenParent();
        if (!mavenDomParent.exists()) {
            return;
        }

        boolean isMatchProjectParent = MatchUtils.isMatchProjectParent(rootProjectGroupId, rootProjectAllArtifactIdList, mavenDomParent);

        if (!isMatchProjectParent) {
            UpdateMavenVersionEffectUtils.initProjectParentDetail(updateMavenProjectOptions.getEffectModel(), mavenDomParent, false);
            return;
        }

        //parent match to set value
        GenericDomValue<String> parentVersion = mavenDomParent.getVersion();
        if (parentVersion.exists()) {
            //版本存在且通过更新版本条件时进行更新
            boolean isUpdate = updateMavenProjectVersionStrategyEnum.isUpdateProjectParentVersion(project, updateMavenProjectVersionForm, parentVersion.getRawText());
            UpdateMavenVersionEffectUtils.initProjectParentDetail(updateMavenProjectOptions.getEffectModel(), mavenDomParent, isUpdate);
            if (isUpdate) {
                parentVersion.setValue(newVersion);
                this.updateVersionEffectSize++;
            }
        }
    }

    /**
     * 修改project依赖版本
     *
     * @param rootProjectGroupId
     * @param rootProjectAllArtifactIdList
     * @param rootElement
     * @param updateMavenProjectOptions
     */
    private void updateProjectDependencyVersion(String rootProjectGroupId, List<String> rootProjectAllArtifactIdList, MavenDomProjectModel rootElement, UpdateMavenProjectOptions updateMavenProjectOptions) {
        MavenDomDependencies mavenDomDependencies = rootElement.getDependencies();
        if (!mavenDomDependencies.exists()) {
            return;
        }
        List<MavenDomDependency> dependencies = mavenDomDependencies.getDependencies();
        for (MavenDomDependency domDependency : dependencies) {
            if (MatchUtils.isMatchMavenDomDependency(rootProjectGroupId, rootProjectAllArtifactIdList, domDependency)) {
                //match dependency
                GenericDomValue<String> domDependencyVersion = domDependency.getVersion();
                if (domDependencyVersion.exists() && updateMavenProjectVersionStrategyEnum.isUpdateProjectDependencyVersion(project, updateMavenProjectVersionForm, domDependencyVersion.getRawText())) {
                    UpdateMavenVersionEffectUtils.initDependencyDetail(updateMavenProjectOptions.getEffectModel(), domDependency);
                    domDependencyVersion.setValue(newVersion);
                    this.updateVersionEffectSize++;
                }
            }
        }
    }

    /**
     * 修改project dependencyManagementDependency版本
     *
     * @param rootProjectGroupId
     * @param rootProjectAllArtifactIdList
     * @param rootElement
     * @param updateMavenProjectOptions
     */
    private void updateProjectDependencyManagementDependencyVersion(String rootProjectGroupId, List<String> rootProjectAllArtifactIdList, MavenDomProjectModel rootElement, UpdateMavenProjectOptions updateMavenProjectOptions) {
        MavenDomDependencies mavenDependencyManagementDomDependencies = rootElement.getDependencyManagement().getDependencies();
        if (!mavenDependencyManagementDomDependencies.exists()) {
            return;
        }
        List<MavenDomDependency> dependencies = mavenDependencyManagementDomDependencies.getDependencies();
        for (MavenDomDependency domDependency : dependencies) {
            if (MatchUtils.isMatchMavenDomDependency(rootProjectGroupId, rootProjectAllArtifactIdList, domDependency)) {
                //match dependency
                GenericDomValue<String> domDependencyVersion = domDependency.getVersion();
                if (domDependencyVersion.exists() && updateMavenProjectVersionStrategyEnum.isUpdateProjectDependencyManagementDependencyVersion(project, updateMavenProjectVersionForm, domDependencyVersion.getRawText())) {
                    UpdateMavenVersionEffectUtils.initDependencyManagementDependencyDetail(updateMavenProjectOptions.getEffectModel(), domDependency);
                    domDependencyVersion.setValue(newVersion);
                    this.updateVersionEffectSize++;
                }
            }
        }
    }

}
