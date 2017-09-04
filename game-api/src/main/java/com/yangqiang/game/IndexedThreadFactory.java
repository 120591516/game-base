/**
 * 创建日期:  2017年08月31日 17:38
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.game;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 带索引的线程工厂
 *
 * @author 杨 强
 */
@Slf4j
public class IndexedThreadFactory implements ThreadFactory {
    private static final AtomicInteger INDEX = new AtomicInteger(1);
    private String name;

    public IndexedThreadFactory(String name) {
        this.name = name;
    }

    public IndexedThreadFactory(int initIndex, String name) {
        this(name);
        INDEX.set(initIndex);
    }

    @Override
    public Thread newThread(Runnable runnable) {
        int currentIndex = INDEX.getAndIncrement();
        String threadName = name + "-" + currentIndex;
        // log.info("创建线程:{}", threadName);
        return new Thread(runnable, threadName);
    }
}
