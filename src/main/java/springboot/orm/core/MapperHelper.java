package springboot.orm.core;

import lombok.extern.slf4j.Slf4j;
import springboot.orm.annotation.*;
import springboot.orm.constant.SqlType;
import springboot.orm.executor.ExecutorFactory;
import springboot.core.ioc.BeanFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MapperHelper {
    // singleton
    private static MapperHelper mapperHelper = null;
    // cached MethodDetails
    private static Map<Method, MethodDetails> cachedMethodDetails = new ConcurrentHashMap<>();
    // all mapper class
    private static Set<Class<?>> mapperClasses = null;

    private MapperHelper() {
    }

    public static MapperHelper getInstance() {
        synchronized (MapperHelper.class) {
            if (mapperHelper == null) {
                synchronized (MapperHelper.class) {
                    mapperHelper = new MapperHelper();
                }
            }
        }
        return mapperHelper;
    }

    public static MethodDetails getMethodDetails(Method method) {
        if (cachedMethodDetails == null || cachedMethodDetails.isEmpty() || !cachedMethodDetails.containsKey(method)) {
            return null;
        }
        return cachedMethodDetails.get(method);
    }

    public void init(Class<?> clazz) {
        if (clazz.isInterface()) {//mapper is an interface
            Method[] methods = clazz.getDeclaredMethods(); // get all methods
            for (Method method : methods) {// generate all sql from the methods
                MethodDetails methodDetails = handleParameter(method); // generate method details from method
                methodDetails.setSqlSource(handleAnnotation(method));
                cachedMethodDetails.put(method, methodDetails); // cached
            }
            // generate proxy class
            MapperProxy mapperProxy = new MapperProxy(ExecutorFactory.getExecutor());
            Object mapperBean = mapperProxy.getProxy(clazz);
            // add to Beans
            BeanFactory.BEANS.put(clazz.getName(), mapperBean);
            log.info("Add " + clazz.getName() + " Mapper");
        }
    }

    /**
     * Generate sql source from method
     *
     * @param method : the @Select,@Insert,@Delete,@Update method
     */
    private SqlSource handleAnnotation(Method method) {
        SqlSource sqlSource = null;
        String sql;
        Annotation[] annotations = method.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof Select) {
                Select selectAnnotation = (Select) annotation;
                sql = selectAnnotation.value();
                sqlSource = new SqlSource(sql);
                sqlSource.setExecuteType(SqlType.SELECT);
                break;
            } else if (annotation instanceof Update) {
                Update updateAnnotation = (Update) annotation;
                sql = updateAnnotation.value();
                sqlSource = new SqlSource(sql);
                sqlSource.setExecuteType(SqlType.UPDATE);
                break;
            } else if (annotation instanceof Delete) {
                Delete deleteAnnotation = (Delete) annotation;
                sql = deleteAnnotation.value();
                sqlSource = new SqlSource(sql);
                sqlSource.setExecuteType(SqlType.DELETE);
                break;
            } else if (annotation instanceof Insert) {
                Insert insertAnnotation = (Insert) annotation;
                sql = insertAnnotation.value();
                sqlSource = new SqlSource(sql);
                sqlSource.setExecuteType(SqlType.INSERT);
                break;
            }
        }
        if (sqlSource == null) {
            throw new RuntimeException("method annotation not null");
        }
        return sqlSource;
    }

    /**
     * Get the param from method annotation
     *
     * @param method: the @Select,@Insert,@Delete,@Update method
     */
    private MethodDetails handleParameter(Method method) {
        MethodDetails methodDetails = new MethodDetails();
        int paramCount = method.getParameterCount();
        Class<?>[] paramTypes = method.getParameterTypes(); // parameters' type
        List<String> paramNames = new ArrayList<>();// parameters' name
        Parameter[] params = method.getParameters();
        for (Parameter parameter : params) { // @Param("id") Integer id -> id
            paramNames.add(parameter.getName());
        }
        for (int i = 0; i < paramCount; i++) { // @Param("id") Integer id -> "id"
            paramNames.set(i, getParamNameFromAnnotation(method, i, paramNames.get(i)));
        }
        methodDetails.setParamTypes(paramTypes);
        methodDetails.setParamNames(paramNames);
        Type returnType = method.getGenericReturnType();
        Class<?> returnClass = method.getReturnType();
        if (returnType instanceof ParameterizedType) {
            //ParameterizedType represents a parameterized type such as Collection<String>.
            if (!List.class.equals(returnClass)) {
                throw new RuntimeException("now ibatis only support list");
            }
            Type type = ((ParameterizedType) returnType).getActualTypeArguments()[0];
            methodDetails.setReturnType((Class<?>) type);
            methodDetails.setHasSet(true);
        } else {
            methodDetails.setReturnType(returnClass);
            methodDetails.setHasSet(false);
        }
        return methodDetails;
    }

    /**
     * Get the @Param annotation parameter
     *
     * @param method    : the @Select,@Insert,@Delete,@Update method
     * @param idx       : index of param
     * @param paramName : the param name
     * @return : @Param("id") Integer id -> "id"
     */
    private static String getParamNameFromAnnotation(Method method, int idx, String paramName) {
        final Object[] paramAnnos = method.getParameterAnnotations()[idx];
        for (Object paramAnno : paramAnnos) {
            if (paramAnno instanceof Param) {
                paramName = ((Param) paramAnno).value();
            }
        }
        return paramName;
    }
}
