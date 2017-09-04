/**
 * 创建日期:  2017年09月01日 14:12
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.data.db;

import com.yangqiang.data.PersistType;
import com.yangqiang.data.Persistable;
import com.yangqiang.game.Cache;
import com.yangqiang.work.Command;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 批量持久化任务
 *
 * @author 杨 强
 */
@Slf4j
public class BatchPersistTask<T extends Persistable> implements Command {
    public static final int DEFAULT_MAX_BATCH_SIZE = 300;
    /**
     * 批量处理的最大数量
     */
    private int maxBatchSize;
    /**
     * 任务名字
     */
    private String taskName;
    /**
     * 持久化所需的构造器
     */
    private PersistBuilder<T> persistBuilder;
    /**
     * 持久化操作的模版
     */
    private JdbcTemplate template;
    /**
     * 持久化数据的缓存获取
     */
    private Cache<Long, T> cache;

    /**
     * 所有需要序列化的数据
     */
    private Map<Long, PersistType> persistMap = new HashMap<>();
    /**
     * 插入的数据参数
     */
    private List<Object[]> insertParams = new LinkedList<>();
    /**
     * 更新的数据参数
     */
    private List<Object[]> updateParams = new LinkedList<>();
    /**
     * 删除的数据参数
     */
    private List<Object[]> deleteParams = new LinkedList<>();

    /**
     * 上一次插入失败的参数
     */
    private List<Object[]> insertErrorParams = new LinkedList<>();

    /**
     * 上一次更新失败的参数
     */
    private List<Object[]> updateErrorParams = new LinkedList<>();

    /**
     * 上一次伤处失败的参数
     */
    private List<Object[]> deleteErrorParams = new LinkedList<>();

    public BatchPersistTask(String taskName, PersistBuilder<T> persistBuilder, JdbcTemplate template, Cache<Long, T> cache) {
        this(DEFAULT_MAX_BATCH_SIZE, taskName, persistBuilder, template, cache);
    }

    public BatchPersistTask(int maxBatchSize, String taskName, PersistBuilder<T> persistBuilder, JdbcTemplate template, Cache<Long, T> cache) {
        this.maxBatchSize = maxBatchSize;
        this.taskName = taskName;
        this.persistBuilder = persistBuilder;
        this.template = template;
        this.cache = cache;
    }

    /**
     * 添加一个持久化操作
     *
     * @param id
     * @param type
     */
    public void addPersist(long id, PersistType type) {
        synchronized (persistMap) {
            if (PersistType.INSERT == type) { //插入操作直接入库
                persistMap.put(id, type);
            } else if (PersistType.UPDATE == type) { //之前没有插入或删除操作则提交更新操作，否则执行之前的操作
                persistMap.putIfAbsent(id, type);
            } else if (PersistType.DELETE == type) {
                PersistType previous = persistMap.get(id);
                if (previous != null && previous == PersistType.INSERT) { //如果之前是插入操作则现在不需要插入
                    persistMap.remove(id);
                } else {
                    persistMap.put(id, type);
                }
            }
        }
    }

    /**
     * 移除一个持久化操作
     *
     * @param id
     */
    public void removePersist(long id) {
        synchronized (persistMap) {
            persistMap.remove(id);
        }
    }

    @Override
    public void action() {

        // 修复上次的错误
        fixErrorPersist();

        Map<Long, PersistType> tempPersists = new HashMap<>();
        synchronized (persistMap) {
            tempPersists.putAll(persistMap);
            persistMap.clear();
        }

        String insertSql = persistBuilder.insertSql();
        String updateSql = persistBuilder.updateSql();
        String deleteSql = persistBuilder.deleteSql();

        tempPersists.forEach((id, type) -> {
            T data = cache.get(id);
            if (data == null) {
                log.error("{}|{}|持久化数据未找到", id, type);
                return;
            }
            if (type == PersistType.UPDATE) {
                updateParams.add(persistBuilder.updateParams(data));

                if (updateParams.size() >= maxBatchSize) {
                    batchExecute(updateSql, updateParams, updateErrorParams);
                }
            } else if (type == PersistType.INSERT) {
                insertParams.add(persistBuilder.insertParams(data));

                if (insertParams.size() >= maxBatchSize) {
                    batchExecute(insertSql, insertParams, insertErrorParams);
                }

            } else if (type == PersistType.DELETE) {
                deleteParams.add(persistBuilder.deleteParams(data));

                if (deleteParams.size() >= maxBatchSize) {
                    batchExecute(deleteSql, deleteParams, deleteErrorParams);
                }
            }
        });

        // 下面处理剩余的数据

        if (!insertParams.isEmpty()) {
            batchExecute(insertSql, insertParams, insertErrorParams);
        }
        if (!updateParams.isEmpty()) {
            batchExecute(updateSql, updateParams, updateErrorParams);
        }
        if (!deleteParams.isEmpty()) {
            batchExecute(deleteSql, deleteParams, deleteErrorParams);
        }
    }


    private void fixErrorPersist() {
        if (!insertErrorParams.isEmpty()) {
            batchFix(persistBuilder.insertSql(), insertErrorParams);
        }

        if (!updateErrorParams.isEmpty()) {
            batchFix(persistBuilder.updateSql(), updateErrorParams);
        }

        if (!deleteErrorParams.isEmpty()) {
            batchFix(persistBuilder.deleteSql(), deleteErrorParams);
        }
    }

    private void batchExecute(String sql, List<Object[]> params, List<Object[]> errorParams) {
        try {
            template.batchExecute(sql, params);
        } catch (Exception e) {
            log.error("#################[{}]数据库操作失败，请立马重启###################", taskName);
            log.error(sql, e);
            errorParams.addAll(params);
        }
        params.clear();
    }

    private void batchFix(String sql, List<Object[]> params) {
        try {
            template.batchExecute(sql, params);
        } catch (Exception e) {
            log.error("[{}]执行数据库修复操作失败:{}", taskName, sql, e);
        }
        params.clear();
    }
}
