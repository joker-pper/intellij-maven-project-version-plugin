package com.github.jokerpper.mavenprojectversion.support;

import com.github.jokerpper.mavenprojectversion.util.ThrowableUtils;
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
     * 显示更多信息(可进行复制信息内容)
     *
     * @param project
     * @param message
     * @param title
     * @param detailInfo
     */
    public static void showMoreInfoDialog(Project project, String message, String title, String detailInfo) {
        Messages.showDialog(project, message, title, detailInfo, new String[]{getMessagesTipOkText()}, 0, -1, Messages.getInformationIcon());
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
     * @param message - 不支持过长内容
     * @param title
     */
    public static void showErrorDialog(Project project, String message, String title) {
        Messages.showDialog(project, message, title, new String[]{getMessagesTipOkText()}, 0, Messages.getErrorIcon());
    }

    /**
     * 显示默认错误详细信息
     *
     * @param project
     * @param throwable
     */
    public static void showErrorDetailInfoDialog(Project project, Throwable throwable) {
        showDefaultErrorMoreInfoDialog(project, LanguageUtils.get(LanguageUtils.Constants.MESSAGES_ERROR_TITLE), ThrowableUtils.getStackTraceContent(throwable));
    }

    /**
     * 显示默认错误详细信息
     *
     * @param project
     * @param title
     * @param detailInfo
     */
    public static void showDefaultErrorMoreInfoDialog(Project project, String title, String detailInfo) {
        showErrorMoreInfoDialog(project, LanguageUtils.get(LanguageUtils.Constants.MESSAGES_SHOW_DEFAULT_ERROR_MORE_INFO_DIALOG_MESSAGE_TEXT), title, detailInfo);
    }

    /**
     * 显示错误详细信息
     *
     * @param project
     * @param message    - 不支持过长内容
     * @param title
     * @param detailInfo
     */
    public static void showErrorMoreInfoDialog(Project project, String message, String title, String detailInfo) {
        Messages.showDialog(project, message, title, detailInfo, new String[]{getMessagesTipOkText()}, 0, -1, Messages.getErrorIcon());
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
