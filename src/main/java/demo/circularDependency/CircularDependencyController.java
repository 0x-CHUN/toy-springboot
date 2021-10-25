package demo.circularDependency;

import springboot.annotation.ioc.Autowired;
import springboot.annotation.mvc.GetMapping;
import springboot.annotation.mvc.RestController;

@RestController("/circular-dependency")
public class CircularDependencyController {

    @Autowired
    private CircularDependencyA testA;

    @Autowired
    private CircularDependencyB testB;

    @Autowired
    private CircularDependencyC testC;

    @GetMapping
    public void test() {
        testA.testA();
        testB.testB();
        testC.testC();
    }
}
