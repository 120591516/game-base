/**
 * 创建日期:  2017年08月29日 11:47
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.work;

/**
 * 有返回值的指令
 *
 * @author 杨 强
 */
@FunctionalInterface
public interface CallableCommand<T> extends Command {
    /**
     * 具体的逻辑
     */
    T call();

    @Override
    default void action() {
        call();
    }
}
