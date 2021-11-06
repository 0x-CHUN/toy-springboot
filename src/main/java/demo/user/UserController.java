package demo.user;

import springboot.annotation.ioc.Autowired;
import springboot.annotation.mvc.*;

@RestController("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public User get(@PathVariable("id") Integer id) {
        return userService.get(id);
    }

    @PostMapping
    public void create(@RequestBody UserDto userDto) {
        userService.create(userDto);
    }
}
