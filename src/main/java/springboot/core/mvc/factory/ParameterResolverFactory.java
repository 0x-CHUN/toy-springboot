package springboot.core.mvc.factory;

import springboot.annotation.mvc.PathVariable;
import springboot.annotation.mvc.RequestBody;
import springboot.annotation.mvc.RequestParam;
import springboot.core.mvc.resolver.ParameterResolver;
import springboot.core.mvc.resolver.PathVariableParameterResolver;
import springboot.core.mvc.resolver.RequestBodyParameterResolver;
import springboot.core.mvc.resolver.RequestParamParameterResolver;

import java.lang.reflect.Parameter;

public class ParameterResolverFactory {
    public static ParameterResolver get(Parameter parameter) {
        if (parameter.isAnnotationPresent(RequestParam.class)) {
            return new RequestParamParameterResolver();
        }
        if (parameter.isAnnotationPresent(PathVariable.class)) {
            return new PathVariableParameterResolver();
        }
        if (parameter.isAnnotationPresent(RequestBody.class)) {
            return new RequestBodyParameterResolver();
        }
        return null;
    }
}
