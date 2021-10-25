package demo.circularDependency;

import springboot.annotation.ioc.Autowired;
import springboot.annotation.ioc.Component;

@Component(name = "CircularDependencyCImpl")
public class CircularDependencyCImpl implements CircularDependencyC {

    @Autowired
    private CircularDependencyA testA;

    @Override
    public void testC() {
        System.out.println("C");
    }
}
