package springboot.core.aop.intercept;

import springboot.core.aop.factory.InterceptorFactory;

public abstract class AbstractAopProxyBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInit(Object bean) {
        Object wrapperProxyBean = bean;
        for (Interceptor interceptor : InterceptorFactory.getInterceptors()) {
            if (interceptor.supports(bean)) {
                wrapperProxyBean = wrapBean(wrapperProxyBean, interceptor);
            }
        }
        return wrapperProxyBean;
    }

    public abstract Object wrapBean(Object target, Interceptor interceptor);
}
