/**
 * 创建日期:  2017年09月02日 17:49
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.work.queue;

import java.util.concurrent.CompletableFuture;

/**
 * 关联future的队列任务
 *
 * @author 杨 强
 */
public interface FutureQueueTask<T> extends IQueueTask {
    CompletableFuture<T> getFuture();

    void setFuture(CompletableFuture<T> future);
}
