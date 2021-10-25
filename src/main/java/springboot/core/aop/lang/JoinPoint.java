package springboot.core.aop.lang;

public interface JoinPoint {
    /**
     * Get the point
     *
     */
    Object getAdviceBean();


    /**
     * Get the target object
     *
     */
    Object getTarget();

    /**
     * Get the parameters for object
     *
     */
    Object[] getArgs();
}
