package demo.mapper;

import demo.user.User;
import springboot.orm.annotation.Mapper;
import springboot.orm.annotation.Select;

@Mapper
public interface TestMapper {
    @Select("select * from user where id=1")
    User selectUser();
}