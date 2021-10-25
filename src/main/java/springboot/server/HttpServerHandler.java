package springboot.server;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.AsciiString;
import lombok.extern.slf4j.Slf4j;
import springboot.utils.UrlUtil;
import springboot.core.mvc.factory.FullHttpResponseFactory;
import springboot.core.mvc.factory.RequestHandlerFactory;
import springboot.core.mvc.handler.RequestHandler;

@Slf4j
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final String FAVICON_ICO = "/favicon.ico";
    private static final AsciiString CONNECTION = AsciiString.cached("Connection");
    private static final AsciiString KEEP_ALIVE = AsciiString.cached("keep-alive");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        log.info("Handler http request:{}", request);
        String uri = request.uri();
        if (uri.equals(FAVICON_ICO)) {
            return;
        }
        RequestHandler requestHandler = RequestHandlerFactory.get(request.method());
        FullHttpResponse response;
        try {
            response = requestHandler.handle(request);
        } catch (Throwable e) {
            log.error("Caught an unexpected error.", e);
            String requestPath = UrlUtil.getRequestPath(request.uri());
            response = FullHttpResponseFactory.getErrorResponse(requestPath, e.toString(),
                    HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
        boolean keepAlive = HttpUtil.isKeepAlive(request);
        if (!keepAlive) {
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            response.headers().set(CONNECTION, KEEP_ALIVE);
            ctx.write(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
}
