package springboot.core.aop.intercept;

import lombok.Getter;
import lombok.Setter;

/**
 * The interceptor
 */
@Getter
@Setter
public abstract class Interceptor {
    private int order = -1;

    public boolean supports(Object bean) {
        return false;
    }

    public abstract Object intercept(MethodInvocation methodinvocation);
}
