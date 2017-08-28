/**
 * 创建日期:  2017年08月26日 11:16
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.net;

import lombok.NonNull;

/**
 * 数据池接口
 *
 * @author 杨 强
 */
public interface IPool<T, R> {
    /**
     * 根据id获取消息
     *
     * @param id
     * @return
     */
    R get(int id);

    /**
     * 根据t获取id
     *
     * @param t
     * @return
     */
    int getId(@NonNull T t);

    /**
     * 注册
     *
     * @param id
     * @param t
     */
    void register(int id, @NonNull T t);
}
