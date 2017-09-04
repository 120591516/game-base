/**
 * 创建日期:  2017年09月04日 10:22
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.work.queue.impl;

import com.yangqiang.work.CallableCommand;
import com.yangqiang.work.Command;
import com.yangqiang.work.queue.IQueueTask;
import com.yangqiang.work.queue.ITaskQueue;
import com.yangqiang.work.queue.ITaskQueuePoolService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * 任务队列池服务
 *
 * @author 杨 强
 */
@Slf4j
@Data
public class IndexedTaskQueuePoolService implements ITaskQueuePoolService {
    private int id;
    private String name;
    private int corePoolSize;
    private int maxWorkSize;
    private Map<Long, ITaskQueue<IQueueTask>> workQueuePool;
    private Executor executor;

    public IndexedTaskQueuePoolService(int id, String name, int corePoolSize, Executor executor) {
        this(id, name, corePoolSize, new ConcurrentHashMap<>(), executor);
    }

    public IndexedTaskQueuePoolService(int id, String name, int corePoolSize, Map<Long, ITaskQueue<IQueueTask>> workQueuePool, Executor executor) {
        this(id, name, corePoolSize, 0, workQueuePool, executor);
    }

    public IndexedTaskQueuePoolService(int id, String name, int corePoolSize, int maxWorkSize, Map<Long, ITaskQueue<IQueueTask>> workQueuePool, Executor executor) {
        if (corePoolSize < 0) {
            throw new IllegalArgumentException("corePoolSize不能为负数");
        }
        this.id = id;
        this.name = name;
        this.corePoolSize = corePoolSize;
        this.maxWorkSize = maxWorkSize;
        this.workQueuePool = workQueuePool;
        this.executor = executor;
    }

    @Override
    public boolean submit(long key, IQueueTask task) {
        Long workQueueKey = key % corePoolSize;
        ITaskQueue<IQueueTask> workQueue = workQueuePool.computeIfAbsent(workQueueKey, queueKey -> new UnlockedTaskQueue<>());
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
    public <T> CompletableFuture<T> submit(long key, CallableCommand<T> callableCommand) {
        AbstractFutureQueueTask<T> commandWrapper = new CallableQueueCommandWrapper<>(callableCommand);
        commandWrapper.setFuture(new CompletableFuture<>());
        if (!submit(key, commandWrapper)) {
            return null;
        }
        return commandWrapper.getFuture();
    }

    @Override
    public CompletableFuture<?> submit(long key, Command command) {
        AbstractFutureQueueTask commandWrapper = new QueueCommandWrapper(command);
        commandWrapper.setFuture(new CompletableFuture<Void>());
        if (!submit(key, commandWrapper)) {
            return null;
        }
        return commandWrapper.getFuture();
    }
}
