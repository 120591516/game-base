/**
 * 创建日期:  2017年08月30日 18:06
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.work.timer.util;

import com.yangqiang.game.SimpleThreadFactory;
import com.yangqiang.work.Command;
import com.yangqiang.work.timer.IScheduled;
import com.yangqiang.work.timer.ScheduledWork;
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
public class ScheduledWorkUtil {
    public static class TimerHolder {
        private static final int TIMER_DURATION = 10;
        private static final Timer TIMER = new HashedWheelTimer(new SimpleThreadFactory("通用定时器"), TIMER_DURATION, TimeUnit.MILLISECONDS, 512);
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
     * @param work
     * @param timer
     */
    private static void schedule(ScheduledWork work, Timer timer) {
        work = Objects.requireNonNull(work);
        timer = Objects.requireNonNull(timer);
        timer.newTimeout(work, work.getInitialDelay(), TimeUnit.MILLISECONDS);
    }

    /**
     * 使用通用的定时器定时执行指定的指令
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
        ScheduledWork scheduledWork = new ScheduledWork(timerDuration, loop, initialDelay, step) {
            @Override
            public void action() {
                command.action();
            }
        };
        schedule(scheduledWork, timer);
        return scheduledWork;
    }
}
