/**
 * 创建日期:  2017年08月28日 15:47
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.work.queue.impl;

import java.util.ArrayDeque;

/**
 * 默认的无锁队列
 *
 * @author 杨 强
 */
public class UnlockedTaskQueue<V> extends TaskQueue<V> {
    public UnlockedTaskQueue() {
        super(new ArrayDeque<>());
    }

    public UnlockedTaskQueue(int size) {
        super(new ArrayDeque<>(size));
    }
}
