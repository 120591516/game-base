/**
 * 创建日期:  2017年08月28日 17:07
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.queue.impl;

import com.yangqiang.queue.CallableWork;
import com.yangqiang.queue.IWorkQueueService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * 带返回值队列任务
 *
 * @author 杨 强
 */
@Data
@Slf4j
public class CallableTask<T> extends AbstractQueueTask {
    public CallableTask(IWorkQueueService workQueueService, CallableWork<T> work, CompletableFuture<T> future) {
        super(workQueueService, work, future);
    }

    @Override
    public void action() {
        CallableWork<T> work = (CallableWork<T>) command;
        future.complete(work.call());
    }
}
