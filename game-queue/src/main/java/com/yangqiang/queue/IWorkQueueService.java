/**
 * 创建日期:  2017年08月29日 12:13
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.queue;

import java.util.concurrent.Executor;

/**
 * 任务执行服务
 *
 * @author 杨 强
 */
public interface IWorkQueueService {
    int getId();
    String getName();
    Executor getExecutor();
    IWorkQueue<Command> getWorkQueue();
    void executeCommand();
}
