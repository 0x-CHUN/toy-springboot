package springboot.core.ioc;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Auto inject the dependency
 */
@Slf4j
public class DependencyInjection {

    /**
     * Traverse the properties of all beans in the ioc container,
     * and inject instances for all properties annotated with @Autowired/@Value
     *
     * @param packageNames the base package path
     */
    public static void inject(String[] packageNames) {
        AutowiredBeanInitialization autowiredBeanInitialization = new AutowiredBeanInitialization(packageNames);
        Map<String, Object> beans = BeanFactory.BEANS;
        if (beans.size() > 0) {
            BeanFactory.BEANS.values().forEach(autowiredBeanInitialization::initialize);
        }
    }
}
