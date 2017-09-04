/**
 * 创建日期:  2017年08月31日 20:16
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.data.db;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 操作数据库
 *
 * @author 杨 强
 */
@Slf4j
public class JdbcTemplate {
    private ConnectionPool pool;

    public JdbcTemplate(ConnectionPool pool) {
        this.pool = pool;
    }

    /**
     * 查询单条数据
     *
     * @param sql
     * @param mapper
     * @param params
     * @param <T>
     * @return
     */
    public <T> T query(String sql, RowMapper<T> mapper, Objects... params) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapper.mapping(rs);
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            release(rs, ps, conn);
        }
        return null;
    }

    /**
     * 查询多条数据
     *
     * @param sql
     * @param mapper
     * @param params
     * @param <T>
     * @return
     */
    public <T> List<T> queryList(String sql, RowMapper<T> mapper, Object... params) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            rs = ps.executeQuery();
            List<T> ret = new ArrayList<>(rs.getFetchSize());
            while (rs.next()) {
                ret.add(mapper.mapping(rs));
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            release(rs, ps, conn);
        }
        return Collections.emptyList();
    }

    /**
     * 执行指定的sql语句 包括插入, 更新, 删除等操作
     *
     * @param sql
     * @param params
     * @return
     */
    public int execute(String sql, Object... params) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            release(null, ps, conn);
        }
    }


    public int[] batchExecute(String sql, List<Object[]> params) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(sql);
            for (Object[] objs : params) {
                for (int i = 0; i < objs.length; i++) {
                    ps.setObject(i + 1, objs[i]);
                }
                ps.addBatch();
            }
            int[] ret = ps.executeBatch();
            conn.commit();
            return ret;
        } catch (SQLException e) {
            throw e;
        } finally {
            release(null, ps, conn);
        }
    }


    private void release(ResultSet rs, PreparedStatement ps, Connection conn) {

        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.error("ResultSet关闭出错", e);
            }
        }

        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                log.error("PreparedStatement关闭出错", e);
            }
        }

        if (conn != null) {
            try {
                pool.release(conn);
            } catch (SQLException e) {
                log.error("Connection关闭出错", e);
            }
        }
    }
}
