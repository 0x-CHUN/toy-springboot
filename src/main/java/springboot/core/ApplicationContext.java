package springboot.core;

import springboot.annotation.boot.ComponentScan;
import springboot.core.aop.factory.InterceptorFactory;
import springboot.core.boot.ApplicationRunner;
import springboot.core.config.Configuration;
import springboot.core.config.ConfigurationFactory;
import springboot.core.config.ConfigurationManager;
import springboot.core.ioc.BeanFactory;
import springboot.core.ioc.DependencyInjection;
import springboot.core.mvc.factory.RouteMethodMapper;
import springboot.factory.ClassFactory;
import springboot.server.HttpServer;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

public class ApplicationContext {
    private static final ApplicationContext APPLICATION_CONTEXT = new ApplicationContext();

    public void run(Class<?> application) {
        String[] packageNames = getPackageNames(application);
        // load configuration first!
        loadResources(application);
        // load class
        ClassFactory.loadClass(packageNames);
        //load route
        RouteMethodMapper.loadRoutes();
        // load beans
        BeanFactory.loadBeans();
        // load interceptor
        InterceptorFactory.loadInterceptors(packageNames);
        // inject the bean
        DependencyInjection.inject(packageNames);
        // apply post processor
        BeanFactory.applyBeanPostProcessors();

        callRunner();
    }

    private void callRunner() {
        List<ApplicationRunner> runners = new ArrayList<>(BeanFactory.getBeanNamesOfType(ApplicationRunner.class).values());
        runners.add(() -> {
            HttpServer server = new HttpServer();
            server.start();
        });
        for (ApplicationRunner runner : new LinkedHashSet<>(runners)) {
            runner.run();
        }
    }

    public static ApplicationContext getApplicationContext() {
        return APPLICATION_CONTEXT;
    }

    private void loadResources(Class<?> application) {
        ClassLoader classLoader = application.getClassLoader();
        List<Path> filePaths = new ArrayList<>();
        for (String configName : Configuration.DEFAULT_CONFIG_NAMES) {
            URL url = classLoader.getResource(configName);
            if (!Objects.isNull(url)) {
                try {
                    filePaths.add(Paths.get(url.toURI()));
                } catch (URISyntaxException ignored) {
                }
            }
        }
        ConfigurationManager manager = new ConfigurationManager(ConfigurationFactory.getConfig());
        // add configuration manager into BEANS
        BeanFactory.BEANS.put(ConfigurationManager.class.getName(), manager);
        manager.loadResources(filePaths);
    }

    private static String[] getPackageNames(Class<?> application) {
        ComponentScan componentScan = application.getAnnotation(ComponentScan.class);
        return !Objects.isNull(componentScan) ? componentScan.value()
                : new String[]{application.getPackage().getName()};
    }
}
