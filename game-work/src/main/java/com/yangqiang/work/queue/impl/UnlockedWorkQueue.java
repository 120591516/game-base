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
public class UnlockedWorkQueue<V> extends WorkQueue<V> {
    public UnlockedWorkQueue() {
        super(new ArrayDeque<>());
    }

    public UnlockedWorkQueue(int size) {
        super(new ArrayDeque<>(size));
    }
}
