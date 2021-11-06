package demo.mapper;

import demo.user.User;
import springboot.orm.annotation.*;

@Mapper
public interface UserMapper {
    @Select("select * from user where id=#{id}")
    User selectUser(@Param("id") Integer id);

    @Insert("insert into user(username,password,timestamp) values(#{username},#{password},#{timestamp})")
    int insertUser(@Param("username") String username, @Param("password") String password, @Param("timestamp") Long timestamp);

    @Select("select * from user where username=#{username}")
    User selectUser(@Param("username") String username);

    @Select("select username from user where id=#{id}")
    String selectUserName(@Param("id") Integer id);

    @Delete("delete from user where id=#{id}")
    int delUser(@Param("id") Integer id);
}
