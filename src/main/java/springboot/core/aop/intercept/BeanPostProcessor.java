package springboot.core.aop.intercept;

/**
 * Bean post processor
 */
public interface BeanPostProcessor {
    default Object postProcessAfterInit(Object bean) {
        return bean;
    }
}
