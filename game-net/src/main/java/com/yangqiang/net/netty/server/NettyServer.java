/**
 * 创建日期:  2017年08月26日 16:46
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.net.netty.server;

import com.yangqiang.net.AbstaractService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;


/**
 * netty服务器
 *
 * @author 杨 强
 */
@Slf4j
public class NettyServer extends AbstaractService {
    private int port;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap bootstrap;

    NettyServer(final NettyServerBuilder builder) {
        this.port = builder.port();
        this.bossGroup = new NioEventLoopGroup(builder.bossGroupCount());
        this.workerGroup = new NioEventLoopGroup(builder.workGroupCount());

        this.bootstrap = new ServerBootstrap();
        this.bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
        this.bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        this.bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        this.bootstrap.childOption(ChannelOption.SO_RCVBUF, 128 * 1024);
        this.bootstrap.childOption(ChannelOption.SO_SNDBUF, 128 * 1024);
        this.bootstrap.handler(new LoggingHandler(LogLevel.DEBUG));

        this.bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pip = ch.pipeline();
                Map<String, Supplier<ChannelHandler>> handlers = builder.handlers();
                if (handlers == null || handlers.isEmpty()) {
                    return;
                }
                handlers.forEach((name, handlerSupplier) -> pip.addLast(name, handlerSupplier.get()));
            }
        });
    }

    @Override
    public void open() {
        try {
            bootstrap.bind(port).sync();
        } catch (InterruptedException e) {
            throw new RuntimeException("Netty服务器启动失败", e);
        }
        this.state = ServiceState.OPENED;
        log.info("Netty服务器启动成功, 端口号:{}", port);
    }

    @Override
    public void close() {
        this.state = ServiceState.CLOSED;
        Future<?> bossFuture = bossGroup.shutdownGracefully();
        Future<?> workerFuture = workerGroup.shutdownGracefully();
        try {
            bossFuture.await(5000, TimeUnit.MILLISECONDS);
            workerFuture.await(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException("Netty服务器关闭失败", e);
        }
        log.info("Netty服务器已经关闭, 端口号:{}", port);
    }
}
