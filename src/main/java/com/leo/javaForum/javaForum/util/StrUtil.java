package com.leo.javaForum.javaForum.util;

public final class StrUtil {
    private StrUtil() {
    }

    public static boolean isInvalidStr(String str) {
        return str == null || str.isBlank();
    }
}
