package springboot.factory;

import springboot.annotation.aop.Aspect;
import springboot.annotation.ioc.Component;
import springboot.annotation.mvc.RestController;
import springboot.utils.ReflectionUtil;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ClassFactory {
    public static final Map<Class<? extends Annotation>, Set<Class<?>>> CLASSES = new ConcurrentHashMap<>();

    /**
     * Load all the class in the package, include the RestController
     *
     * @param packageName the package name
     */
    public static void loadClass(String[] packageName) {
        // Scan
        Set<Class<?>> controllers = ReflectionUtil.scanAnnotatedClass(packageName, RestController.class);
        Set<Class<?>> components = ReflectionUtil.scanAnnotatedClass(packageName, Component.class);
        Set<Class<?>> aspects = ReflectionUtil.scanAnnotatedClass(packageName, Aspect.class);
        // Store
        CLASSES.put(RestController.class, controllers);
        CLASSES.put(Component.class, components);
        CLASSES.put(Aspect.class,aspects);
    }
}
