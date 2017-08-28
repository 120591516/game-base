/**
 * 创建日期:  2017年08月26日 14:47
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.net.netty.protobuf;

import com.google.protobuf.MessageLite;
import com.yangqiang.net.IPool;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 默认的protobuf解码器
 *
 * @author 杨 强
 */
@Slf4j
public class DefaultProtoBufDecoder extends ByteToMessageDecoder {
    private IPool<MessageLite, MessageLite> inIPool;

    public DefaultProtoBufDecoder(IPool<MessageLite, MessageLite> inIPool) {
        this.inIPool = inIPool;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int messageId = in.readInt();
        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);

        MessageLite message = inIPool.get(messageId);
        if (message == null) {
            log.error("解码到未知的消息id:{}", messageId);
            return;
        }
        out.add(message.getParserForType().parseFrom(bytes));
    }
}
