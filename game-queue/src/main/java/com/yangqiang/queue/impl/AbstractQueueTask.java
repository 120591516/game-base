/**
 * 创建日期:  2017年08月29日 13:14
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.queue.impl;

import com.yangqiang.queue.Command;
import com.yangqiang.queue.IQueueTask;
import com.yangqiang.queue.IWorkQueue;
import com.yangqiang.queue.IWorkQueueService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * @author 杨 强
 */
@Data
@Slf4j
public abstract class AbstractQueueTask implements IQueueTask {
    protected Command command;
    protected CompletableFuture future;
    protected IWorkQueueService workQueueService;

    public AbstractQueueTask(IWorkQueueService workQueueService, Command command, CompletableFuture future) {
        this.workQueueService = workQueueService;
        this.command = command;
        this.future = future;
    }

    @Override
    public void run() {
        try {
            if (future.isCancelled()) {
                return;
            }
            action();
        } catch (Throwable cause) {
            future.completeExceptionally(cause);
        } finally {
            if (workQueueService == null) {
                log.error("任务队列服务不存在null");
                return;
            }
            IWorkQueue<Command> workQueue = workQueueService.getWorkQueue();
            if (workQueue == null) {
                log.error("[{}:{}]任务队列不存在null", workQueueService.getId(), workQueueService.getName());
                return;
            }
            synchronized (workQueue) {
                Command nextCommand = workQueue.poll();
                if (nextCommand == null) {
                    // 执行完毕后队列中没有命令了设置为执行完毕标识
                    workQueue.setProcessing(false);
                } else {
                    // 如果队列中还有其他命令则继续执行下一个命令
                    workQueueService.getExecutor().execute(nextCommand);
                }
            }
        }
    }
}
