package springboot.utils;

import springboot.annotation.ioc.Component;

public class BeanUtil {
    /**
     * Get the class name
     *
     * @param clazz the class
     * @return the bean name
     */
    public static String getBeanName(Class<?> clazz) {
        String name = clazz.getName();
        if (clazz.isAnnotationPresent(Component.class)) {
            Component component = clazz.getAnnotation(Component.class);
            name = "".equals(component.name()) ? clazz.getName() : component.name();
        }
        return name;
    }
}
