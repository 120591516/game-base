/**
 * 创建日期:  2017年08月26日 9:18
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.net;

/**
 * session监听器
 *
 * @author 杨 强
 */
public interface SessionListener<T extends Session> {
    /**
     * 连接建立
     *
     * @param session
     */
    void onConnected(T session);

    /**
     * 连接断开
     *
     * @param session
     */
    void onDisconnected(T session);

    /**
     * 异常发生
     *
     * @param session
     * @param cause
     */
    void onExceptionCaught(T session, Throwable cause);
}
