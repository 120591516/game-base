/**
 * 创建日期:  2017年08月28日 10:22
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.net.netty.client;

import com.google.protobuf.MessageLite;
import com.yangqiang.net.IPool;
import com.yangqiang.net.MessageListener;
import com.yangqiang.net.Session;
import com.yangqiang.net.SessionListener;
import com.yangqiang.net.netty.NettyMessageHandler;
import com.yangqiang.net.netty.NettySession;
import com.yangqiang.net.netty.msg.Login;
import com.yangqiang.net.netty.msg.Login.ReqLoginMessage;
import com.yangqiang.net.netty.msg.Login.ResLoginMessage;
import com.yangqiang.net.netty.protobuf.DefaultProtoBufDecoder;
import com.yangqiang.net.netty.protobuf.DefaultProtoBufEncoder;
import com.yangqiang.net.pool.ProtobufMessagePool;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 杨 强
 */
@Slf4j
public class Client implements MessageListener<MessageLite>, SessionListener<NettySession> {
    public static IPool<MessageLite, MessageLite> inIPool = new ProtobufMessagePool();

    static {
        inIPool.register(2, ResLoginMessage.getDefaultInstance());
    }

    public static void main(String[] args) {

        Client client = new Client();

        IPool<MessageLite, MessageLite> outIPool = new ProtobufMessagePool();
        outIPool.register(1, ReqLoginMessage.getDefaultInstance());

        NettyClientBuilder builder = NettyClientBuilder.builder();
        builder.host("127.0.0.1").port(30000).workGroupCount(1);
        builder.handler("frameDecoder", () -> new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4));
        builder.handler("protobufDecoder", () -> new DefaultProtoBufDecoder(inIPool));
        builder.handler("frameEncoder", () -> new LengthFieldPrepender(4));
        builder.handler("protobufEncoder", () -> new DefaultProtoBufEncoder(outIPool));
        NettyMessageHandler messageHandler = new NettyMessageHandler(client, client);
        builder.handler("messageHanlder", () -> messageHandler);

        builder.build().open();
    }

    @Override
    public void onMessage(Session session, MessageLite message) {
        log.info("收到服务器端消息:" + inIPool.getId(message));
        if (message instanceof ResLoginMessage) {
            ResLoginMessage msg = (ResLoginMessage) message;
            log.info("服务器返回:{}", msg.getLoginResult());
        }
    }

    @Override
    public void onConnected(NettySession session) {
        ReqLoginMessage.Builder builder = Login.ReqLoginMessage.newBuilder();
        builder.setAccount(124234).setPassword("password");

        session.write(builder.build());
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
