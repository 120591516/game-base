/**
 * 创建日期:  2017年08月26日 10:24
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.net.netty;

import com.yangqiang.net.MessageListener;
import com.yangqiang.net.SessionListener;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 基于netty的消息处理器
 *
 * @param <T> 消息类型
 * @author 杨 强
 */
@Sharable
public class NettyMessageHandler<T> extends ChannelInboundHandlerAdapter {
    /**
     * 指定消息的监听器
     */
    private MessageListener<T> messageListener;
    /**
     * session状态监听器
     */
    private SessionListener<NettySession> sessionListener;

    public NettyMessageHandler(MessageListener<T> messageListener, SessionListener<NettySession> sessionListener) {
        this.messageListener = messageListener;
        this.sessionListener = sessionListener;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (messageListener == null) {
            return;
        }
        messageListener.onMessage(getSession(ctx), (T) msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (sessionListener == null) {
            return;
        }
        sessionListener.onExceptionCaught(getSession(ctx), cause);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (sessionListener == null) {
            return;
        }
        sessionListener.onConnected(createSession(ctx));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (sessionListener == null) {
            return;
        }
        sessionListener.onDisconnected(getSession(ctx));
    }

    /**
     * 创建基于netty的session
     *
     * @param ctx
     * @return
     */
    private NettySession createSession(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        NettySession session = new NettySession(channel);
        channel.attr(NettySession.NETTY_SESSION).set(session);
        return session;
    }

    /**
     * 获取已创建的session
     *
     * @param ctx
     * @return
     */
    private NettySession getSession(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        NettySession session = channel.attr(NettySession.NETTY_SESSION).get();
        return session;
    }
}
