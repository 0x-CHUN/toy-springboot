package demo.user;

import springboot.annotation.ioc.Component;
import springboot.annotation.mvc.RequestBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(name = "myUserService")
public class UserService {
    private Integer id = 1;

    private final Map<Integer, User> users = new HashMap<>() {
        {
            put(1, new User("Admin", "It is admin", 18));
        }
    };

    public User get(Integer id) {
        return users.get(id);
    }

    public List<User> create(@RequestBody UserDto userDto) {
        users.put(++id, new User(userDto.getName(), userDto.getDes(), userDto.getAge()));
        return new ArrayList<>(users.values());

    }

    public void say() {
        System.out.println("UserService!");
    }
}
