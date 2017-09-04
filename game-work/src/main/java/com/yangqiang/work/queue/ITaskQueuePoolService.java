/**
 * 创建日期:  2017年09月04日 10:19
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.work.queue;

import com.yangqiang.work.CallableCommand;
import com.yangqiang.work.Command;

import java.util.concurrent.CompletableFuture;

/**
 * @author 杨 强
 */
public interface ITaskQueuePoolService {
    /**
     * 提交一个队列任务
     *
     * @param key 关联队列的key
     * @param task 队列任务
     * @return 成功或失败
     */
    boolean submit(long key, IQueueTask task);

    /**
     * 提交一个带有返回值的指令
     *
     * @param key 关联队列的key
     * @param command 带返回值的指令
     * @return CompletableFuture
     */
    <T> CompletableFuture<T> submit(long key, CallableCommand<T> command);

    /**
     * 提交一个无返回值的指令
     *
     * @param key 关联队列的key
     * @param command 无返回值的指令
     */
    CompletableFuture<?> submit(long key, Command command);
}
