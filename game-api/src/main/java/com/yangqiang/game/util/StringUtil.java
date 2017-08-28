/**
 * 创建日期:  2017年08月25日 17:31
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.game.util;


import com.yangqiang.game.MessageFormatter;

/**
 * @author YangQiang
 */
public final class StringUtil {
    /**
     * 格式化字符串
     *
     * @param format
     * @param params
     * @return
     */
    public static final String format(String format, Object... params) {
        return MessageFormatter.arrayFormat(format, params).getMessage();
    }
}
