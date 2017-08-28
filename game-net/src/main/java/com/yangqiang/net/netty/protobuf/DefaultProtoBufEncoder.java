/**
 * 创建日期:  2017年08月26日 11:33
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.net.netty.protobuf;

import com.google.protobuf.MessageLite;
import com.yangqiang.net.IPool;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * 默认的protobuf编码器
 *
 * @author 杨 强
 */
@Slf4j
public class DefaultProtoBufEncoder extends MessageToByteEncoder<MessageLite> {
    private IPool<MessageLite, MessageLite> outIPool;

    public DefaultProtoBufEncoder(IPool<MessageLite, MessageLite> outIPool) {
        this.outIPool = outIPool;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MessageLite msg, ByteBuf out) throws Exception {
        Integer messageId = outIPool.getId(msg);
        if (messageId == null) {
            log.error("编码到未知的消息id:{}", messageId);
            return;
        }
        byte[] bytes = msg.toByteArray();
        int length = Integer.BYTES + bytes.length;
        boolean writable = out.isWritable(length);
        if (!writable) {
            log.error("消息过大,编码失败:{},{}", messageId, length);
            return;
        }
        out.writeInt(messageId);
        out.writeBytes(bytes);
    }
}
