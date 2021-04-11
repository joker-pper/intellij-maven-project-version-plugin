package com.github.jokerpper.mavenprojectversion.decorator;

import com.github.jokerpper.mavenprojectversion.handler.ShowMavenProjectVersionHandler;
import com.github.jokerpper.mavenprojectversion.util.IntellijUtils;
import com.github.jokerpper.mavenprojectversion.util.StringUtils;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ProjectViewNodeDecorator;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.packageDependencies.ui.PackageDependenciesNode;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class MavenProjectVersionViewDecorator implements ProjectViewNodeDecorator {

    @Override
    public void decorate(ProjectViewNode node, PresentationData data) {
        ShowMavenProjectVersionHandler.INSTANCE.resolveMavenProjectView(node, data);
    }

    @Override
    public void decorate(PackageDependenciesNode node, ColoredTreeCellRenderer cellRenderer) {
    }
}
