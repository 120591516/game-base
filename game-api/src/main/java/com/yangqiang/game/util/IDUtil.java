/**
 * 创建日期:  2017年08月05日 14:48
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.game.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * id工具
 *
 * @author 杨 强
 */
public class IDUtil {
    private static final int SEQUENCE_BITS = 16; //自增的序列id占最低16位
    private static final long SEQUENCE_MASK = -1L ^ (-1L << SEQUENCE_BITS); //0xFFFF

    private static final int WORK_ID_BITS = 15; //具体的业务id占中间15位
    private static final int WORK_ID_LEFT_SHIFT = SEQUENCE_BITS;
    private static final long WORK_ID_MASK = -1L ^ (-1L << WORK_ID_BITS); //0x7FFF

    private static final int TIMESTAMP_BITS = 32; //时间戳
    private static final int TIMESTAMP_LEFT_SHIFT = WORK_ID_BITS + WORK_ID_LEFT_SHIFT;
    private static final long TIMESTAMP_MASK = -1L ^ (-1L << TIMESTAMP_BITS); //0xFFFFFF

    private static long lastTimestamp = System.currentTimeMillis() / 1000L;
    private static long sequence = 0;

    private static final Lock ID_LOCK = new ReentrantLock();

    /**
     * 保证时间有序 最多支持32767种业务id 每种每秒可生成65535个id
     *
     * @param workId
     * @return
     */
    public static long uniqueId(long workId) {
        ID_LOCK.lock();
        try {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) { //id自增到最大值时重置为1
                lastTimestamp += 1L;
            }
            long timestamp = System.currentTimeMillis() / 1000L;
            if (timestamp > lastTimestamp) { // 每秒重置自增id
                sequence = 0;
                lastTimestamp = timestamp;
            } else {
                timestamp = lastTimestamp;
            }
            return (((timestamp & TIMESTAMP_MASK) << TIMESTAMP_LEFT_SHIFT) | ((workId & WORK_ID_MASK) << WORK_ID_LEFT_SHIFT) | (sequence & SEQUENCE_MASK));
        } finally {
            ID_LOCK.unlock();
        }
    }
}
