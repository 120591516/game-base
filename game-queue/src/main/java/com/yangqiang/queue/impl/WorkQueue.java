/**
 * 创建日期:  2017年08月28日 15:40
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.queue.impl;

import com.yangqiang.queue.IWorkQueue;

import java.util.Queue;

/**
 * 任务队列
 *
 * @author 杨 强
 */
public class WorkQueue<V> implements IWorkQueue<V> {
    private final Queue<V> queue;
    private volatile boolean processing = false;

    public WorkQueue(Queue<V> queue) {
        this.queue = queue;
    }

    public V poll() {
        return this.queue.poll();
    }

    public boolean offer(V value) {
        return this.queue.offer(value);
    }

    public void clear() {
        this.queue.clear();
    }

    public int size() {
        return this.queue.size();
    }

    public boolean isProcessing() {
        return this.processing;
    }

    public void setProcessing(boolean processing) {
        this.processing = processing;
    }
}
