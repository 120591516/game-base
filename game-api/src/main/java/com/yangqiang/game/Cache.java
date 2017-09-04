package com.yangqiang.game; /**
 * 创建日期:  2017年09月01日 11:52
 * 创建作者:  杨 强  <281455776@qq.com>
 */

import com.google.common.cache.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 数据缓存
 *
 * @author 杨 强
 */
@Slf4j
public class Cache<K, V> {
    private LoadingCache<K, V> cache;

    public Cache(CacheBuilder<K, V> cacheBuilder, CacheLoader<K, V> loader) {
        this.cache = cacheBuilder.build(loader);
    }

    public LoadingCache<K, V> getCache() {
        return cache;
    }

    public long size() {
        return cache.size();
    }

    /**
     * 获取一个值
     *
     * @param key
     * @return
     */
    public V get(K key) {
        try {
            return cache.get(key);
        } catch (ExecutionException e) {
            log.error("获取数据失败;{}", key, e);
        }
        return null;
    }

    /**
     * 放入一个值
     *
     * @param key
     * @param value
     */
    public void put(K key, V value) {
        cache.put(key, value);
    }

    /**
     * 移除一个值
     *
     * @param key
     */
    public void remove(K key) {
        cache.invalidate(key);
    }

    public static void main(String[] args) throws InterruptedException {
        Cache<Integer, Integer> cache = new Cache<>(CacheBuilder.<Integer, Integer>newBuilder().maximumSize(110).expireAfterAccess(5, TimeUnit.SECONDS).removalListener(new RemovalListener<Integer, Integer>() {
            @Override
            public void onRemoval(RemovalNotification<Integer, Integer> notification) {
                System.out.println("移除" + notification.getKey() + ":" + notification.getValue() + ":" + notification.getCause());
            }
        }), new CacheLoader<Integer, Integer>() {
            int num = 0;

            @Override
            public Integer load(Integer key) throws Exception {
                return ++num;
            }
        });
        for (int i = 1; i < 100; i++) {
            cache.get(i);
        }
        // System.out.println("移除一个值");
        // cache.remove(1);
        new Thread(new Runnable() {
            int i = 100;

            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    cache.get(i++);
                }
            }
        }).start();
        while (true)
            for (int i = 1; i < 10; i++) {
                Thread.sleep(1000L);
                // System.out.println("访问：" + cache.get((int) (Math.random() * 10 + 1)));
                System.out.println("访问：" + i + ":" + cache.get(i));
            }
        // System.out.println(cache.size());
        // while (true){
        //     Thread.sleep(1000L);
        //     System.out.println("随机访问：" + cache.get((int) (Math.random() * 10 + 1)));
        // }
    }
}
