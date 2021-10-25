package springboot.core.ioc;

import springboot.annotation.ioc.Component;
import springboot.annotation.mvc.RestController;
import springboot.utils.BeanUtil;
import springboot.utils.ReflectionUtil;
import springboot.core.aop.factory.AopProxyBeanPostProcessorFactory;
import springboot.core.aop.intercept.BeanPostProcessor;
import springboot.core.config.ConfigurationFactory;
import springboot.core.config.ConfigurationManager;
import springboot.exception.BeanNotFoundException;
import springboot.factory.ClassFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BeanFactory {
    // ioc container
    public static final Map<String, Object> BEANS = new ConcurrentHashMap<>();
    private static final Map<String, String[]> SINGLE_BEAN_NAMES_TYPE_MAP = new ConcurrentHashMap<>();

    public static void loadBeans() {
        // load the bean component
        ClassFactory.CLASSES.get(Component.class).forEach(clazz -> {
            String beanName = BeanUtil.getBeanName(clazz);
            Object obj = ReflectionUtil.newInstance(clazz);
            BEANS.put(beanName, obj);
        });
        // load the controller
        ClassFactory.CLASSES.get(RestController.class).forEach(clazz -> {
            Object obj = ReflectionUtil.newInstance(clazz);
            BEANS.put(clazz.getName(), obj);
        });
        // load the configuration
        BEANS.put(ConfigurationManager.class.getName(), new ConfigurationManager(ConfigurationFactory.getConfig()));
    }

    // apply the post processors
    public static void applyBeanPostProcessors() {
        BEANS.replaceAll((beanName, beanInstance) -> {
            BeanPostProcessor postProcessor = AopProxyBeanPostProcessorFactory.get(beanInstance.getClass());
            return postProcessor.postProcessAfterInit(beanInstance);
        });
    }

    // get the specific type of bean
    public static <T> T getBean(Class<T> type) {
        String[] beanNames = getBeanNamesForType(type);
        if (beanNames.length == 0) {
            throw new BeanNotFoundException("Not found bean implement, bean : " + type.getName());
        }
        Object beanInstance = BEANS.get(beanNames[0]);
        if (!type.isInstance(beanInstance)) {
            throw new BeanNotFoundException("Not found bean implement, bean : " + type.getName());
        }
        return type.cast(beanInstance);
    }

    private static <T> String[] getBeanNamesForType(Class<T> type) {
        String beanName = type.getName();
        String[] beanNames = SINGLE_BEAN_NAMES_TYPE_MAP.get(beanName);
        if (beanNames == null) {
            List<String> beanNamesList = new ArrayList<>();
            for (Map.Entry<String, Object> entry : BEANS.entrySet()) {
                Class<?> beanClass = entry.getValue().getClass();
                if (type.isInterface()) {
                    Class<?>[] interfaces = beanClass.getInterfaces();
                    for (Class<?> c : interfaces) {
                        if (type.getName().equals(c.getName())) {
                            beanNamesList.add(entry.getKey());
                            break;
                        }
                    }
                } else if (beanClass.isAssignableFrom(type)) {
                    beanNamesList.add(entry.getKey());
                }
            }
            beanNames = beanNamesList.toArray(new String[0]);
            SINGLE_BEAN_NAMES_TYPE_MAP.put(beanName, beanNames);
        }
        return beanNames;
    }

    // get the specific type of bean
    public static <T> Map<String, T> getBeanNamesOfType(Class<T> type) {
        Map<String, T> result = new HashMap<>();
        String[] beanNames = getBeanNamesForType(type);
        for (String beanName : beanNames) {
            Object beanInstance = BEANS.get(beanName);
            if (!type.isInstance(beanInstance)) {
                throw new BeanNotFoundException("Not found bean implement, bean : " + type.getName());
            }
            result.put(beanName, type.cast(beanInstance));
        }
        return result;
    }
}
