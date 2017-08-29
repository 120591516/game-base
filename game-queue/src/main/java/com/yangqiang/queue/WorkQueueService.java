/**
 * 创建日期:  2017年08月28日 17:39
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.queue;

import com.yangqiang.game.SimpleThreadFactory;
import com.yangqiang.game.util.CodeExecTime;
import com.yangqiang.queue.impl.CallableTask;
import com.yangqiang.queue.impl.Task;
import com.yangqiang.queue.impl.UnlockedWorkQueue;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * 任务队列服务
 *
 * @author 杨 强
 */
@Data
@Slf4j
public class WorkQueueService implements IWorkQueueService {
    private int id;
    private String name;
    private IWorkQueue<Command> workQueue;
    private Executor executor;
    private int maxWorkSize;

    public WorkQueueService(int id, String name, Executor executor) {
        this(id, name, new UnlockedWorkQueue<>(), executor);
    }

    public WorkQueueService(int id, String name, IWorkQueue<Command> workQueue, Executor executor) {
        this.id = id;
        this.name = name;
        this.workQueue = workQueue;
        this.executor = executor;
    }

    private CompletableFuture submitTask(IQueueTask task) {
        synchronized (workQueue) {
            int workSize = workQueue.size();
            if (maxWorkSize > 0 && workSize > maxWorkSize) {
                log.warn("[{}:{}]任务队列服务抛弃指令【{}】!", getId(), getName(), workSize);
                workQueue.clear();
            }

            if (!workQueue.offer(task)) {
                log.error("[{}:{}]任务队列服务添加指令失败:[{}]", getId(), getName(), task.getCommand().getClass().getName());
                return null;
            }

            if (!workQueue.isProcessing()) {
                //如果该队列中的所有task都已经执行完毕，那么重新启动该队列的执行
                workQueue.setProcessing(true);
                getExecutor().execute(workQueue.poll());
            }
            return task.getFuture();
        }
    }

    /**
     * 提交一个带有返回值的队列任务
     *
     * @param work
     * @return
     */
    public <T> CompletableFuture<T> submit(CallableWork<T> work) {
        return submitTask(new CallableTask<>(this, work, new CompletableFuture<>()));
    }

    /**
     * 提交一个无返回值的任务
     *
     * @param work
     * @return
     */
    public CompletableFuture<Void> submit(Work work) {
        return submitTask(new Task(this, work, new CompletableFuture<Void>()));
    }

    public static void main(String[] args) throws InterruptedException {
        WorkQueueService service = new WorkQueueService(1, "测试任务队列服务", Executors.newFixedThreadPool(1, new SimpleThreadFactory("测试线程")));

        for (int i = 0; i < 1000; i++) {
            final int index = i;
            // System.out.println(index);
            // service.submit(() -> System.out.println(index));
            service.submit(() -> System.out.println("000000.....{}......" + index));
            // Thread.sleep(1L);
        }
    }
}
