/**
 * 创建日期:  2017年08月31日 19:54
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.data.db;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * 基于druid的数据库连接池
 *
 * @author 杨 强
 */
public class DruidConnectionPool implements ConnectionPool {
    private DataSource dataSource;

    public DruidConnectionPool(Map properties) throws Exception {
        dataSource = DruidDataSourceFactory.createDataSource(properties);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void release(Connection connection) throws SQLException {
        connection.close();
    }
}
