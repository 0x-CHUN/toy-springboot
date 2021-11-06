package demo.circularDependency;

import springboot.annotation.ioc.Autowired;
import springboot.annotation.ioc.Component;

@Component
public class CircularDependencyAImpl implements CircularDependencyA {

    @Autowired
    private CircularDependencyB testB;

    @Override
    public void testA() {
        System.out.println("A");
    }
}
