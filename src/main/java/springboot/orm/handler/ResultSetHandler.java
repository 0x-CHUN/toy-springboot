package springboot.orm.handler;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.mysql.cj.jdbc.Blob;

/**
 * Result map int return type object
 */
public class ResultSetHandler {
    // returned type
    Class<?> typeReturn;
    // the ResultSet need to be mapped
    ResultSet resultSet;
    // has Collection?
    Boolean hasSet;

    public ResultSetHandler(Class<?> typeReturn, ResultSet resultSet) {
        this.resultSet = resultSet;
        this.typeReturn = typeReturn;
    }

    /**
     * Map the result set into a list
     */
    public <T> List<T> handle() throws Exception {
        List<T> res = new ArrayList<>();
        while (resultSet.next()) {
            if (String.class.equals(typeReturn)) {
                String val = resultSet.getString(1);
                if (val != null) {
                    res.add((T) val);
                }
            } else if (Integer.class.equals(typeReturn) || int.class.equals(typeReturn)) {
                Integer val = resultSet.getInt(1);
                if (val != null) {
                    res.add((T) val);
                }
            } else if (Float.class.equals(typeReturn) || float.class.equals(typeReturn)) {
                Float val = resultSet.getFloat(1);
                if (val != null) {
                    res.add((T) val);
                }

            } else if (Double.class.equals(typeReturn) || double.class.equals(typeReturn)) {
                Double val = resultSet.getDouble(1);
                if (val != null) {
                    res.add((T) val);
                }
            } else {
                Object val = generateObjFromResultSet(resultSet, typeReturn);
                if (val != null) {
                    res.add((T) val);
                }
            }
        }

        return res;
    }

    /**
     * Generate the object of specific class from resultSet
     *
     * @param resultSet : the sql ResultSet
     * @param clazz     : the specific class
     * @return the object of sql result
     */
    private Object generateObjFromResultSet(ResultSet resultSet, Class<?> clazz) throws Exception {
        Constructor[] constructors = clazz.getConstructors();
        Constructor usedConstructor = null;
        for (Constructor constructor : constructors) {
            if (constructor.getParameterCount() == 0) { // the object must have no args constructor
                usedConstructor = constructor;
                break;
            }
        }
        if (usedConstructor == null) {
            throw new RuntimeException(typeReturn + " is not empty constructor");
        }
        Object object = usedConstructor.newInstance(); // new instance from no args constructor
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Type type = field.getGenericType();
            if (type.equals(String.class)) {
                String column = resultSet.getString(fieldName);
                field.set(object, column);
            } else if (type.equals(Integer.class)) {
                Integer column = resultSet.getInt(fieldName);
                field.set(object, column);
            } else if (type.equals(Long.class)) {
                Long column = resultSet.getLong(fieldName);
                field.set(object, column);
            } else if (type.equals(Float.class)) {
                Float column = resultSet.getFloat(fieldName);
                field.set(object, column);
            } else if (type.equals(Double.class)) {
                Double column = resultSet.getDouble(fieldName);
                field.set(object, column);
            } else if (type.equals(BigDecimal.class)) {
                BigDecimal column = resultSet.getBigDecimal(fieldName);
                field.set(object, column);
            } else if (type.equals(Blob.class)) {
                Blob column = (Blob) resultSet.getBlob(fieldName);
                field.set(object, column);
            } else if (type.equals(Boolean.class)) {
                Boolean column = resultSet.getBoolean(fieldName);
                field.set(object, column);
            } else if (type.equals(Date.class)) {
                Date column = resultSet.getDate(fieldName);
                field.set(object, column);
            } else if (type.equals(byte[].class)) {
                byte[] column = resultSet.getBytes(fieldName);
                field.set(object, column);
            }
        }
        return object;
    }
}
