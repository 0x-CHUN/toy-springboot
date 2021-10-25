package demo.aop.ascept;

import lombok.extern.slf4j.Slf4j;
import springboot.annotation.aop.*;
import springboot.annotation.ioc.Component;
import springboot.core.aop.lang.JoinPoint;

@Aspect
@Order(value = 0)
@Component
@Slf4j
public class TeacherAspect {

    @Pointcut("com.github.demo.*.*Service*")
    public void perAspect() {
    }

    @Before
    public void beforeAction(JoinPoint joinPoint) {
        log.info("aspect teacher before to do something");
    }

    @After
    public void afterAction(Object result, JoinPoint joinPoint) {
        log.info("aspect teacher after to do something");
    }
}
