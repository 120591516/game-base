/**
 * 工 程 名:  game-core
 * 创建日期:  2017年04月27日 14:15
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.net;

import java.net.SocketAddress;

/**
 * 会话连接
 *
 * @author YangQiang
 */
public interface Session {
    /**
     * 写入一个消息
     *
     * @param msg
     */
    void write(Object msg);

    /**
     * 关闭连接
     */
    void close();

    /**
     * 存入一个属性
     *
     * @param key
     * @param value
     */
    void putAttribute(String key, Object value);

    /**
     * 获取一个属性
     *
     * @param key
     * @return
     */
    Object getAttribute(String key);

    /**
     * 获取远程端的地址
     *
     * @return
     */
    SocketAddress remoteAddress();

    /**
     * 获取本地端的地址
     *
     * @return
     */
    SocketAddress localAddress();
}
