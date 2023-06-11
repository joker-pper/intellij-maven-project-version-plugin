package com.github.jokerpper.mavenprojectversion.util;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ThrowableUtils {

    /**
     * 获取异常信息内容
     *
     * @param cause
     * @return
     */
    public static String getStackTraceContent(Throwable cause) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8), true);
            cause.printStackTrace(printWriter);
            printWriter.flush();
            return StringUtils.toString(new ByteArrayInputStream(out.toByteArray()), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            return "【ThrowableUtils.getStackTraceContent】 has error, error cause: " + ex.getMessage() + "; and before to parse cause message is: " + cause.getMessage();
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
            }
        }
    }

}
