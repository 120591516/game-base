package com.yangqiang.config.beans;

import com.yangqiang.config.IConfig;
import com.yangqiang.config.annotation.Column;
import com.yangqiang.config.annotation.Config;
import com.yangqiang.config.annotation.Table;
import com.yangqiang.config.converters.IntegerArrayConverter;
import com.yangqiang.config.converters.IntegerMapConverter;
import lombok.Data;
import lombok.ToString;

import java.util.Map;

@ToString
@Data
@Config
@Table(name = "cfg_item", primaryKey = {"id", "secondId"}, header = 3, ignoreRow = {0, 1, 2})
public class ItemConfig implements IConfig {
    @Column(notNull = true)
    private int id;

    @Column(name = "id2")
    private String secondId;

    @Column({IntegerArrayConverter.class, IntegerMapConverter.class})
    private Map<Integer, Integer> map;

    @Column(name = "ints", value = IntegerArrayConverter.class)
    private int[] arrays;
}