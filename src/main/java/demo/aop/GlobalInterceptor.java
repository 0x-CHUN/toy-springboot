package demo.aop;

import springboot.core.aop.intercept.Interceptor;
import springboot.core.aop.intercept.MethodInvocation;

public class GlobalInterceptor extends Interceptor {
    @Override
    public Object intercept(MethodInvocation methodInvocation) {
        System.out.println(GlobalInterceptor.class.getSimpleName() + " before method：" + methodInvocation.getTargetMethod().getName());
        Object result = methodInvocation.process();
        System.out.println(GlobalInterceptor.class.getSimpleName() + " after method：" + methodInvocation.getTargetMethod().getName());
        return result;
    }
}
