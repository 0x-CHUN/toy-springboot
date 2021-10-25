package springboot.core.ioc;

import springboot.annotation.config.Value;
import springboot.annotation.ioc.Autowired;
import springboot.annotation.ioc.Qualifier;
import springboot.core.aop.factory.AopProxyBeanPostProcessorFactory;
import springboot.core.aop.intercept.BeanPostProcessor;
import springboot.utils.BeanUtil;
import springboot.utils.ObjectUtil;
import springboot.utils.ReflectionUtil;
import springboot.core.config.ConfigurationManager;
import springboot.exception.CanNotDetermineTargetBeanException;
import springboot.exception.InterfaceNotImplementedException;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AutowiredBeanInitialization {
    private final String[] packageNames;

    public AutowiredBeanInitialization(String[] packageNames) {
        this.packageNames = packageNames;
    }

    private static final Map<String, Object> SINGLETON_OBJECTS = new ConcurrentHashMap<>();

    public void initialize(Object bean) {
        Class<?> beanClass = bean.getClass();
        Field[] beanFields = beanClass.getDeclaredFields();
        if (beanFields.length > 0) {
            for (Field beanField : beanFields) {
                if (beanField.isAnnotationPresent(Autowired.class)) {
                    Object beanFieldInstance = processAutowiredAnnotationField(beanField);
                    String beanFieldName = BeanUtil.getBeanName(beanField.getType());
                    // solve cycle dependency
                    beanFieldInstance = resolveCircularDependency(bean, beanFieldInstance, beanFieldName);
                    // aop
                    BeanPostProcessor postProcessor = AopProxyBeanPostProcessorFactory.get(beanField.getType());
                    beanFieldInstance = postProcessor.postProcessAfterInit(beanFieldInstance);
                    ReflectionUtil.setField(bean, beanField, beanFieldInstance);
                }
                if (beanField.isAnnotationPresent(Value.class)) {
                    Object value = processValueAnnotationField(beanField);
                    ReflectionUtil.setField(bean, beanField, value);
                }
            }
        }
    }

    /**
     * solve the circular dependency
     */
    private Object resolveCircularDependency(Object bean, Object beanFieldInstance, String beanFieldName) {
        if (SINGLETON_OBJECTS.containsKey(beanFieldName)) {
            beanFieldInstance = SINGLETON_OBJECTS.get(beanFieldName);
        } else {
            SINGLETON_OBJECTS.put(beanFieldName, beanFieldInstance);
            initialize(bean);
        }
        return beanFieldInstance;
    }

    /**
     * Process @Value bean field
     *
     * @return the bean of the specific field
     */
    private Object processValueAnnotationField(Field beanField) {
        String key = beanField.getDeclaredAnnotation(Value.class).value();
        ConfigurationManager manager = (ConfigurationManager)
                BeanFactory.BEANS.get(ConfigurationManager.class.getName());
        String value = manager.getString(key);
        if (value == null) {
            throw new IllegalArgumentException("can not find target value for property:{" + key + "}");
        }
        return ObjectUtil.convert(beanField.getType(), value);
    }

    /**
     * Process @Autowired bean field
     *
     * @return the bean of the specific field
     */
    private Object processAutowiredAnnotationField(Field beanField) {
        Class<?> beanFieldClass = beanField.getType();
        String beanFieldName = BeanUtil.getBeanName(beanFieldClass);
        Object beanFieldInstance;
        if (beanFieldClass.isInterface()) {
            Set<Class<?>> subClasses = ReflectionUtil.getSubClass(packageNames, (Class<Object>) beanFieldClass);
            if (subClasses.size() == 0) {
                throw new InterfaceNotImplementedException(beanFieldClass.getName() +
                        "is interface and do not have implemented class exception");
            }
            if (subClasses.size() == 1) {
                Class<?> subClass = subClasses.iterator().next();
                beanFieldName = BeanUtil.getBeanName(subClass);
            }
            if (subClasses.size() > 1) {
                Qualifier qualifier = beanField.getDeclaredAnnotation(Qualifier.class);
                beanFieldName = qualifier == null ? beanFieldName : qualifier.value();
            }
        }
        beanFieldInstance = BeanFactory.BEANS.get(beanFieldName);
        if (beanFieldInstance == null) {
            throw new CanNotDetermineTargetBeanException("can not determine target bean of" + beanFieldClass.getName());
        }
        return beanFieldInstance;
    }
}
