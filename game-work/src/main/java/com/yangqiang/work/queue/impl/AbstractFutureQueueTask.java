/**
 * 创建日期:  2017年09月02日 17:51
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.work.queue.impl;

import com.yangqiang.work.queue.FutureQueueTask;
import lombok.Data;

import java.util.concurrent.CompletableFuture;

/**
 * @author 杨 强
 */
@Data
public abstract class AbstractFutureQueueTask<T> extends AbstractQueueTask implements FutureQueueTask<T> {
    private CompletableFuture<T> future;
}
