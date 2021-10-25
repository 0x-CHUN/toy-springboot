package demo.aop;


import springboot.annotation.ioc.Autowired;
import springboot.annotation.mvc.GetMapping;
import springboot.annotation.mvc.RestController;

@RestController("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/summary")
    public String getAge() {
        return studentService.getSummary("1");
    }

}
