/**
 * 创建日期:  2017年08月26日 9:26
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.net.netty;

import com.yangqiang.net.Session;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.Data;

import java.net.SocketAddress;

/**
 * 基于netty封装的session
 *
 * @author 杨 强
 */
@Data
public class NettySession implements Session {
    public static final AttributeKey<NettySession> NETTY_SESSION = AttributeKey.valueOf("NETTY_SESSION");

    private Channel channel;

    public NettySession(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void write(Object msg) {
        channel.writeAndFlush(msg);
    }

    @Override
    public void close() {
        channel.close();
    }

    @Override
    public void putAttribute(String key, Object value) {
        channel.attr(AttributeKey.valueOf(key)).set(value);
    }

    @Override
    public Object getAttribute(String key) {
        return channel.attr(AttributeKey.valueOf(key)).get();
    }

    @Override
    public SocketAddress remoteAddress() {
        return channel.remoteAddress();
    }

    @Override
    public SocketAddress localAddress() {
        return channel.localAddress();
    }
}
