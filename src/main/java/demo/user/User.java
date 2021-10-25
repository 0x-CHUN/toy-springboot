package demo.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    private String name;
    private String des;
    private Integer age;
}
