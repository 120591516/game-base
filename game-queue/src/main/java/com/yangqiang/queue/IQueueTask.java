/**
 * 创建日期:  2017年08月29日 19:01
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.queue;

import java.util.concurrent.CompletableFuture;

/**
 * 基于队列的任务
 *
 * @author 杨 强
 */
public interface IQueueTask extends Command {
    Command getCommand();

    void setCommand(Command command);

    CompletableFuture getFuture();

    void setFuture(CompletableFuture future);

    IWorkQueueService getWorkQueueService();

    void setWorkQueueService(IWorkQueueService workQueueService);
}
