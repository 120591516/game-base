/**
 * 创建日期:  2017年08月31日 19:25
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.data;

/**
 * 可持久化接口
 *
 * @author 杨 强
 */
public interface Persistable extends Cacheable {
    boolean isDirty();

    void setDirty(boolean dirty);
}
