package demo;

import springboot.annotation.boot.ComponentScan;
import springboot.annotation.boot.SpringbootApplication;
import springboot.core.ApplicationContext;

@SpringbootApplication
public class DemoApp {
    public static void main(String[] args) {
        DemoApp.run(DemoApp.class, args);
    }

    public static void run(Class<?> applicationClass, String... args) {
        ApplicationContext applicationContext = ApplicationContext.getApplicationContext();
        applicationContext.run(applicationClass);
    }
}
