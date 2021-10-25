package springboot.core.aop.factory;

import springboot.core.aop.intercept.BeanPostProcessor;
import springboot.core.aop.intercept.CglibAopProxyBeanPostProcessor;
import springboot.core.aop.intercept.JdkAopProxyBeanPostProcessor;

public class AopProxyBeanPostProcessorFactory {
    public static BeanPostProcessor get(Class<?> beanClass) {
        if (beanClass.isInterface() || beanClass.getInterfaces().length > 0) {
            return new JdkAopProxyBeanPostProcessor();
        } else {
            return new CglibAopProxyBeanPostProcessor();
        }
    }
}
