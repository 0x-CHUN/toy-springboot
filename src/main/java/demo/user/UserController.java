package demo.user;

import springboot.annotation.ioc.Autowired;
import springboot.annotation.mvc.*;

@RestController("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public User get(@RequestParam(value = "name", require = true, defaultValue = "default name") String name,
                    @RequestParam("des") String des,
                    @RequestParam("age") Integer age) {
        return new User(name, des, age);
    }

    @GetMapping("/{id}")
    public User get(@PathVariable("id") Integer id) {
        return userService.get(id);
    }

    @PostMapping
    public void create(@RequestBody UserDto userDto) {
        userService.create(userDto);
    }
}
