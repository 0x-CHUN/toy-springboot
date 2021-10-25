package springboot.core.mvc.factory;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AsciiString;
import springboot.utils.ReflectionUtil;
import springboot.exception.ErrorResponse;
import springboot.serialize.JacksonSerializer;

import java.lang.reflect.Method;
import java.util.List;

public class FullHttpResponseFactory {
    private static final AsciiString CONTENT_TYPE = AsciiString.cached("Content-Type");
    private static final AsciiString CONTENT_LENGTH = AsciiString.cached("Content-Length");
    private static final JacksonSerializer JSON_SERIALIZER = new JacksonSerializer();

    public static FullHttpResponse getSuccessResponse(Method targetMethod, List<Object> targetMethodParams, Object targetObject) {
        //the return type of targetMethod is void
        if (targetMethod.getReturnType() == void.class) {
            ReflectionUtil.executeTargetMethodNoResult(targetObject, targetMethod, targetMethodParams.toArray());
            return buildSuccessResponse();
        } else {
            Object result = ReflectionUtil.executeTargetMethod(targetObject, targetMethod, targetMethodParams.toArray());
            return buildSuccessResponse(result);
        }
    }

    public static FullHttpResponse getErrorResponse(String url, String message, HttpResponseStatus status) {
        ErrorResponse errorResponse = new ErrorResponse(status.code(), status.reasonPhrase(), message, url);
        byte[] content = JSON_SERIALIZER.serialize(errorResponse);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.wrappedBuffer(content));
        response.headers().set(CONTENT_TYPE, "application/json");
        response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
        return response;
    }


    private static FullHttpResponse buildSuccessResponse(Object o) {
        byte[] content = JSON_SERIALIZER.serialize(o);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(content));
        response.headers().set(CONTENT_TYPE, "application/json");
        response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
        return response;
    }

    private static FullHttpResponse buildSuccessResponse() {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set(CONTENT_TYPE, "application/json");
        response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
        return response;
    }

}
