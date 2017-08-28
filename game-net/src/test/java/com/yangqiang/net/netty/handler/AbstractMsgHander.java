/**
 * 创建日期:  2017年08月28日 11:31
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.net.netty.handler;

import com.yangqiang.net.MessageCommand;
import com.yangqiang.net.Session;

/**
 * @author 杨 强
 */
public abstract class AbstractMsgHander<T> implements MessageCommand<T> {
    protected Session session;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
