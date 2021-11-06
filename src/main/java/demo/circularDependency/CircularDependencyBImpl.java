package demo.circularDependency;

import springboot.annotation.ioc.Autowired;
import springboot.annotation.ioc.Component;

@Component
public class CircularDependencyBImpl implements CircularDependencyB {

    @Autowired
    private CircularDependencyC testC;

    @Override
    public void testB() {
        System.out.println("B");
    }
}
