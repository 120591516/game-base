/**
 * 创建日期:  2017年08月12日 10:24
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.config.excel.reader;

import com.yangqiang.config.ICellReader;
import com.yangqiang.config.IConverter;
import com.yangqiang.config.util.ExcelUtils;
import org.apache.poi.ss.usermodel.Cell;

/**
 * 字符串单元格读取器
 *
 * @author YangQiang
 */
public class ExcelCellStingReader implements ICellReader<Cell, String> {

    @Override
    public String getDefaultValue(Cell cell) {
        return null;
    }

    @Override
    public IConverter<Cell, String> getConverter() {
        return ExcelUtils::getCellStringValue;
    }
}
