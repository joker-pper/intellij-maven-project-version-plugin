package com.github.jokerpper.mavenprojectversion.model;

import org.jetbrains.idea.maven.project.MavenProject;

public class UpdateMavenProjectOptions {

    private MavenProject mavenProject;

    private UpdateMavenVersionEffectModel effectModel;

    public MavenProject getMavenProject() {
        return mavenProject;
    }

    public void setMavenProject(MavenProject mavenProject) {
        this.mavenProject = mavenProject;
    }

    public UpdateMavenVersionEffectModel getEffectModel() {
        return effectModel;
    }

    public void setEffectModel(UpdateMavenVersionEffectModel effectModel) {
        this.effectModel = effectModel;
    }
}
