package com.github.jokerpper.mavenprojectversion.support;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

public class MessagesUtils {

    /**
     * 显示信息
     *
     * @param project
     * @param message
     * @param title
     */
    public static void showInfoDialog(Project project, String message, String title) {
        Messages.showDialog(project, message, title, new String[]{getMessagesTipOkText()}, 0, Messages.getInformationIcon());
    }

    /**
     * 显示更多信息(可复制信息内容)
     *
     * @param project
     * @param message
     * @param title
     */
    public static void showMoreInfoDialog(Project project, String message, String title) {
        Messages.showDialog(project, message, title, new String[]{getMessagesTipOkText()}, 1, Messages.getInformationIcon());
    }

    /**
     * 显示警告信息
     *
     * @param project
     * @param message
     * @param title
     */
    public static void showWarningDialog(Project project, String message, String title) {
        Messages.showDialog(project, message, title, new String[]{getMessagesTipOkText()}, 0, Messages.getWarningIcon());
    }

    /**
     * 显示错误信息
     *
     * @param project
     * @param message
     * @param title
     */
    public static void showErrorDialog(Project project, String message, String title) {
        Messages.showDialog(project, message, title, new String[]{getMessagesTipOkText()}, 0, Messages.getErrorIcon());
    }

    /**
     * 获取ok文本
     *
     * @return
     */
    private static String getMessagesTipOkText() {
        return LanguageUtils.get(LanguageUtils.Constants.MESSAGES_TIP_OK_TEXT);
    }
}
