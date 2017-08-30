/**
 * 创建日期:  2017年08月29日 18:15
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.work.queue.impl;

import com.yangqiang.work.queue.IWorkQueueService;
import com.yangqiang.work.Work;

import java.util.concurrent.CompletableFuture;

/**
 * 任务
 *
 * @author 杨 强
 */
public class Task extends AbstractQueueTask {
    public Task(IWorkQueueService workQueueService, Work work, CompletableFuture future) {
        super(workQueueService, work, future);
    }

    @Override
    public void action() {
        command.action();
        future.complete(null);
    }
}
