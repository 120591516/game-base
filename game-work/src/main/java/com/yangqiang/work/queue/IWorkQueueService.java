/**
 * 创建日期:  2017年08月29日 12:13
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.work.queue;

import com.yangqiang.work.CallableWork;
import com.yangqiang.work.Work;
import com.yangqiang.work.queue.impl.CallableTask;
import com.yangqiang.work.queue.impl.Task;

import java.util.concurrent.CompletableFuture;

/**
 * 任务执行服务
 *
 * @author 杨 强
 */
public interface IWorkQueueService {
    /**
     * 执行队列中的指令
     */
    void executeCommand();

    /**
     * 提交一个队列任务
     *
     * @param task
     * @param <T>
     * @return
     */
    <T> CompletableFuture<T> submitTask(IQueueTask task);

    /**
     * 提交一个带有返回值的任务
     *
     * @param work 带返回值的任务
     * @return CompletableFuture
     */
    default <T> CompletableFuture<T> submit(CallableWork<T> work) {
        return submitTask(new CallableTask<>(this, work, new CompletableFuture<>()));
    }

    /**
     * 提交一个无返回值的任务
     *
     * @param work 无返回值的任务
     * @return CompletableFuture
     */
    default CompletableFuture<Void> submit(Work work) {
        return submitTask(new Task(this, work, new CompletableFuture<Void>()));
    }
}
