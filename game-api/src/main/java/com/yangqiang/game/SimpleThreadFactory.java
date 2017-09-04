/**
 * 创建日期:  2017年08月28日 17:24
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.game;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 简单的线程工厂
 *
 * @author 杨 强
 */
@Slf4j
public class SimpleThreadFactory implements ThreadFactory {
    private String name;

    public SimpleThreadFactory(String name) {
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        log.info("创建线程:{}", name);
        return new Thread(runnable, name);
    }
}
