/**
 * 创建日期:  2017年08月31日 20:17
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.data.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据库结果转换器
 *
 * @author 杨 强
 */
public interface RowMapper<T> {

    T mapping(ResultSet rs) throws SQLException;

    class IntegerRowMapper implements RowMapper<Integer> {

        @Override
        public Integer mapping(ResultSet rs) throws SQLException {
            return rs.getInt(1);
        }
    }

    class LongRowMapper implements RowMapper<Long> {

        @Override
        public Long mapping(ResultSet rs) throws SQLException {
            return rs.getLong(1);
        }
    }

    class StringRowMapper implements RowMapper<String> {

        @Override
        public String mapping(ResultSet rs) throws SQLException {
            return rs.getString(1);
        }
    }

    class MapRowMapper implements RowMapper<Map<String, Object>> {
        @Override
        public Map<String, Object> mapping(ResultSet rs) throws SQLException {
            Map<String, Object> map = new HashMap<>();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                map.put(metaData.getColumnName(i), rs.getObject(i));
            }
            return map;
        }
    }

    class ByteArrayRowMapper implements RowMapper<byte[]> {

        @Override
        public byte[] mapping(ResultSet rs) throws SQLException {
            return rs.getBytes(1);
        }
    }
}
