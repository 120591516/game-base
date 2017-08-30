/**
 * 创建日期:  2017年08月28日 17:39
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.work.queue.impl;

import com.yangqiang.work.CallableWork;
import com.yangqiang.work.Command;
import com.yangqiang.work.Work;
import com.yangqiang.work.queue.IQueueTask;
import com.yangqiang.work.queue.IWorkQueue;
import com.yangqiang.work.queue.IWorkQueueService;
import com.yangqiang.work.queue.impl.CallableTask;
import com.yangqiang.work.queue.impl.Task;
import com.yangqiang.work.queue.impl.UnlockedWorkQueue;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * 任务队列服务
 *
 * @author 杨 强
 */
@Data
@Slf4j
public class WorkQueueService implements IWorkQueueService {
    private int id;
    private String name;
    private IWorkQueue<Command> workQueue;
    private Executor executor;
    private int maxWorkSize;

    public WorkQueueService(int id, String name, Executor executor) {
        this(id, name, new UnlockedWorkQueue<>(), executor);
    }

    public WorkQueueService(int id, String name, IWorkQueue<Command> workQueue, Executor executor) {
        this.id = id;
        this.name = name;
        this.workQueue = workQueue;
        this.executor = executor;
    }

    /**
     * 执行队列中的命令
     */
    public void executeCommand() {
        synchronized (workQueue) {
            Command command = workQueue.poll();
            if (command == null) {
                // 队列中没有命令了设置为执行完毕标识
                workQueue.setProcessing(false);
                return;
            }
            // 如果队列中还有其他命令则继续执行下一个命令
            workQueue.setProcessing(true);
            getExecutor().execute(command);
        }
    }

    /**
     * 提交一个队列任务
     *
     * @param task 队列任务
     * @param <T> 返回值类型
     * @return CompletableFuture
     */
     public <T> CompletableFuture<T> submitTask(IQueueTask task) {
        synchronized (workQueue) {
            int workSize = workQueue.size();
            if (maxWorkSize > 0 && workSize > maxWorkSize) {
                log.warn("[{}:{}]任务队列服务抛弃指令【{}】!", getId(), getName(), workSize);
                workQueue.clear();
            }

            if (!workQueue.offer(task)) {
                log.error("[{}:{}]任务队列服务添加指令失败:[{}]", getId(), getName(), task.getCommand().getClass().getName());
                return null;
            }

            // 队列如果不在执行中则执行命令
            if (!workQueue.isProcessing()) {
                executeCommand();
            }
            return task.getFuture();
        }
    }
}
