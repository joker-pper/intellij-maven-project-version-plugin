package com.github.jokerpper.mavenprojectversion.ui;

import com.github.jokerpper.mavenprojectversion.constants.SystemConstants;
import com.github.jokerpper.mavenprojectversion.model.UpdateMavenProjectOptions;
import com.github.jokerpper.mavenprojectversion.model.UpdateMavenVersionEffectModel;
import com.github.jokerpper.mavenprojectversion.strategy.impl.UpdateMavenProjectVersionStrategyEnum;
import com.github.jokerpper.mavenprojectversion.support.LanguageUtils;
import com.github.jokerpper.mavenprojectversion.support.MessagesUtils;
import com.github.jokerpper.mavenprojectversion.support.UpdateMavenVersionEffectUtils;
import com.github.jokerpper.mavenprojectversion.support.UserConfUtils;
import com.github.jokerpper.mavenprojectversion.util.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import com.intellij.util.xml.GenericDomValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.dom.model.*;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import javax.swing.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UpdateMavenProjectVersionDialog extends DialogWrapper {

    /**
     * 是否修改properties中的version值
     */
    private static final String IS_CHANGE_VERSION_PROPERTIES_KEY = "update_version.is_change_version_properties";

    private final Project project;
    private final MavenProjectsManager projectsManager;
    private final List<MavenProject> projects;
    private final List<MavenProject> rootProjects;
    private final DomManager domManager;
    private final PsiManager psiManager;

    private final UpdateMavenProjectVersionForm updateMavenProjectVersionForm;

    private MavenProject rootProject;
    private String rootProjectGroupId;
    private String rootProjectArtifactId;
    private UpdateMavenProjectVersionStrategyEnum updateMavenProjectVersionStrategyEnum;

    private String newVersion;
    private Throwable updateVersionThrowable;
    private int updateVersionEffectSize;
    private List<UpdateMavenVersionEffectModel> updateMavenVersionEffectModelList;

    private Date updateStartTime;
    private Date updateEndTime;

    private boolean isChangeVersionProperties;


    public UpdateMavenProjectVersionDialog(@NotNull Project project, boolean canBeParent) {
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
        this.rootProjectArtifactId = StringUtils.trim(rootProject.getMavenId().getArtifactId());
        this.updateMavenProjectVersionStrategyEnum = updateMavenProjectVersionForm.getUpdateMavenProjectVersionStrategy();
        this.newVersion = updateMavenProjectVersionForm.getNewVersion();
        this.updateVersionThrowable = null;
        this.updateVersionEffectSize = 0;
        this.updateMavenVersionEffectModelList = new ArrayList<>(64);
        this.updateStartTime = new Date();
        this.updateEndTime = null;
        this.isChangeVersionProperties = UserConfUtils.getProperty(Boolean.class, IS_CHANGE_VERSION_PROPERTIES_KEY, true);
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
        if (updateVersionThrowable != null) {
            MessagesUtils.showErrorDetailInfoDialog(project, updateVersionThrowable);
        } else {
            try {
                if (updateVersionEffectSize == 0) {
                    MessagesUtils.showErrorDialog(project, LanguageUtils.get(LanguageUtils.Constants.UPDATE_INFO_UPDATE_VERSION_EFFECT_SIZE_IS_ZERO_TEXT), LanguageUtils.get(LanguageUtils.Constants.MESSAGES_ERROR_TITLE));
                } else {
                    //影响详细
                    String effectDetail = UpdateMavenVersionEffectUtils.format(rootProjectGroupId, rootProjectArtifactId, updateMavenVersionEffectModelList, newVersion, LanguageUtils.getCurrentLanguage());
                    //更新版本信息
                    String message = LanguageUtils.parseTemplateValueByKey(LanguageUtils.Constants.UPDATE_INFO_SUCCESS_TEMPLATE, SystemConstants.IDEA_PRODUCT_FULL_NAME, DateFormatUtils.formatDateTime(updateStartTime), DateFormatUtils.formatDateTime(updateEndTime), updateVersionEffectSize);
                    MessagesUtils.showMoreInfoDialog(project, message, LanguageUtils.get(LanguageUtils.Constants.MESSAGES_SUCCESS_TITLE), effectDetail);
                }
            } catch (Throwable throwable) {
                MessagesUtils.showErrorDetailInfoDialog(project, throwable);
            }
        }
    }


    private void updateVersion() {
        boolean hasThrowable = false;

        final Set<String> rootProjectModulePaths = rootProject.getModulePaths();
        final List<String> rootProjectAllArtifactIdList = new ArrayList<>(64);
        final List<MavenProject> toResolveMavenProjects = new ArrayList<>(64);
        final Set<String> rootProjectAllModulePaths;

        try {
            toResolveMavenProjects.add(rootProject);
            rootProjectAllArtifactIdList.add(StringUtils.trim(rootProject.getMavenId().getArtifactId()));

            if (rootProjectModulePaths != null && !rootProjectModulePaths.isEmpty()) {
                //存在多模块时

                rootProjectAllModulePaths = new LinkedHashSet<>(32);

                //获取rootProject下的所有module paths
                toMatchProjectAllModulePaths(rootProjectModulePaths, rootProjectAllModulePaths);

                List<MavenProject> toResolveChildMavenProjects = new ArrayList<>(rootProjectAllModulePaths.size());
                for (MavenProject mavenProject : projects) {
                    MavenId mavenId = mavenProject.getMavenId();
                    //是否需要处理的mavenProject（属于rootProject下的module）
                    boolean isMatchToResolve = rootProjectAllModulePaths.contains(mavenProject.getPath());
                    if (!isMatchToResolve) {
                        continue;
                    }
                    toResolveChildMavenProjects.add(mavenProject);
                    boolean isSameProjectGroupId = StringUtils.equals(rootProjectGroupId, StringUtils.trim(mavenId.getGroupId()));
                    if (isSameProjectGroupId) {
                        String mavenProjectArtifactId = StringUtils.trim(mavenId.getArtifactId());
                        rootProjectAllArtifactIdList.add(mavenProjectArtifactId);
                    }
                }

                //将待处理的子模块进行排序
                List<String> sortModuleKeys = new ArrayList<>(rootProjectAllModulePaths);
                toResolveChildMavenProjects.sort(Comparator.comparing((mavenProject) -> {
                    int index = sortModuleKeys.indexOf(mavenProject.getPath());
                    return index == -1 ? Integer.MAX_VALUE : index;
                }));
                toResolveMavenProjects.addAll(toResolveChildMavenProjects);
            }


            Map<MavenProject, MavenDomProjectModel> mavenProjectMavenDomProjectModelMap = new HashMap<>(64);

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

                if (isChangeVersionProperties) {
                    mavenProjectMavenDomProjectModelMap.put(toResolveMavenProject, rootElement);
                }

                //resolve current project version
                updateProjectVersion(rootProjectGroupId, rootProjectAllArtifactIdList, toResolveMavenProjectMavenId, rootElement, updateMavenProjectOptions);

                //resolve current project parent project version
                updateProjectParentVersion(rootProjectGroupId, rootProjectAllArtifactIdList, rootElement, updateMavenProjectOptions);

                //resolve current project dependencies
                updateProjectDependencyVersion(rootProjectGroupId, rootProjectAllArtifactIdList, rootElement, updateMavenProjectOptions);

                //resolve current project dependencyManagement dependencies
                updateProjectDependencyManagementDependencyVersion(rootProjectGroupId, rootProjectAllArtifactIdList, rootElement, updateMavenProjectOptions);
            }

            //resolve all project properties
            updateChangeVersionProperties(toResolveMavenProjects, mavenProjectMavenDomProjectModelMap);

        } catch (Throwable throwable) {
            hasThrowable = true;
            updateVersionThrowable = throwable;
        } finally {
            if (!hasThrowable) {
                FileDocumentManager.getInstance().saveAllDocuments();
            }
            projectsManager.scheduleForceUpdateMavenProjects(projects);
            updateEndTime = new Date();
            this.close(DialogWrapper.OK_EXIT_CODE);
        }
    }


    /**
     * 匹配Match Paths下所有的Module Paths
     *
     * @param projectModuleMatchPaths
     * @param rootProjectAllModulePaths
     */
    private void toMatchProjectAllModulePaths(Set<String> projectModuleMatchPaths, Set<String> rootProjectAllModulePaths) {
        for (String projectModuleMatchPath : projectModuleMatchPaths) {
            MavenProject mavenProject = projects.stream().filter(it -> StringUtils.equals(projectModuleMatchPath, it.getPath())).findFirst().orElse(null);
            if (mavenProject == null) {
                continue;
            }
            rootProjectAllModulePaths.add(projectModuleMatchPath);
            Set<String> projectModulePaths = mavenProject.getModulePaths();
            if (projectModulePaths == null || projectModulePaths.isEmpty()) {
                continue;
            }
            toMatchProjectAllModulePaths(projectModulePaths, rootProjectAllModulePaths);
        }
    }

    /**
     * 根据当前模块依赖中直接引用修改版本的属性列表记录当前模块以及父模块要修改properties的配置
     *
     * @param updateMavenVersionEffectModel
     * @param options
     */
    private void doResolveUpdateChangeVersionPropertiesConfig(UpdateMavenVersionEffectModel updateMavenVersionEffectModel, ExcerptAndShouldChangeVersionPropertiesOptions options) {
        doResolveUpdateChangeVersionPropertiesConfig(updateMavenVersionEffectModel, true, null, options);
    }

    private void doResolveUpdateChangeVersionPropertiesConfig(UpdateMavenVersionEffectModel updateMavenVersionEffectModel, boolean isFirst, Collection<String> excerptAndShouldChangeVersionProperties, ExcerptAndShouldChangeVersionPropertiesOptions options) {
        if (isFirst) {
            //依赖中直接引用修改版本的属性列表
            excerptAndShouldChangeVersionProperties = updateMavenVersionEffectModel.getExcerptAndShouldChangeVersionProperties();
            if (excerptAndShouldChangeVersionProperties == null || excerptAndShouldChangeVersionProperties.isEmpty()) {
                return;
            }
        }

        String currentKey = options.getUpdateMavenVersionEffectModelKeyFunction().apply(updateMavenVersionEffectModel);
        MavenProject toResolveMavenProject = options.getMavenProjectMap().get(currentKey);
        MavenDomProjectModel rootElement = options.getMavenProjectMavenDomProjectModelMap().get(toResolveMavenProject);
        MavenDomProperties mavenDomProperties = rootElement.getProperties();

        //获取当前项目中对应的实际存在引用版本的变量属性Map
        Map<String, String[]> currentProjectExistExcerptPropertiesMap = VersionUtils.getCurrentProjectExistExcerptPropertiesMap(excerptAndShouldChangeVersionProperties, mavenDomProperties);
        if (!currentProjectExistExcerptPropertiesMap.isEmpty()) {
            //记录当前项目要修改的properties
            Map<String, String[]> excerptAndToChangeVersionPropertiesMap = updateMavenVersionEffectModel.getExcerptAndToChangeVersionPropertiesMap();
            if (excerptAndToChangeVersionPropertiesMap == null) {
                excerptAndToChangeVersionPropertiesMap = new LinkedHashMap<>(32);
                updateMavenVersionEffectModel.setExcerptAndToChangeVersionPropertiesMap(excerptAndToChangeVersionPropertiesMap);
            }

            excerptAndToChangeVersionPropertiesMap.putAll(currentProjectExistExcerptPropertiesMap);
        }

        //获取剩余待处理的引用版本的变量属性
        List<String> remainExcerptProperties = new ArrayList<>(excerptAndShouldChangeVersionProperties);
        remainExcerptProperties.removeAll(currentProjectExistExcerptPropertiesMap.keySet());
        if (remainExcerptProperties.isEmpty()) {
            //已不存在剩余待处理的
            return;
        }

        //递归处理 -- 找到父级项目要修改的properties并写入
        if (updateMavenVersionEffectModel.getProjectParentDetail() == null) {
            return;
        }

        String effectParentModelKey = options.getUpdateMavenVersionEffectModelParentKeyFunction().apply(updateMavenVersionEffectModel);
        UpdateMavenVersionEffectModel updateMavenVersionEffectParentModel = options.getMavenVersionEffectModelMap().get(effectParentModelKey);
        if (updateMavenVersionEffectParentModel == null) {
            return;
        }
        doResolveUpdateChangeVersionPropertiesConfig(updateMavenVersionEffectParentModel, false, remainExcerptProperties, options);
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
                if (!domDependencyVersion.exists()) {
                    continue;
                }

                String versionRawText = domDependencyVersion.getRawText();
                if (updateMavenProjectVersionStrategyEnum.isUpdateProjectDependencyVersion(project, updateMavenProjectVersionForm, versionRawText)) {
                    UpdateMavenVersionEffectUtils.initDependencyDetail(updateMavenProjectOptions.getEffectModel(), domDependency);
                    domDependencyVersion.setValue(newVersion);
                    this.updateVersionEffectSize++;
                } else {
                    if (!isChangeVersionProperties) {
                        continue;
                    }

                    if (VersionUtils.isExcerptVersion(versionRawText) && !VersionUtils.isExcerptProjectVersion(versionRawText)) {
                        UpdateMavenVersionEffectUtils.initExcerptAndShouldChangeVersionProperty(updateMavenProjectOptions.getEffectModel(), versionRawText);
                    }
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
                if (!domDependencyVersion.exists()) {
                    continue;
                }
                String versionRawText = domDependencyVersion.getRawText();
                if (updateMavenProjectVersionStrategyEnum.isUpdateProjectDependencyManagementDependencyVersion(project, updateMavenProjectVersionForm, domDependencyVersion.getRawText())) {
                    UpdateMavenVersionEffectUtils.initDependencyManagementDependencyDetail(updateMavenProjectOptions.getEffectModel(), domDependency);
                    domDependencyVersion.setValue(newVersion);
                    this.updateVersionEffectSize++;
                } else {
                    if (!isChangeVersionProperties) {
                        continue;
                    }

                    if (VersionUtils.isExcerptVersion(versionRawText) && !VersionUtils.isExcerptProjectVersion(versionRawText)) {
                        UpdateMavenVersionEffectUtils.initExcerptAndShouldChangeVersionProperty(updateMavenProjectOptions.getEffectModel(), versionRawText);
                    }
                }
            }
        }
    }

    /**
     * 修改被引用的properties版本
     *
     * @param toResolveMavenProjects
     * @param mavenProjectMavenDomProjectModelMap
     */
    private void updateChangeVersionProperties(List<MavenProject> toResolveMavenProjects, Map<MavenProject, MavenDomProjectModel> mavenProjectMavenDomProjectModelMap) {
        if (!isChangeVersionProperties) {
            //未启用时
            return;
        }

        Function<UpdateMavenVersionEffectModel, String> updateMavenVersionEffectModelKeyFunction = (it) -> it.getProjectDetail().getGroupId() + "-" + it.getProjectDetail().getArtifactId();
        Function<UpdateMavenVersionEffectModel, String> updateMavenVersionEffectModelParentKeyFunction = (it) -> it.getProjectParentDetail().getGroupId() + "-" + it.getProjectParentDetail().getArtifactId();
        Map<String, UpdateMavenVersionEffectModel> mavenVersionEffectModelMap = updateMavenVersionEffectModelList.stream().collect(Collectors.toMap(updateMavenVersionEffectModelKeyFunction, Function.identity()));
        Map<String, MavenProject> mavenProjectMap = toResolveMavenProjects.stream().collect(Collectors.toMap(it -> it.getMavenId().getGroupId() + "-" + it.getMavenId().getArtifactId(), Function.identity()));

        ExcerptAndShouldChangeVersionPropertiesOptions options = new ExcerptAndShouldChangeVersionPropertiesOptions();

        options.setUpdateMavenVersionEffectModelKeyFunction(updateMavenVersionEffectModelKeyFunction);
        options.setUpdateMavenVersionEffectModelParentKeyFunction(updateMavenVersionEffectModelParentKeyFunction);
        options.setMavenVersionEffectModelMap(mavenVersionEffectModelMap);
        options.setMavenProjectMap(mavenProjectMap);
        options.setMavenProjectMavenDomProjectModelMap(mavenProjectMavenDomProjectModelMap);

        //进行处理相关模块的存在被引用且会进行修改版本的properties配置数据
        updateMavenVersionEffectModelList.forEach(updateMavenVersionEffectModel -> {
            doResolveUpdateChangeVersionPropertiesConfig(updateMavenVersionEffectModel, options);
        });

        //进行修改相关模块的properties数据
        updateMavenVersionEffectModelList.forEach(updateMavenVersionEffectModel -> {
            Map<String, String[]> excerptAndToChangeVersionPropertiesMap = updateMavenVersionEffectModel.getExcerptAndToChangeVersionPropertiesMap();
            if (excerptAndToChangeVersionPropertiesMap == null || excerptAndToChangeVersionPropertiesMap.isEmpty()) {
                return;
            }
            String currentKey = updateMavenVersionEffectModelKeyFunction.apply(updateMavenVersionEffectModel);
            MavenProject toResolveMavenProject = mavenProjectMap.get(currentKey);
            MavenDomProjectModel rootElement = mavenProjectMavenDomProjectModelMap.get(toResolveMavenProject);
            MavenDomProperties mavenDomProperties = rootElement.getProperties();
            doUpdateVersionProperties(excerptAndToChangeVersionPropertiesMap, mavenDomProperties, newVersion);
        });
    }


    /**
     * 修改pom中properties版本值
     *
     * @param excerptAndToChangeVersionPropertiesMap
     * @param mavenDomProperties
     * @param newVersion
     */
    public void doUpdateVersionProperties(Map<String, String[]> excerptAndToChangeVersionPropertiesMap,
                                          MavenDomProperties mavenDomProperties,
                                          String newVersion) {
        if (excerptAndToChangeVersionPropertiesMap == null || excerptAndToChangeVersionPropertiesMap.isEmpty()) {
            return;
        }
        XmlTag[] xmlTags = mavenDomProperties.getXmlTag().getSubTags();
        for (XmlTag xmlTag : xmlTags) {
            String xmlTagName = xmlTag.getName();
            if (!excerptAndToChangeVersionPropertiesMap.containsKey(xmlTagName)) {
                continue;
            }
            xmlTag.getValue().setText(newVersion);
            updateVersionEffectSize++;
        }
    }

    static class ExcerptAndShouldChangeVersionPropertiesOptions {
        private Function<UpdateMavenVersionEffectModel, String> updateMavenVersionEffectModelKeyFunction;
        private Function<UpdateMavenVersionEffectModel, String> updateMavenVersionEffectModelParentKeyFunction;
        private Map<String, UpdateMavenVersionEffectModel> mavenVersionEffectModelMap;
        private Map<String, MavenProject> mavenProjectMap;
        private Map<MavenProject, MavenDomProjectModel> mavenProjectMavenDomProjectModelMap;

        public Function<UpdateMavenVersionEffectModel, String> getUpdateMavenVersionEffectModelKeyFunction() {
            return updateMavenVersionEffectModelKeyFunction;
        }

        public void setUpdateMavenVersionEffectModelKeyFunction(Function<UpdateMavenVersionEffectModel, String> updateMavenVersionEffectModelKeyFunction) {
            this.updateMavenVersionEffectModelKeyFunction = updateMavenVersionEffectModelKeyFunction;
        }

        public Function<UpdateMavenVersionEffectModel, String> getUpdateMavenVersionEffectModelParentKeyFunction() {
            return updateMavenVersionEffectModelParentKeyFunction;
        }

        public void setUpdateMavenVersionEffectModelParentKeyFunction(Function<UpdateMavenVersionEffectModel, String> updateMavenVersionEffectModelParentKeyFunction) {
            this.updateMavenVersionEffectModelParentKeyFunction = updateMavenVersionEffectModelParentKeyFunction;
        }

        public Map<String, UpdateMavenVersionEffectModel> getMavenVersionEffectModelMap() {
            return mavenVersionEffectModelMap;
        }

        public void setMavenVersionEffectModelMap(Map<String, UpdateMavenVersionEffectModel> mavenVersionEffectModelMap) {
            this.mavenVersionEffectModelMap = mavenVersionEffectModelMap;
        }

        public Map<String, MavenProject> getMavenProjectMap() {
            return mavenProjectMap;
        }

        public void setMavenProjectMap(Map<String, MavenProject> mavenProjectMap) {
            this.mavenProjectMap = mavenProjectMap;
        }

        public Map<MavenProject, MavenDomProjectModel> getMavenProjectMavenDomProjectModelMap() {
            return mavenProjectMavenDomProjectModelMap;
        }

        public void setMavenProjectMavenDomProjectModelMap(Map<MavenProject, MavenDomProjectModel> mavenProjectMavenDomProjectModelMap) {
            this.mavenProjectMavenDomProjectModelMap = mavenProjectMavenDomProjectModelMap;
        }
    }
}
