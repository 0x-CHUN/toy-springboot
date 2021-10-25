package demo.aop.ascept;


import lombok.extern.slf4j.Slf4j;
import springboot.annotation.aop.*;
import springboot.annotation.ioc.Component;
import springboot.core.aop.lang.JoinPoint;


@Aspect
@Order(value = 1)
@Component
@Slf4j
public class HeadMasterAspect {

    @Pointcut("com.github.demo.*.*Service*")
    public void oneAspect() {

    }

    @Pointcut("com.github.demo.*.*Controller*")
    public void twoAspect() {

    }

    @Before
    public void beforeAction(JoinPoint params) {
        log.info("aspect headmaster : before to do something");
    }

    @After
    public void afterAction(Object result, JoinPoint joinPoint) {
        log.info("aspect headmaster after to do something");
    }
}
