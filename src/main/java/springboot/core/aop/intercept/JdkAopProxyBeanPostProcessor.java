package springboot.core.aop.intercept;

import springboot.core.aop.proxy.JDKAspectProxy;

public class JdkAopProxyBeanPostProcessor extends AbstractAopProxyBeanPostProcessor {
    @Override
    public Object wrapBean(Object target, Interceptor interceptor) {
        return JDKAspectProxy.wrap(target, interceptor);
    }
}
