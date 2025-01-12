package com.leo.javaForum.javaForum.context;

public interface Context {
    <T> T getBean(Class<T> clazz);
    void putBean(Class<?> clazz, Object bean);
    void putAll(Object... beans);
}
