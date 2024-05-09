package com.github.jokerpper.mavenprojectversion.decorator;

import com.github.jokerpper.mavenprojectversion.handler.ShowMavenProjectVersionHandler;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ProjectViewNodeDecorator;
import com.intellij.packageDependencies.ui.PackageDependenciesNode;
import com.intellij.ui.ColoredTreeCellRenderer;

public class MavenProjectVersionViewDecorator implements ProjectViewNodeDecorator {

    @Override
    public void decorate(ProjectViewNode<?> node, PresentationData data) {
        ShowMavenProjectVersionHandler.INSTANCE.resolveMavenProjectView(node, data);
    }

    @Override
    public void decorate(PackageDependenciesNode node, ColoredTreeCellRenderer cellRenderer) {
    }
}
