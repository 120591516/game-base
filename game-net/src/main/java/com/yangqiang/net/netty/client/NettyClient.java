/**
 * 创建日期:  2017年08月26日 17:32
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.net.netty.client;

import com.yangqiang.net.AbstaractService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * netty客户端
 *
 * @author 杨 强
 */
@Slf4j
public class NettyClient extends AbstaractService {
    private String host;
    private int port;
    private EventLoopGroup group;
    private Bootstrap bootstrap;

    NettyClient(NettyClientBuilder builder) {
        this.host = builder.host();
        this.port = builder.port();
        this.group = new NioEventLoopGroup(builder.workGroupCount());

        this.bootstrap = new Bootstrap();
        this.bootstrap.group(group).channel(NioSocketChannel.class);
        this.bootstrap.option(ChannelOption.TCP_NODELAY, true);
        this.bootstrap.handler(new LoggingHandler(LogLevel.DEBUG));
        this.bootstrap.handler(new ChannelInitializer<SocketChannel>() {
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
            bootstrap.connect(host, port).sync();
        } catch (InterruptedException e) {
            throw new RuntimeException("连接失败", e);
        }
        this.state = ServiceState.OPENED;
        log.info("连接成功:[{}:{}]", host, port);
    }

    @Override
    public void close() {
        this.state = ServiceState.CLOSED;
        Future<?> future = group.shutdownGracefully();
        try {
            future.await(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException("关闭失败", e);
        }
        log.info("连接已经关闭:[{}:{}]", host, port);
    }
}
