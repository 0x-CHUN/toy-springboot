package springboot.core.mvc.resolver;

import springboot.annotation.mvc.PathVariable;
import springboot.utils.ObjectUtil;
import springboot.core.mvc.entity.MethodDetail;

import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * Process @PathVariable
 */
public class PathVariableParameterResolver implements ParameterResolver {
    @Override
    public Object resolve(MethodDetail methodDetail, Parameter parameter) {
        PathVariable pathVariable = parameter.getDeclaredAnnotation(PathVariable.class);
        String requestParameter = pathVariable.value();
        Map<String, String> urlParameterMappings = methodDetail.getUrlParameterMappings();
        String requestParameterValue = urlParameterMappings.get(requestParameter);
        return ObjectUtil.convert(parameter.getType(), requestParameterValue);
    }
}
