/**
 * 创建日期:  2017年08月28日 11:02
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.net.pool;

import com.yangqiang.net.IPool;
import com.yangqiang.net.MessageCommand;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 杨 强
 */
public class MessageHandlerPool implements IPool<Class<? extends MessageCommand>, MessageCommand> {
    private Map<Integer, Class<? extends MessageCommand>> handlers = new HashMap<>();
    private Map<String, Integer> handlerIds = new HashMap<>();

    @Override
    public MessageCommand get(int id) {
        Class<? extends MessageCommand> clz = handlers.get(id);
        if (clz == null) {
            return null;
        }
        try {
            return clz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getId(Class<? extends MessageCommand> messageHandlerClass) {
        return handlerIds.get(messageHandlerClass.getName());
    }

    @Override
    public void register(int id, Class<? extends MessageCommand> messageHandlerClass) {
        handlers.put(id, messageHandlerClass);
    }
}
