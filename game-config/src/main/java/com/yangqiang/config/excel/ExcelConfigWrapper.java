/**
 * 创建日期:  2017年08月22日 10:19
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.config.excel;

import com.yangqiang.config.ConfigContainer;
import com.yangqiang.config.IConfigWrapper;
import com.yangqiang.config.beans.ColumnDesc;
import com.yangqiang.config.beans.TableDesc;
import com.yangqiang.config.util.BeanUtils;
import com.yangqiang.config.util.ExcelUtils;
import com.yangqiang.game.util.StringUtil;
import lombok.Data;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * excel配置包装器
 *
 * @author YangQiang
 */
@Data
public class ExcelConfigWrapper implements IConfigWrapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelConfigWrapper.class);

    private ConfigContainer container;
    private String excelFilePath;
    private String keyDelimiter = "_";
    private TableDesc tableDesc;

    public ExcelConfigWrapper(String excelFilePath, TableDesc tableDesc) {
        this.excelFilePath = excelFilePath;
        this.tableDesc = tableDesc;
        this.container = new ConfigContainer<>();
    }

    @Override
    public <T> T getConfig(Object key) {
        return (T) container.getConfig(key);
    }

    @Override
    public <T> List<T> getList() {
        return container.getList();
    }


    @Override
    public ExcelConfigWrapper build() {
        LOGGER.info("加载配置表[{}]", tableDesc.getName());
        Workbook workbook = ExcelUtils.getWorkbook(excelFilePath);
        if (workbook == null) {
            throw new RuntimeException(StringUtil.format("[{}]打开excel文件失败", excelFilePath));
        }
        Class clz = tableDesc.getClz();
        Map<String, ColumnDesc> columns = tableDesc.getColumns();

        String[] primaryKeys = tableDesc.getPrimaryKeys();
        if (primaryKeys != null && primaryKeys.length > 0) {
            for (String primaryKey : primaryKeys) {
                if (primaryKey == null) {
                    throw new RuntimeException(StringUtil.format("[{}]主键错误", clz.getName()));
                }
                try {
                    clz.getDeclaredField(primaryKey);
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(StringUtil.format("[{}]无效的主键[{}]", clz.getName(), primaryKey));
                }
            }
        } else {
            LOGGER.warn("【{}】中没有找到主键", clz.getName());
        }

        if (tableDesc.getIndex() < 0) {
            throw new RuntimeException(StringUtil.format("[{}]无效的index索引[{}]", clz.getName(), tableDesc.getIndex()));
        }
        if (tableDesc.getHeader() < 0) {
            throw new RuntimeException(StringUtil.format("[{}]无效的header索引[{}]", clz.getName(), tableDesc.getHeader()));
        }
        if (tableDesc.getIgnoreRow() != null) {
            for (int row : tableDesc.getIgnoreRow()) {
                if (row < 0) {
                    throw new RuntimeException(StringUtil.format("[{}]无效的忽略行索引[{}]", clz.getName(), row));
                }
            }
        }

        Map<String, String> warnKey = new HashMap<>();
        Map<String, String> errorKey = new HashMap<>();

        Map<Integer, Map<Integer, String>> table = ExcelUtils.readExcelSheetToMap(workbook.getSheetAt(tableDesc.getIndex()));
        if (tableDesc.getIgnoreRow() != null) {
            for (int ignoreRow : tableDesc.getIgnoreRow()) {
                table.remove(ignoreRow);
            }
        }
        Map<Integer, String> header = table.remove(tableDesc.getHeader());
        table.forEach((rowIndex, rowData) -> {
            try {
                Object config = clz.newInstance();
                header.forEach((columnIndex, columnName) -> {
                    String cellData = rowData.get(columnIndex);
                    ColumnDesc columnDesc = columns.get(columnName);
                    if (columnDesc == null) {
                        warnKey.put(columnName, StringUtil.format("[{}]中没有找到[{}]属性", clz.getName(), columnName));
                        return;
                    }

                    if (columnDesc.isNotNull() && cellData == null) {
                        errorKey.put(columnName, StringUtil.format("[{}]中[{}]列不允许为null", tableDesc.getName(), columnName));
                        return;
                    }
                    Object newValue = cellData;
                    Field field = columnDesc.getField();

                    try {
                        if (columnDesc.getConverter() != null) {
                            newValue = columnDesc.getConverter().convert(newValue);
                        }
                    } catch (Exception e) {
                        errorKey.put(columnName, StringUtil.format("[{}]中[{}]的转换器错误", clz.getName(), field.getName()));
                        return;
                    }
                    try {
                        field.setAccessible(true);
                        Object oldValue = field.get(config);
                        BeanUtils.setProperty(config, field.getName(), newValue);
                        newValue = field.get(config);
                        // 如果最后解析成null而默认值不是null则还原回去
                        if (newValue == null && oldValue != null) {
                            field.set(config, oldValue);
                        }
                    } catch (Exception e) {
                        errorKey.put(columnName, StringUtil.format("[{}]中[{}]设值失败:{}", clz.getName(), field.getName(), cellData));
                        e.printStackTrace();
                    }
                });
                if (primaryKeys != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < primaryKeys.length; i++) {
                        String primaryKey = primaryKeys[i];
                        Field field = null;
                        try {
                            field = clz.getDeclaredField(primaryKey);
                        } catch (NoSuchFieldException e) {
                            errorKey.put(primaryKey, StringUtil.format("[{}]中无效的主键[{}]", clz.getName(), primaryKey));
                        }
                        field.setAccessible(true);
                        try {
                            Object value = field.get(config);
                            if (i == 0) {
                                sb.append(value);
                            } else {
                                sb.append(getKeyDelimiter()).append(value);
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    container.getConfigMap().put(sb.toString(), config);
                }
                container.getConfigList().add(config);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        if (!warnKey.isEmpty()) {
            warnKey.forEach((key, warn) -> LOGGER.warn(warn));
        }

        if (!errorKey.isEmpty()) {
            errorKey.forEach((key, error) -> LOGGER.error(error));
            throw new RuntimeException(StringUtil.format("[{}]:[{}]解析失败", tableDesc.getName(), clz.getName()));
        }
        return this;
    }
}
