/**
 * 创建日期:  2017年08月28日 17:07
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.work.queue.impl;

import com.yangqiang.work.CallableCommand;
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
public class CallableQueueCommandWrapper<T> extends AbstractFutureQueueTask<T> implements CallableCommand<T> {
    private CallableCommand<T> command;

    public CallableQueueCommandWrapper(CallableCommand<T> command) {
        this.command = command;
    }

    @Override
    public T call() {
        return command.call();
    }

    @Override
    public void action() {
        CompletableFuture future = getFuture();
        if (future != null) {
            if (future.isCancelled()) {
                return;
            }
            try {
                T result = call();
                future.complete(result);
            } catch (Throwable cause) {
                future.completeExceptionally(cause);
            }
        } else {
            call();
        }
    }
}
