/**
 * 创建日期:  2017年08月26日 16:50
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.net.netty.server;

import com.yangqiang.net.Builder;
import io.netty.channel.ChannelHandler;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.LinkedHashMap;
import java.util.function.Supplier;

/**
 * netty服务器构建器
 *
 * @author 杨 强
 */
@Accessors(fluent = true)
public class NettyServerBuilder implements Builder<NettyServer> {
    /**
     * 监听端口
     */
    @Getter
    @Setter
    private int port;
    /**
     * 连接线程处理数量
     */
    @Getter
    @Setter
    private int bossGroupCount;
    /**
     * 工作线程数量
     */
    @Getter
    @Setter
    private int workGroupCount;

    @Getter
    private LinkedHashMap<String, Supplier<ChannelHandler>> handlers = new LinkedHashMap<>();

    private NettyServerBuilder() {

    }

    public static NettyServerBuilder Builder() {
        return new NettyServerBuilder();
    }

    public NettyServerBuilder handler(String name, @NonNull Supplier<ChannelHandler> handlerSupplier) {
        this.handlers.put(name, handlerSupplier);
        return this;
    }

    public NettyServerBuilder handlers(@NonNull LinkedHashMap<String, Supplier<ChannelHandler>> handlersSupplier) {
        this.handlers.putAll(handlersSupplier);
        return this;
    }

    public NettyServerBuilder clearHandlers() {
        this.handlers.clear();
        return this;
    }

    @Override
    public NettyServer build() {
        return new NettyServer(this);
    }
}
