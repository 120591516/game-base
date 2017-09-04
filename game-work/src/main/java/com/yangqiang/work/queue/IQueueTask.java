/**
 * 创建日期:  2017年08月29日 19:01
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.work.queue;

import com.yangqiang.work.Command;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * 基于队列的任务 将具体的任务封装成队列任务执行 保证有序
 *
 * @author 杨 强
 */
public interface IQueueTask extends Command {
    /**
     * 获取当前任务执行的线程池
     *
     * @return
     */
    Executor getExecutor();

    /**
     * 设置当前任务执行的线程池
     *
     * @param executor
     */
    void setExecutor(Executor executor);

    /**
     * 获取当前任务所属的队列
     *
     * @return
     */
    ITaskQueue<IQueueTask> getWorkQueue();

    /**
     * 设置当前任务所属的队列
     *
     * @param workQueue
     */
    void setWorkQueue(ITaskQueue<IQueueTask> workQueue);

    @Override
    default void run() {
        try {
            action();
        } finally {
            ITaskQueue<IQueueTask> workQueue = getWorkQueue();
            synchronized (workQueue) {
                IQueueTask nextTask = workQueue.poll();
                if (nextTask == null) {
                    // 队列中没有任务了设置为执行完毕标识
                    workQueue.setProcessing(false);
                } else {
                    // 如果队列中还有其他命令则继续执行下一个任务
                    getExecutor().execute(nextTask);
                }
            }
        }
    }
}
