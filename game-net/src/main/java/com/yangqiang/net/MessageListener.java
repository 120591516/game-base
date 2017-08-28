/**
 * 创建日期:  2017年08月25日 20:29
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.net;

/**
 * 消息监听器
 *
 * @author YangQiang
 */
@FunctionalInterface
public interface MessageListener<T> {
    void onMessage(Session session, T message);
}
