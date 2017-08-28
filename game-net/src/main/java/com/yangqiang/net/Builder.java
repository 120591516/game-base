/**
 * 创建日期:  2017年08月26日 17:01
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.net;

import java.util.function.Supplier;

/**
 * 构建器
 *
 * @author 杨 强
 */
public interface Builder<T> extends Supplier<T> {
    T build();

    @Override
    default T get() {
        return build();
    }
}
