package springboot.core.mvc.factory;

import io.netty.handler.codec.http.HttpMethod;
import springboot.core.mvc.handler.GetRequestHandler;
import springboot.core.mvc.handler.PostRequestHandler;
import springboot.core.mvc.handler.RequestHandler;

import java.util.HashMap;
import java.util.Map;

public class RequestHandlerFactory {
    public static final Map<HttpMethod, RequestHandler> HANDLER_MAP = new HashMap<>();

    static {
        HANDLER_MAP.put(HttpMethod.GET, new GetRequestHandler());
        HANDLER_MAP.put(HttpMethod.POST, new PostRequestHandler());
    }

    public static RequestHandler get(HttpMethod httpMethod) {
        return HANDLER_MAP.get(httpMethod);
    }
}
