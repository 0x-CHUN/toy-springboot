package demo.user;

import springboot.annotation.ioc.Component;
import springboot.annotation.mvc.RequestBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserService {
    private Integer id = 1;

    private final Map<Integer, User> users = new HashMap<>() {
        {
            put(1, new User(1, "Admin", "admin", 1L));
            put(2, new User(2, "CHUN", "CHUN", 2L));
        }
    };

    public User get(Integer id) {
        return users.get(id);
    }

    public List<User> create(@RequestBody UserDto userDto) {
        return new ArrayList<>(users.values());
    }

    public void say() {
        System.out.println("UserService!");
    }
}
