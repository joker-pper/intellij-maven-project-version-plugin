package com.github.jokerpper.mavenprojectversion.decorator;

import com.github.jokerpper.mavenprojectversion.handler.ShowMavenProjectVersionHandler;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ProjectViewNodeDecorator;

public class MavenProjectVersionViewDecorator implements ProjectViewNodeDecorator {

    @Override
    public void decorate(ProjectViewNode<?> node, PresentationData data) {
        ShowMavenProjectVersionHandler.INSTANCE.resolveMavenProjectView(node, data);
    }

}
