package springboot.core.aop.factory;

import springboot.annotation.aop.Aspect;
import springboot.annotation.aop.Order;
import springboot.utils.ReflectionUtil;
import springboot.core.aop.intercept.BeanValidationInterceptor;
import springboot.core.aop.intercept.Interceptor;
import springboot.core.aop.intercept.InternallyAspectInterceptor;
import springboot.exception.CannotInitializeConstructorException;
import springboot.factory.ClassFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The factory of interceptor
 */
public class InterceptorFactory {
    // All the interceptors
    private static List<Interceptor> interceptors = new ArrayList<>();

    public static void loadInterceptors(String[] packageName) {
        // Get all class implement the interceptor
        Set<Class<? extends Interceptor>> interceptorsClasses = ReflectionUtil.getSubClass(packageName, Interceptor.class);
        // Get all @Aspect class
        Set<Class<?>> aspects = ClassFactory.CLASSES.get(Aspect.class);
        interceptorsClasses.forEach(interceptorClass -> {
            try {
                interceptors.add(interceptorClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new CannotInitializeConstructorException("not init constructor , the interceptor name :" +
                        interceptorClass.getSimpleName());
            }
        });
        aspects.forEach(clazz -> {
            Object obj = ReflectionUtil.newInstance(clazz);
            Interceptor interceptor = new InternallyAspectInterceptor(obj);
            if (clazz.isAnnotationPresent(Order.class)) {
                Order order = clazz.getAnnotation(Order.class);
                interceptor.setOrder(order.value());
            }
            interceptors.add(interceptor);
        });
        // add bean validation
        interceptors.add(new BeanValidationInterceptor());
        // sort by the order
        interceptors = interceptors.stream().
                sorted(Comparator.comparing(Interceptor::getOrder)).
                collect(Collectors.toList());
    }

    public static List<Interceptor> getInterceptors() {
        return interceptors;
    }
}
