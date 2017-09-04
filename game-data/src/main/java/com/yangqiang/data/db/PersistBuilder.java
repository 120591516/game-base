/**
 * 创建日期:  2017年09月01日 13:54
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.data.db;

/**
 * 持久化构造器
 *
 * @author 杨 强
 */

import com.yangqiang.data.Persistable;

public interface PersistBuilder<T extends Persistable> {
    /**
     * 插入语句
     *
     * @return
     */
    String insertSql();

    /**
     * 更新语句
     *
     * @return
     */
    String updateSql();

    /**
     * 删除语句
     *
     * @return
     */
    String deleteSql();

    /**
     * 插入参数
     *
     * @param persist
     * @return
     */
    Object[] insertParams(T persist);

    /**
     * 更新参数
     *
     * @param persist
     * @return
     */
    Object[] updateParams(T persist);

    /**
     * 删除参数
     *
     * @param persist
     * @return
     */
    Object[] deleteParams(T persist);
}
