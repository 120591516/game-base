/**
 * 创建日期:  2017年08月19日 11:21
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.config.util;

import com.yangqiang.config.IConverter;
import com.yangqiang.config.beans.*;
import com.yangqiang.config.annotation.Table;
import com.yangqiang.config.annotation.Column;

import com.yangqiang.game.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 反射工具
 *
 * @author YangQiang
 */
public class ReflectUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectUtils.class);

    /**
     * 获取指定类的表描述信息
     *
     * @param clz
     * @return
     * @throws Exception
     */
    public static TableDesc getTableDesc(Class clz) throws Exception {
        TableDesc tableDesc = new TableDesc(clz);

        tableDesc.setName(clz.getSimpleName());

        Table table = (Table) clz.getAnnotation(Table.class);
        if (table != null) {
            String tableName = table.name();
            if (tableName != null && !tableName.trim().isEmpty()) {
                tableDesc.setName(tableName);
            }

            String[] primaryKeys = table.primaryKey();
            if (primaryKeys != null && primaryKeys.length > 0) {
                tableDesc.setPrimaryKeys(primaryKeys);
            }
            tableDesc.setIndex(table.index());
            tableDesc.setHeader(table.header());
            tableDesc.setIgnoreRow(table.ignoreRow());
        }

        Map<String, ColumnDesc> columns = new HashMap<>();
        Field[] declaredFields = clz.getDeclaredFields();
        if (declaredFields != null) {
            for (Field field : declaredFields) {
                ColumnDesc columnDesc = getColumnDesc(field);
                columns.put(columnDesc.getName(), columnDesc);
            }
        }
        tableDesc.setColumns(columns);
        return tableDesc;
    }


    /**
     * 获取指定属性的列描述信息
     *
     * @param field
     * @return
     * @throws Exception
     */
    public static ColumnDesc getColumnDesc(Field field) throws Exception {
        String fieldName = field.getName();

        ColumnDesc columnDesc = new ColumnDesc();
        columnDesc.setName(fieldName);
        columnDesc.setField(field);

        try {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                String columnName = column.name();
                // 有单独设置列名
                if (columnName != null && !columnName.trim().isEmpty()) {
                    columnDesc.setName(columnName);
                }
                // 字段不为null
                columnDesc.setNotNull(column.notNull());

                // 将多个转换器转为一个
                Class<? extends IConverter>[] converters = column.value();
                if (converters != null && converters.length > 0) {
                    IConverter converter = converters[0].newInstance();
                    for (int i = 1; i < converters.length; i++) {
                        converter = converter.andThen(converters[i].newInstance());
                    }
                    columnDesc.setConverter(converter);
                }
            }
        } catch (Exception e) {
            LOGGER.error(StringUtil.format("解析[{}]中的[{}]属性错误", field.getDeclaringClass().getName(), fieldName), e);
            throw e;
        }

        return columnDesc;
    }
}
