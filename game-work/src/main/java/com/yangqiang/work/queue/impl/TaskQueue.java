/**
 * 创建日期:  2017年08月28日 15:40
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.work.queue.impl;

import com.yangqiang.work.queue.ITaskQueue;

import java.util.Queue;

/**
 * 任务队列
 *
 * @author 杨 强
 */
public class TaskQueue<V> implements ITaskQueue<V> {
    private final Queue<V> queue;
    private volatile boolean processing = false;

    public TaskQueue(Queue<V> queue) {
        this.queue = queue;
    }

    @Override
    public V poll() {
        return this.queue.poll();
    }

    @Override
    public boolean offer(V value) {
        return this.queue.offer(value);
    }

    @Override
    public void clear() {
        this.queue.clear();
    }

    @Override
    public int size() {
        return this.queue.size();
    }

    @Override
    public boolean isProcessing() {
        return this.processing;
    }

    @Override
    public void setProcessing(boolean processing) {
        this.processing = processing;
    }
}
