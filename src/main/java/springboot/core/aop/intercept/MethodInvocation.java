package springboot.core.aop.intercept;

import lombok.AllArgsConstructor;
import lombok.Getter;
import springboot.utils.ReflectionUtil;

import java.lang.reflect.Method;

@Getter
@AllArgsConstructor
public class MethodInvocation {
    private final Object targetObject;
    private final Method targetMethod;
    private final Object[] args;

    public Object process() {
        return ReflectionUtil.executeTargetMethod(targetObject, targetMethod, args);
    }
}
