/**
 * 创建日期:  2017年08月31日 19:39
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.data.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库连接池接口
 *
 * @author 杨 强
 */
public interface ConnectionPool {
    /**
     * 获取一个数据库连接
     *
     * @return
     * @throws SQLException
     */
    Connection getConnection() throws SQLException;

    /**
     * 释放一个数据库连接
     *
     * @param connection
     * @throws SQLException
     */
    void release(Connection connection) throws SQLException;
}
