package springboot.orm.core;

import org.junit.Assert;
import org.junit.Test;

public class SqlSourceTest {
    @Test
    public void inject() {
        String[] expected = new String[]{"id", "name"};
        Integer[] expectedTypes = new Integer[]{0, 1};
        SqlSource sqlSource = new SqlSource("select * from users where id = ${id} and name = #{name}");
        Assert.assertEquals(sqlSource.getSql(), "select * from users where id = ? and name = ?");
        Assert.assertArrayEquals(sqlSource.getParam().toArray(), expected);
        Assert.assertArrayEquals(sqlSource.getInjectTypes().toArray(), expectedTypes);
    }
}