package com.github.jokerpper.mavenprojectversion.support;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;

/**
 * Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 * <p>
 * copy from org.jetbrains.idea.maven.utils.actions.MavenAction
 */
public abstract class MavenAction extends AnAction implements DumbAware {
    @Override
    public void update(@NotNull AnActionEvent event) {
        Presentation p = event.getPresentation();
        p.setEnabled(isAvailable(event));
        p.setVisible(isVisible(event));
    }

    protected boolean isAvailable(@NotNull AnActionEvent event) {
        return MavenActionUtil.hasProject(event.getDataContext());
    }

    protected boolean isVisible(@NotNull AnActionEvent event) {
        return true;
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}