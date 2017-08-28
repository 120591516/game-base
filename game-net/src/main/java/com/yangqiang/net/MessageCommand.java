/**
 * 创建日期:  2017年08月26日 11:08
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.net;

/**
 * 消息命令
 *
 * @author 杨 强
 */
public interface MessageCommand<T> {
    <T extends Session> void setSession(T session);

    <T extends Session> T getSession();

    void handMessage(T msg);
}
