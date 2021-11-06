package demo.mapper;

import demo.user.User;
import springboot.annotation.ioc.Autowired;
import springboot.annotation.mvc.GetMapping;
import springboot.annotation.mvc.PathVariable;
import springboot.annotation.mvc.RestController;

@RestController("/mapper")
public class MapperController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TestMapper testMapper;

    @GetMapping("/select/{id}")
    public User mapper(@PathVariable("id") Integer id) {
        return userMapper.selectUser(id);
    }

    @GetMapping("/selectName/{id}")
    public String selectName(@PathVariable("id") Integer id) {
        return userMapper.selectUserName(id);
    }

    @GetMapping("/insert")
    public int insert() {
        return userMapper.insertUser("a", "xx", 2L);
    }

    @GetMapping("/del/{id}")
    public int del(@PathVariable("id") Integer id) {
        return userMapper.delUser(id);
    }

    @GetMapping("/test")
    public User test() {
        return testMapper.selectUser();
    }
}
