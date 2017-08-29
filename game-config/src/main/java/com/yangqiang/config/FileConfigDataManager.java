/**
 * 创建日期:  2017年08月24日 17:19
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.config;

import com.yangqiang.config.annotation.Cache;
import com.yangqiang.config.annotation.Config;
import com.yangqiang.config.beans.TableDesc;
import com.yangqiang.config.parser.DataConfigXmlParser;
import com.yangqiang.game.util.ClassFileUtil;
import com.yangqiang.config.util.ReflectUtils;
import com.yangqiang.game.util.StringUtil;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 文件配置管理器
 * @author YangQiang
 */
public abstract class FileConfigDataManager extends AbstractConfigDataManager {
    protected FileConfigDataManagerConfig config;

    public FileConfigDataManager() {
        Class<?> clz = getClass();
        this.config = new FileConfigDataManagerConfig(clz);
    }

    public FileConfigDataManager(Class<?> configClz) {
        this(new FileConfigDataManagerConfig(configClz));
    }

    public FileConfigDataManager(FileConfigDataManagerConfig config) {
        this.config = config;
    }

    public FileConfigDataManager setXmlConfigFile(String file) {
        this.config.setXmlConfigFile(file);
        return this;
    }

    public FileConfigDataManager setConfigPackage(String configPackage) {
        this.config.setConfigPackage(configPackage);
        return this;
    }

    public FileConfigDataManager setConfigFileDir(String configFileDir) {
        this.config.setConfigFileDir(configFileDir);
        return this;
    }

    public FileConfigDataManager setConfigFileSuffix(String configFileSuffix) {
        this.config.setConfigFileSuffix(configFileSuffix);
        return this;
    }

    public String getXmlConfigFile() {
        return config.getXmlConfigFile();
    }

    public String getConfigPackage() {
        return config.getConfigPackage();
    }

    public String getConfigFileDir() {
        return config.getConfigFileDir();
    }

    public String getConfigFileSuffix() {
        return config.getConfigFileSuffix();
    }

    @Override
    public void init() throws Exception {
        parseFileConfig();
    }

    private void parseFileConfig() throws Exception {
        Map<String, Class> cacheClz = new HashMap<>();
        Map<String, TableDesc> configTable = new HashMap<>();

        String configPackage = getConfigPackage();
        if (configPackage != null) {
            // 获取所有的配置类和缓存类
            Set<Class> clzSet = ClassFileUtil.getClasses(configPackage, clz -> clz.isAnnotationPresent(Config.class) || clz.isAnnotationPresent(Cache.class));
            for (Class clz : clzSet) {
                if (clz.isAnnotationPresent(Config.class)) {
                    TableDesc tableDesc = ReflectUtils.getTableDesc(clz);
                    configTable.put(clz.getName(), tableDesc);
                } else if (clz.isAnnotationPresent(Cache.class)) {
                    cacheClz.put(clz.getName(), clz);
                }
            }
        }

        String xmlConfigFile = getXmlConfigFile();
        if (xmlConfigFile == null) {
            URL xmlConfigResource = FileConfigDataManager.class.getClassLoader().getResource(DEFAULT_XML_CONFIG_FILE);
            if (xmlConfigResource != null) {
                xmlConfigFile = xmlConfigResource.getFile();
            }
        }

        if (xmlConfigFile != null) {
            Map<String, TableDesc> xmlConfigs = DataConfigXmlParser.parseConfigs(xmlConfigFile);
            configTable.putAll(xmlConfigs);

            Map<String, Class> xmlCaches = DataConfigXmlParser.parseCaches(xmlConfigFile);
            cacheClz.putAll(xmlCaches);
        }

        Map<String, IConfigWrapper> tempConfigs = new HashMap<>();
        configTable.forEach((clzName, tableDesc) -> {
            IConfigWrapper wrapper = parseTableDesc(tableDesc);
            wrapper.getList().forEach(e -> {
                if (IConfig.class.isAssignableFrom(e.getClass())) {
                    IConfig config = (IConfig) e;
                    config.afterLoad();
                }
            });
            tempConfigs.put(clzName, wrapper);
        });

        Map<String, Object> tempCaches = new HashMap<>();
        cacheClz.forEach((clzName, clz) -> {
            try {
                Object obj = clz.newInstance();
                if (IConfigCache.class.isAssignableFrom(clz)) {
                    IConfigCache cache = (IConfigCache) obj;
                    cache.build();
                }
                LOGGER.info("加载缓存[{}]...", clz.getName());
                tempCaches.put(clzName, obj);
            } catch (Exception e) {
                throw new RuntimeException(StringUtil.format("创建对象[{}]错误", clz.getName()), e);
            }
        });

        configs = tempConfigs;
        caches = tempCaches;
    }

    public abstract IConfigWrapper parseTableDesc(TableDesc tableDesc);
}
