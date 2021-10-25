package springboot.core.aop.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import springboot.core.aop.intercept.Interceptor;
import springboot.core.aop.intercept.MethodInvocation;

import java.lang.reflect.Method;

public class CglibAspectProxy implements MethodInterceptor {
    private final Object target;
    private final Interceptor interceptor;

    public CglibAspectProxy(Object target, Interceptor interceptor) {
        this.target = target;
        this.interceptor = interceptor;
    }

    public static Object wrap(Object target, Interceptor interceptor) {
        Class<?> rootClass = target.getClass();
        Class<?> proxySuperClass = rootClass.getSuperclass();
        if (target.getClass().getName().contains("$$")) {
            proxySuperClass = rootClass.getSuperclass();
        }
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(target.getClass().getClassLoader());
        enhancer.setSuperclass(proxySuperClass);
        enhancer.setCallback(new CglibAspectProxy(target, interceptor));
        return enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        MethodInvocation methodInvocation = new MethodInvocation(target, method, args);
        return interceptor.intercept(methodInvocation);
    }
}
