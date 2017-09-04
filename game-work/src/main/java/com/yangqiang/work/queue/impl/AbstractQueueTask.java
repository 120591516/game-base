/**
 * 创建日期:  2017年08月29日 13:14
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.work.queue.impl;

import com.yangqiang.work.queue.IQueueTask;
import com.yangqiang.work.queue.ITaskQueue;
import lombok.Data;

import java.util.concurrent.Executor;

/**
 * @author 杨 强
 */
@Data
public abstract class AbstractQueueTask implements IQueueTask {
    private ITaskQueue<IQueueTask> workQueue;
    private Executor executor;
}
