/**
 * 创建日期:  2017年08月26日 16:47
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.net;

/**
 * @author 杨 强
 */
public abstract class AbstaractService implements NetworkService {
    protected ServiceState state = ServiceState.NEW;

    @Override
    public ServiceState getState() {
        return state;
    }
}
