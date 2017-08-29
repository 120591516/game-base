/**
 * 创建日期:  2017年08月19日 10:20
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang;

import com.yangqiang.config.annotation.ConfigFileScan;
import com.yangqiang.config.annotation.PackageScan;
import com.yangqiang.config.beans.ItemConfig;
import com.yangqiang.config.excel.ExcelConfigDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author YangQiang
 */
@ConfigFileScan(path = "E:\\github.com\\QiangYoung\\game-base\\game-config\\src\\test\\resources", suffix = ".xlsx")
@PackageScan("com.yangqiang.config.beans")
public class Test {
    private static final Logger LOGGER = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) throws Exception {
        // System.out.println(Test.class.getClassLoader().getResource(""));
        ExcelConfigDataManager dataManager = new ExcelConfigDataManager(Test.class);

        // dataManager.setConfigFileSuffix(".xlsx");
        // 使用注解的方式需要设置配置类的包名
        // dataManager.setConfigPackage("com.yangqiang.config.beans");

        // 设置excel文件的路径  这里直接使用classpath路径
        // dataManager.setConfigFileDir(Test.class.getClassLoader().getResource("").getPath());

        // 设置xml配置文件的路径 不设置的话默认读取classpath路径下的data_config.xml 使用注解方式的话可以不用配置xml文件
        // dataManager.setXmlConfigFile(Test.class.getClassLoader().getResource("data_config.xml").getFile());

        // 初始化配置
        dataManager.init();

        ItemConfig config = dataManager.getConfig(ItemConfig.class, 11, "cc");
        LOGGER.info(config.toString());

        dataManager.getConfigs(ItemConfig.class).forEach(e -> LOGGER.info(e.toString()));


    }
}
