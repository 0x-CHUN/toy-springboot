package springboot.core.aop.intercept;

import springboot.annotation.aop.After;
import springboot.annotation.aop.Before;
import springboot.annotation.aop.Pointcut;
import springboot.utils.PatternMatchUtils;
import springboot.utils.ReflectionUtil;
import springboot.core.aop.lang.JoinPoint;
import springboot.core.aop.lang.JoinPointImpl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class InternallyAspectInterceptor extends Interceptor {
    private final Object adviceBean;
    private final HashSet<String> expressionUrls = new HashSet<>();
    private final List<Method> beforeMethods = new ArrayList<>();
    private final List<Method> afterMethods = new ArrayList<>();

    public InternallyAspectInterceptor(Object adviceBean) {
        this.adviceBean = adviceBean;
        init();
    }

    private void init() {
        for (Method method : adviceBean.getClass().getDeclaredMethods()) {
            Pointcut pointcut = method.getAnnotation(Pointcut.class);
            if (!Objects.isNull(pointcut)) {
                expressionUrls.add(pointcut.value());
            }
            Before before = method.getAnnotation(Before.class);
            if (!Objects.isNull(before)) {
                beforeMethods.add(method);
            }
            After after = method.getAnnotation(After.class);
            if (!Objects.isNull(after)) {
                afterMethods.add(method);
            }
        }
    }

    @Override
    public boolean supports(Object bean) {
        return expressionUrls.stream().anyMatch(url -> PatternMatchUtils.simpleMatch(url, bean.getClass().getName()))
                && (!beforeMethods.isEmpty() || !afterMethods.isEmpty());
    }

    @Override
    public Object intercept(MethodInvocation methodinvocation) {
        JoinPoint joinPoint = new JoinPointImpl(adviceBean, methodinvocation.getTargetObject(), methodinvocation.getArgs());
        beforeMethods.forEach(method -> ReflectionUtil.executeTargetMethodNoResult(adviceBean, method, joinPoint));
        Object result = methodinvocation.process();
        afterMethods.forEach(method -> ReflectionUtil.executeTargetMethodNoResult(adviceBean, method, result, joinPoint));
        return result;
    }
}
