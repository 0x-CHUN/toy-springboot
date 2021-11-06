package springboot.orm.core;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import springboot.orm.annotation.Delete;
import springboot.orm.annotation.Insert;
import springboot.orm.annotation.Select;
import springboot.orm.annotation.Update;
import springboot.orm.constant.SqlType;
import springboot.orm.executor.Executor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

public class MapperProxy implements MethodInterceptor {
    // sql executor
    private Executor executor;

    public MapperProxy(Executor executor) {
        this.executor = executor;
    }

    @Override
    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        Object result = null;
        if (isIntercept(method)) {
            // get Method Type
            MethodDetails methodDetails = MapperHelper.getMethodDetails(method);
            assert methodDetails != null;
            Integer methodType = methodDetails.getSqlSource().getExecuteType();
            if (methodType == null) {
                throw new RuntimeException("method is normal sql method");
            }
            //@Select method
            if (methodType.equals(SqlType.SELECT)) {
                List<Object> list = executor.select(method, args);
                result = list;
                if (!methodDetails.isHasSet()) {
                    if (list.size() == 0) {
                        result = null;
                    } else {
                        result = list.get(0);
                    }
                }
            } else {
                // @Update, @Delete, @Insert method
                result = executor.update(method, args);
            }
        } else if (Object.class.equals(method.getDeclaringClass())) {
            result = methodProxy.invokeSuper(object, args);
        }
        return result;
    }

    public Object getProxy(Class<?> cls) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(cls);
        enhancer.setCallback(this);
        return enhancer.create();
    }

    /**
     * Return whether method is annotated with @Select, @Update, @Delete, @Insert
     *
     */
    private boolean isIntercept(Method method) {
        for (Annotation annotation : method.getAnnotations()) {
            if (annotation.annotationType().equals(Select.class) ||
                    annotation.annotationType().equals(Update.class) ||
                    annotation.annotationType().equals(Insert.class) ||
                    annotation.annotationType().equals(Delete.class)) {
                return true;
            }
        }
        return false;
    }
}
