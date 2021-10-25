package springboot.core.mvc.handler;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.Charsets;
import springboot.utils.BeanUtil;
import springboot.utils.UrlUtil;
import springboot.core.ioc.BeanFactory;
import springboot.core.mvc.entity.MethodDetail;
import springboot.core.mvc.factory.FullHttpResponseFactory;
import springboot.core.mvc.factory.ParameterResolverFactory;
import springboot.core.mvc.factory.RouteMethodMapper;
import springboot.core.mvc.resolver.ParameterResolver;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handle the get request
 */
@Slf4j
public class GetRequestHandler implements RequestHandler {
    @Override
    public FullHttpResponse handle(FullHttpRequest request) {
        String requestUri = request.uri();
        Map<String, String> queryParameterMappings = getQueryParams(requestUri);
        // get the path
        String requestPath = UrlUtil.getRequestPath(requestUri);
        // get the target method
        MethodDetail methodDetail = RouteMethodMapper.getMethodDetail(requestPath, HttpMethod.GET);
        methodDetail.setQueryParameterMappings(queryParameterMappings);
        Method targetMethod = methodDetail.getMethod();
        if (targetMethod == null) {
            return null;
        }
        log.info("requestPath -> target method [{}]", targetMethod.getName());
        Parameter[] targetMethodParameters = targetMethod.getParameters();
        // convert the params
        List<Object> targetMethodParams = new ArrayList<>();
        for (Parameter parameter : targetMethodParameters) {
            ParameterResolver parameterResolver = ParameterResolverFactory.get(parameter);
            if (parameterResolver != null) {
                Object param = parameterResolver.resolve(methodDetail, parameter);
                targetMethodParams.add(param);
            }
        }
        String beanName = BeanUtil.getBeanName(methodDetail.getMethod().getDeclaringClass());
        Object targetObj = BeanFactory.BEANS.get(beanName);
        return FullHttpResponseFactory.getSuccessResponse(targetMethod, targetMethodParams, targetObj);
    }

    /**
     * get the parameters of uri
     */
    private Map<String, String> getQueryParams(String uri) {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(uri, Charsets.toCharset(CharEncoding.UTF_8));
        Map<String, List<String>> parameters = queryDecoder.parameters();
        Map<String, String> queryParams = new HashMap<>();
        for (Map.Entry<String, List<String>> attr : parameters.entrySet()) {
            for (String attrVal : attr.getValue()) {
                queryParams.put(attr.getKey(), attrVal);
            }
        }
        return queryParams;
    }
}
