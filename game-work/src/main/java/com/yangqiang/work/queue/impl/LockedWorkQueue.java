/**
 * 创建日期:  2017年08月28日 15:43
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.work.queue.impl;

import java.util.ArrayDeque;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 默认的带锁任务队列
 *
 * @author 杨 强
 */
public class LockedWorkQueue<V> extends WorkQueue<V> {
    private final Lock lock = new ReentrantLock();

    public LockedWorkQueue() {
        super(new ArrayDeque<>());
    }

    public LockedWorkQueue(int size) {
        super(new ArrayDeque<>(size));
    }

    @Override
    public V poll() {
        this.lock.lock();
        try {
            return super.poll();
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public boolean offer(V value) {
        this.lock.lock();
        try {
            return super.offer(value);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void clear() {
        this.lock.lock();
        try {
            super.clear();
        } finally {
            this.lock.unlock();
        }
    }
}
