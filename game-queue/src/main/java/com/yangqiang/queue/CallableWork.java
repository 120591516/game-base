/**
 * 创建日期:  2017年08月29日 11:47
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.queue;

/**
 * 有返回值的任务
 *
 * @author 杨 强
 */
@FunctionalInterface
public interface CallableWork<T> extends Command {
    /**
     * 具体的任务
     */
    T call();

    @Override
    default void action() {
        call();
    }
}
