/**
 * 创建日期:  2017年08月29日 12:13
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.work.queue;

import com.yangqiang.work.CallableCommand;
import com.yangqiang.work.Command;

import java.util.concurrent.CompletableFuture;

/**
 * 任务执行服务
 *
 * @author 杨 强
 */
public interface ITaskQueueService {

    /**
     * 提交一个队列任务
     *
     * @param task 队列任务
     * @return 成功或失败
     */
    boolean submit(IQueueTask task);

    /**
     * 提交一个带有返回值的指令
     *
     * @param command 带返回值的指令
     * @return CompletableFuture
     */
    <T> CompletableFuture<T> submit(CallableCommand<T> command);

    /**
     * 提交一个无返回值的指令
     *
     * @param command 无返回值的指令
     */
    CompletableFuture<?> submit(Command command);
}
