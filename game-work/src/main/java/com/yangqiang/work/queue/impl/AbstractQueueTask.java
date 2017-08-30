/**
 * 创建日期:  2017年08月29日 13:14
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.work.queue.impl;

import com.yangqiang.work.Command;
import com.yangqiang.work.queue.IQueueTask;
import com.yangqiang.work.queue.IWorkQueueService;
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
            workQueueService.executeCommand();
        }
    }
}
