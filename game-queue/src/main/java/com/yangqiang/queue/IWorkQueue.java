/**
 * 创建日期:  2017年08月28日 15:39
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.queue;

/**
 * 任务队列接口
 *
 * @author 杨 强
 */
public interface IWorkQueue<V> {
    /**
     * 获取一个命令
     *
     * @return
     */
    V poll();

    /**
     * 添加一个命令
     *
     * @param value
     * @return
     */
    boolean offer(V value);

    /**
     * 清空命令队列
     */
    void clear();

    /**
     * 获取命令队列长度
     *
     * @return
     */
    int size();

    /**
     * 是否正在处理当中
     *
     * @return
     */
    boolean isProcessing();

    void setProcessing(boolean processing);
}
