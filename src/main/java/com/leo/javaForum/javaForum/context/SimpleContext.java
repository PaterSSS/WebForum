package com.leo.javaForum.javaForum.context;



import com.leo.javaForum.javaForum.annotations.InjectLogger;
import com.leo.javaForum.javaForum.repositories.CategoryInfoRepository;
import com.leo.javaForum.javaForum.repositories.CommentRepository;
import com.leo.javaForum.javaForum.repositories.PostRepository;
import com.leo.javaForum.javaForum.repositories.UserProfileRepository;
import com.leo.javaForum.javaForum.repositories.connetion.DatabaseConnector;
import com.leo.javaForum.javaForum.services.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public final class SimpleContext implements Context {
    private final Map<Class<?>, Object> beans = new HashMap<>();
    private static SimpleContext instance;
    private List<Class<?>> allowedInterfaces;

    private SimpleContext() {
    }

    public static SimpleContext getInstance() {
        if (instance == null) {
            instance = new SimpleContext();
            instance.fillAllowedInterfaces();
        }
        return instance;
    }

    private void fillAllowedInterfaces() {
        allowedInterfaces = List.of(DatabaseConnector.class, CategoryInfoRepository.class, UserProfileRepository.class,
                CategoryService.class, PostService.class, UserService.class, AuthentificationService.class,
                PostRepository.class, CategoryInfoRepository.class, UserProfileRepository.class, CommentRepository.class,
                CommentService.class);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> clazz) {
        return (T) beans.get(clazz);
    }

    @Override
    public void putBean(Class<?> clazz, Object bean) {
        injectLogger(bean);
        beans.put(clazz, bean);
    }

    @Override
    public void putAll(Object... objects) {
        for (Object bean : objects) regBeanWithoutClass(bean);
    }

    private void injectLogger(Object bean) {
        Method[] methods = bean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(InjectLogger.class)) {
                try {
                    method.invoke(bean, getBean(Logger.class));
                } catch (IllegalAccessException | InvocationTargetException ignored) {
                }
            }
        }
    }

    private void regBeanWithoutClass(Object bean) {
        for (var interfaces : bean.getClass().getInterfaces()) {
            if (allowedInterfaces.contains(interfaces)) {
                putBean(interfaces, bean);
                return;
            }
        }
        putBean(bean.getClass(), bean);
    }
}
