/**
 * 创建日期:  2017年08月28日 10:50
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.net.netty;

import com.google.protobuf.MessageLite;
import com.yangqiang.net.IPool;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 杨 强
 */
public class ProtobufMessagePool implements IPool<MessageLite, MessageLite> {
    private Map<Integer, MessageLite> messages = new HashMap<>();
    private Map<String, Integer> messageIds = new HashMap<>();

    @Override
    public MessageLite get(int id) {
        return messages.get(id);
    }

    @Override
    public int getId(MessageLite msg) {
        return messageIds.get(msg.getClass().getName());
    }

    @Override
    public void register(int id, MessageLite messageLite) {
        messages.put(id, messageLite);
        messageIds.put(messageLite.getClass().getName(), id);
    }
}
