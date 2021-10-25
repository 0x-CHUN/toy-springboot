package demo.circularDependency;

import springboot.annotation.ioc.Autowired;
import springboot.annotation.ioc.Component;

@Component(name = "CircularDependencyBImpl")
public class CircularDependencyBImpl implements CircularDependencyB {

    @Autowired
    private CircularDependencyC testC;

    @Override
    public void testB() {
        System.out.println("B");
    }
}
