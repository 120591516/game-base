/**
 * 创建日期:  2017年08月29日 19:01
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.work.queue;

import com.yangqiang.work.Command;

import java.util.concurrent.CompletableFuture;

/**
 * 基于队列的任务
 *
 * @author 杨 强
 */
public interface IQueueTask extends Command {
    /**
     * 获取执行的具体指令
     *
     * @return
     */
    Command getCommand();

    /**
     * 获取关联任务的future
     *
     * @return
     */
    CompletableFuture getFuture();

    /**
     * 获取当前任务所属的任务队列服务
     *
     * @return
     */
    IWorkQueueService getWorkQueueService();
}
