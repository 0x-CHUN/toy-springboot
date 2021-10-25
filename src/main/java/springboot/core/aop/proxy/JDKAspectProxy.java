package springboot.core.aop.proxy;

import springboot.core.aop.intercept.Interceptor;
import springboot.core.aop.intercept.MethodInvocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public final class JDKAspectProxy implements InvocationHandler {

    private final Object target;
    private final Interceptor interceptor;

    private JDKAspectProxy(Object target, Interceptor interceptor) {
        this.target = target;
        this.interceptor = interceptor;
    }

    public static Object wrap(Object target, Interceptor interceptor) {
        JDKAspectProxy jdkAspectProxy = new JDKAspectProxy(target, interceptor);
        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(), jdkAspectProxy);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        MethodInvocation methodInvocation = new MethodInvocation(target, method, args);
        return interceptor.intercept(methodInvocation);
    }

}

