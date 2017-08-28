/**
 * 创建日期:  2017年08月25日 17:03
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.game;

/**
 * 字符串格式化器
 *
 * @author YangQiang
 */
public class StringFormatter {
    public static String format(String format, Object... params) {
        FormattingTuple formattingTuple = MessageFormatter.arrayFormat(format, params);
        return formattingTuple.getMessage();
    }
}
