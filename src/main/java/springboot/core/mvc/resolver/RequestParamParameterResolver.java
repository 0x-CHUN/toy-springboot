package springboot.core.mvc.resolver;

import springboot.annotation.mvc.RequestParam;
import springboot.utils.ObjectUtil;
import springboot.core.mvc.entity.MethodDetail;

import java.lang.reflect.Parameter;

/**
 * Process @RequestParam
 */
public class RequestParamParameterResolver implements ParameterResolver {
    @Override
    public Object resolve(MethodDetail methodDetail, Parameter parameter) {
        RequestParam requestParam = parameter.getDeclaredAnnotation(RequestParam.class);
        String requestParameter = requestParam.value();
        String requestParameterValue = methodDetail.getQueryParameterMappings().get(requestParameter);
        if (requestParameterValue == null) {
            if (requestParam.require() && requestParam.defaultValue().isEmpty()) {
                throw new IllegalArgumentException("The specified parameter " + requestParameter + " can not be null!");
            } else {
                requestParameterValue = requestParam.defaultValue();
            }
        }
        return ObjectUtil.convert(parameter.getType(), requestParameterValue);
    }
}
