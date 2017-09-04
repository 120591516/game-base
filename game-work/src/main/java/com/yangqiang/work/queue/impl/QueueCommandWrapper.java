/**
 * 创建日期:  2017年08月29日 18:15
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.work.queue.impl;

import com.yangqiang.work.Command;

import java.util.concurrent.CompletableFuture;

/**
 * 普通无返回值的任务
 *
 * @author 杨 强
 */
public class QueueCommandWrapper extends AbstractFutureQueueTask<Void> {
    private Command command;

    public QueueCommandWrapper(Command command) {
        this.command = command;
    }

    @Override
    public void action() {
        CompletableFuture future = getFuture();
        if (future != null) {
            if (future.isCancelled()) {
                return;
            }
            try {
                command.action();
                future.complete(null);
            } catch (Throwable cause) {
                future.completeExceptionally(cause);
            }
        } else {
            command.action();
        }
    }
}
