/**
 * 创建日期:  2017年08月29日 19:08
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.queue;

/**
 * @author 杨 强
 */
@FunctionalInterface
public interface Command extends Runnable {
    void action();

    @Override
    default void run() {
        action();
    }
}
