/**
 * 创建日期:  2017年09月04日 10:48
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.data.db;

import com.yangqiang.data.PersistType;
import com.yangqiang.data.Persistable;
import com.yangqiang.work.Command;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;

/**
 * 单个持久化任务
 *
 * @author 杨 强
 */
@Slf4j
public class PersistTask<T extends Persistable> implements Command {
    /**
     * 任务名字
     */
    private String taskName;
    /**
     * 持久化数据
     */
    private T persist;
    /**
     * 持久化类型
     */
    private PersistType type;
    /**
     * 持久化所需的构造器
     */
    private PersistBuilder<T> persistBuilder;
    /**
     * 持久化操作的模版
     */
    private JdbcTemplate template;

    @Override
    public void action() {
        String sql = null;
        Object[] params = null;
        if (PersistType.INSERT == type) {
            sql = persistBuilder.insertSql();
            params = persistBuilder.insertParams(persist);
        } else if (PersistType.UPDATE == type) {
            sql = persistBuilder.updateSql();
            params = persistBuilder.updateParams(persist);
        } else if (PersistType.DELETE == type) {
            sql = persistBuilder.deleteSql();
            params = persistBuilder.deleteParams(persist);
        }
        if (sql == null) {
            log.error("{}|持久化任务失败, sql为null", taskName);
            return;
        }
        if (params == null) {
            log.error("{}|持久化任务失败, 参数为null", taskName);
            return;
        }

        try {
            template.execute(sql, params);
        } catch (SQLException e) {
            log.error("#################[{}]数据库操作失败，请立马重启###################", taskName);
            log.error(sql, e);
        }
    }
}
