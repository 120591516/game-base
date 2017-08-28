/**
 * 创建日期:  2017年08月25日 19:45
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.net;

import static com.yangqiang.net.NetworkService.ServiceState.CLOSED;
import static com.yangqiang.net.NetworkService.ServiceState.OPENED;

/**
 * @author YangQiang
 */
public interface NetworkService {
    /**
     * 启动服务
     */
    void open();

    /**
     * 停止服务
     */
    void close();

    /**
     * 获取当前服务的状态
     *
     * @return
     */
    ServiceState getState();

    /**
     * 是否已开启
     *
     * @return
     */
    default boolean isOpened() {
        return getState() == OPENED;
    }

    /**
     * 是否已停止
     *
     * @return
     */
    default boolean isClosed() {
        return getState() == CLOSED;
    }

    enum ServiceState {
        /**
         * 初始状态
         */
        NEW,

        /**
         * 开启状态
         */
        OPENED,

        /**
         * 关闭状态
         */
        CLOSED
    }
}
