/**
 * 创建日期:  2017年08月30日 18:13
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.work.timer;

import io.netty.util.Timeout;

/**
 * 可定时的接口
 *
 * @author 杨 强
 */
public interface IScheduled {
    int getLoop();

    int getInitialDelay();

    int getStep();

    Timeout getTimeout();
}
