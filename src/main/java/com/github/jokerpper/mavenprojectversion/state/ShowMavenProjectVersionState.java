package com.github.jokerpper.mavenprojectversion.state;

import com.github.jokerpper.mavenprojectversion.constants.SystemConstants;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "ShowMavenProjectVersion", storages = {
        @Storage("workspace.xml")
       // @Storage("show-maven-project-version.xml")
})
public class ShowMavenProjectVersionState implements PersistentStateComponent<ShowMavenProjectVersionState> {

    private boolean showProjectView = false;

    private boolean showStructureView = false;

    private String projectViewVersionRule = SystemConstants.DEFAULT_PROJECT_VIEW_VERSION_RULE;

    public boolean isShowProjectView() {
        return showProjectView;
    }

    public void setShowProjectView(boolean showProjectView) {
        this.showProjectView = showProjectView;
    }

    public boolean isShowStructureView() {
        return showStructureView;
    }

    public void setShowStructureView(boolean showStructureView) {
        this.showStructureView = showStructureView;
    }

    public String getProjectViewVersionRule() {
        return projectViewVersionRule;
    }

    public void setProjectViewVersionRule(String projectViewVersionRule) {
        this.projectViewVersionRule = projectViewVersionRule;
    }

    @Override
    public @Nullable ShowMavenProjectVersionState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ShowMavenProjectVersionState state) {
        XmlSerializerUtil.copyBean(state, this);
    }


    public static ShowMavenProjectVersionState getInstance(@NotNull Project project) {
        return project.getComponent(ShowMavenProjectVersionState.class);
    }
}
