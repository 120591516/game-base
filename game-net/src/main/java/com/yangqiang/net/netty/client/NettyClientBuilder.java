/**
 * 创建日期:  2017年08月26日 17:31
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.net.netty.client;

import com.yangqiang.net.Builder;
import io.netty.channel.ChannelHandler;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.LinkedHashMap;
import java.util.function.Supplier;

/**
 * @author 杨 强
 */
@Accessors(fluent = true)
public class NettyClientBuilder implements Builder<NettyClient> {
    @Getter
    @Setter
    private String host;
    @Getter
    @Setter
    private int port;
    @Getter
    @Setter
    private int workGroupCount;
    @Getter
    private LinkedHashMap<String, Supplier<ChannelHandler>> handlers = new LinkedHashMap<>();

    private NettyClientBuilder() {
    }

    public static NettyClientBuilder builder() {
        return new NettyClientBuilder();
    }

    public NettyClientBuilder handler(String name, @NonNull Supplier<ChannelHandler> handlerSupplier) {
        this.handlers.put(name, handlerSupplier);
        return this;
    }

    public NettyClientBuilder handlers(@NonNull LinkedHashMap<String, Supplier<ChannelHandler>> handlersSupplier) {
        this.handlers.putAll(handlersSupplier);
        return this;
    }

    public NettyClientBuilder clearHandlers() {
        this.handlers.clear();
        return this;
    }

    @Override
    public NettyClient build() {
        return new NettyClient(this);
    }
}
