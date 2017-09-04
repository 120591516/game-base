/**
 * 创建日期:  2017年08月30日 18:06
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.work.timer.util;

import com.yangqiang.game.SimpleThreadFactory;
import com.yangqiang.work.Command;
import com.yangqiang.work.timer.IScheduled;
import com.yangqiang.work.timer.ScheduledTask;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 定时任务工具
 *
 * @author 杨 强
 */
@Slf4j
public class ScheduledTaskUtil {
    public static class TimerHolder {
        private static final int TIMER_DURATION = 10;
        private static final Timer TIMER = new HashedWheelTimer(new SimpleThreadFactory("默认定时器"), TIMER_DURATION, TimeUnit.MILLISECONDS, 512);
    }

    /**
     * 关闭内部的timer
     *
     * @return
     */
    public static Set<Timeout> stop() {
        return TimerHolder.TIMER.stop();
    }

    /**
     * 使用指定的timer定时执行指定的定时任务
     *
     * @param task
     * @param timer
     */
    private static Timeout schedule(ScheduledTask task, Timer timer) {
        task = Objects.requireNonNull(task);
        timer = Objects.requireNonNull(timer);
        return timer.newTimeout(task, task.getInitialDelay(), TimeUnit.MILLISECONDS);
    }

    /**
     * 使用默认的定时器定时执行指定的指令
     *
     * @param loop
     * @param initialDelay
     * @param step
     * @param command
     * @return
     */
    public static IScheduled schedule(int loop, int initialDelay, int step, Command command) {
        return schedule(loop, initialDelay, step, command, TimerHolder.TIMER_DURATION, TimerHolder.TIMER);
    }

    /**
     * 使用指定的定时器执行指定的命令
     *
     * @param loop
     * @param initialDelay
     * @param step
     * @param command
     * @param timerDuration
     * @param timer
     * @return
     */
    public static IScheduled schedule(int loop, int initialDelay, int step, Command command, int timerDuration, Timer timer) {
        ScheduledTask scheduledTask = new ScheduledTask(timerDuration, loop, initialDelay, step) {
            @Override
            public void action() {
                command.action();
            }
        };
        scheduledTask.setTimeout(schedule(scheduledTask, timer));
        return scheduledTask;
    }

    public static void main(String[] args) {
        for (int i = 1; i <= 100; i++) {
            int index = i;
            schedule(10, 0, 1000, new Command() {
                @Override
                public void action() {
                    log.info("{}", index);
                }
            });
        }
    }
}
