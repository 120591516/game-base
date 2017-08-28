/**
 * 创建日期:  2017年08月21日 18:08
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.game.config.excel;

import com.yangqiang.game.config.FileConfigDataManager;
import com.yangqiang.game.config.FileConfigDataManagerConfig;
import com.yangqiang.game.config.IConfigWrapper;
import com.yangqiang.game.config.beans.TableDesc;

import java.io.File;

/**
 * excel配置管理器
 *
 * @author YangQiang
 */
public class ExcelConfigDataManager extends FileConfigDataManager {
    public ExcelConfigDataManager() {
    }

    public ExcelConfigDataManager(Class<?> configClz) {
        super(configClz);
    }

    public ExcelConfigDataManager(FileConfigDataManagerConfig config) {
        super(config);
    }

    public IConfigWrapper parseTableDesc(TableDesc tableDesc) {
        StringBuilder configFile = new StringBuilder();
        String configFileDir = getConfigFileDir();
        if (configFileDir != null) {
            configFile.append(configFileDir).append(File.separatorChar);
        }
        configFile.append(tableDesc.getName());
        String configFileSuffix = getConfigFileSuffix();
        if (configFileSuffix != null) {
            configFile.append(configFileSuffix);
        }
        return new ExcelConfigWrapper(configFile.toString(), tableDesc).build();
    }
}
