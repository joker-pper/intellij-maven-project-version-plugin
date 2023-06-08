package com.github.jokerpper.mavenprojectversion.handler;

import com.github.jokerpper.mavenprojectversion.support.UserPropertiesUtils;
import com.intellij.openapi.project.Project;

public class ActionPerformedStartHandler {

    public static final ActionPerformedStartHandler INSTANCE = new ActionPerformedStartHandler();

    public void start(Project project) {
        //加载用户配置
        UserPropertiesUtils.init(project);
    }

}
