package springboot.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Server for http
 */
@Slf4j
public class HttpServer {
    private static final int PORT = 8080;

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY, true) // user Nagle
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // use keep alive
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline().addLast("decoder", new HttpRequestDecoder())
                                    .addLast("encoder", new HttpResponseEncoder())
                                    .addLast("aggregator", new HttpObjectAggregator(512 * 1024))
                                    .addLast("handler", new HttpServerHandler());
                        }
                    });
            Channel channel = b.bind(PORT).sync().channel();
            log.info("Server @ localhost:" + PORT);
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("Error occur when start application : ", e);
        } finally {
            log.error("Shutdown");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
