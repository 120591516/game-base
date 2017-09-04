/**
 * 创建日期:  2017年08月30日 17:29
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.work.timer;

import com.yangqiang.work.Command;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 * 定时的任务
 *
 * @author 杨 强
 */
@Data
public abstract class ScheduledTask implements Command, TimerTask, IScheduled {
    private int timerDuration;
    private int loop;
    private int initialDelay;
    private int step;
    private Timeout timeout;

    public ScheduledTask(int timerDuration) {
        this(timerDuration, 1, 0, 0);
    }

    public ScheduledTask(int timerDuration, int loop, int step) {
        this(timerDuration, loop, 0, step);
    }

    public ScheduledTask(int timerDuration, int loop, int initialDelay, int step) {
        this.timerDuration = timerDuration;
        this.loop = loop;
        this.initialDelay = initialDelay;
        this.step = step;
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        try {
            // 无限循环的定时任务
            if (loop < 0) {
                this.timeout = timeout.timer().newTimeout(this, step - timerDuration, TimeUnit.MILLISECONDS);
            } else if (loop == 0) { // 只执行1次的
            } else { //多次执行的
                loop--;
                if (loop > 0) { //还有剩余次数
                    this.timeout = timeout.timer().newTimeout(this, step - timerDuration, TimeUnit.MILLISECONDS);
                }
            }
        } finally {
            action();
        }

    }
}
