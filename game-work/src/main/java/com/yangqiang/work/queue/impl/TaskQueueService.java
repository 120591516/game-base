/**
 * 创建日期:  2017年08月28日 17:39
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.work.queue.impl;

import com.yangqiang.game.IndexedThreadFactory;
import com.yangqiang.work.CallableCommand;
import com.yangqiang.work.Command;
import com.yangqiang.work.queue.IQueueTask;
import com.yangqiang.work.queue.ITaskQueue;
import com.yangqiang.work.queue.ITaskQueueService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 任务队列服务
 *
 * @author 杨 强
 */
@Data
@Slf4j
public class TaskQueueService implements ITaskQueueService {
    private int id;
    private String name;
    private ITaskQueue<IQueueTask> workQueue;
    private Executor executor;
    private int maxWorkSize;

    public TaskQueueService(int id, String name, Executor executor) {
        this(id, name, new UnlockedTaskQueue<>(), executor);
    }

    public TaskQueueService(int id, String name, ITaskQueue<IQueueTask> workQueue, Executor executor) {
        this(id, name, workQueue, executor, 0);
    }

    public TaskQueueService(int id, String name, ITaskQueue<IQueueTask> workQueue, Executor executor, int maxWorkSize) {
        this.id = id;
        this.name = name;
        this.workQueue = workQueue;
        this.executor = executor;
        this.maxWorkSize = maxWorkSize;
    }

    @Override
    public boolean submit(IQueueTask task) {
        task.setExecutor(executor);
        task.setWorkQueue(workQueue);
        synchronized (workQueue) {
            int workSize = workQueue.size();
            if (maxWorkSize > 0 && workSize > maxWorkSize) {
                log.warn("[{}:{}]任务队列服务抛弃指令【{}】!", getId(), getName(), workSize);
                workQueue.clear();
            }

            if (!workQueue.offer(task)) {
                log.error("[{}:{}]任务队列服务添加指令失败:[{}]", getId(), getName(), task.getClass().getName());
                return false;
            }

            // 队列如果不在执行中则执行命令
            if (!workQueue.isProcessing()) {
                workQueue.setProcessing(true);
                executor.execute(workQueue.poll());
            }
            return true;
        }
    }

    @Override
    public <T> CompletableFuture<T> submit(CallableCommand<T> callableCommand) {
        AbstractFutureQueueTask<T> commandWrapper = new CallableQueueCommandWrapper<>(callableCommand);
        commandWrapper.setFuture(new CompletableFuture<>());
        if (!submit(commandWrapper)) {
            return null;
        }
        return commandWrapper.getFuture();
    }

    @Override
    public CompletableFuture<?> submit(Command command) {
        AbstractFutureQueueTask commandWrapper = new QueueCommandWrapper(command);
        commandWrapper.setFuture(new CompletableFuture<Void>());
        if (!submit(commandWrapper)) {
            return null;
        }
        return commandWrapper.getFuture();
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Executor executor = Executors.newFixedThreadPool(16, new IndexedThreadFactory("测试线程"));
        TaskQueueService service = new TaskQueueService(0, "测试队列", executor);
        for (int i = 1; i <= 100; i++) {
            int index = i;
            CompletableFuture<?> future = service.submit(() -> {
                // try {
                //     Thread.sleep(1000L);
                // } catch (InterruptedException e) {
                //     e.printStackTrace();
                // }
                log.info("执行:{}", index);
            });
            // future.thenAcceptAsync(value -> log.info("{}", value), executor);
            // if ((index & 1) == 0) {
            //     future.cancel(false);
            // }
        }

    }
}
