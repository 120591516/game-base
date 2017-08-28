/**
 * 创建日期:  2017年08月26日 17:56
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.net.netty.server;

import com.google.protobuf.MessageLite;
import com.yangqiang.net.*;
import com.yangqiang.net.netty.MessageHandlerPool;
import com.yangqiang.net.netty.NettyMessageHandler;
import com.yangqiang.net.netty.NettySession;
import com.yangqiang.net.netty.ProtobufMessagePool;
import com.yangqiang.net.netty.handler.ReqLoginHandler;
import com.yangqiang.net.netty.msg.Login.ReqLoginMessage;
import com.yangqiang.net.netty.msg.Login.ResLoginMessage;
import com.yangqiang.net.netty.protobuf.DefaultProtoBufDecoder;
import com.yangqiang.net.netty.protobuf.DefaultProtoBufEncoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 杨 强
 */
@Slf4j
public class Server implements MessageListener<MessageLite>, SessionListener<NettySession> {
    public static final IPool<MessageLite, MessageLite> inIPool = new ProtobufMessagePool();
    public static final IPool<Class<? extends MessageCommand>, MessageCommand> handlerPool = new MessageHandlerPool();

    static {
        inIPool.register(1, ReqLoginMessage.getDefaultInstance());
        handlerPool.register(1, ReqLoginHandler.class);
    }

    public static void main(String[] args) {
        Server server = new Server();

        IPool<MessageLite, MessageLite> outIPool = new ProtobufMessagePool();
        outIPool.register(2, ResLoginMessage.getDefaultInstance());

        NettyServerBuilder builder = NettyServerBuilder.Builder();
        builder.port(30000).bossGroupCount(1).workGroupCount(4);
        builder.handler("frameDecoder", () -> new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4));
        builder.handler("protobufDecoder", () -> new DefaultProtoBufDecoder(inIPool));
        builder.handler("frameEncoder", () -> new LengthFieldPrepender(4));
        builder.handler("protobufEncoder", () -> new DefaultProtoBufEncoder(outIPool));
        NettyMessageHandler messageHandler = new NettyMessageHandler(server, server);
        builder.handler("messageHanlder", () -> messageHandler);

        builder.build().open();
    }

    @Override
    public void onMessage(Session session, MessageLite message) {
        int messageId = inIPool.getId(message);
        MessageCommand messageCommand = handlerPool.get(messageId);
        messageCommand.setSession(session);
        if (messageCommand != null) {
            messageCommand.handMessage(message);
        }
    }

    @Override
    public void onConnected(NettySession session) {
        log.info("收到连接:{}", session.remoteAddress());
    }

    @Override
    public void onDisconnected(NettySession session) {
        log.info("连接断开:{}", session.remoteAddress());
    }

    @Override
    public void onExceptionCaught(NettySession session, Throwable cause) {
        log.info("发生异常", cause);
    }
}
