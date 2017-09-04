/**
 * 创建日期:  2017年08月31日 17:22
 * 创建作者:  杨 强  <281455776@qq.com>
 */
package com.yangqiang.game.util;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于protostuff的序列化工具
 *
 * @author 杨 强
 */
public class SerializeUtil {
    private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();

    public static <T> byte[] encode(T obj) {
        LinkedBuffer buffer = LinkedBuffer.allocate();
        try {
            Class<T> clazz = (Class<T>) obj.getClass();
            Schema<T> schema = getSchema(clazz);
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    public static <T> T decode(byte[] bytes, Class<T> clazz) {
        try {
            T object = clazz.newInstance();
            return decode(bytes, clazz, object);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public static <T> T decode(byte[] bytes, Class<T> clazz, T obj) {
        Schema<T> schema = getSchema(clazz);
        ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
        return obj;
    }


    private static <T> Schema<T> getSchema(Class<T> cls) {
        Schema<T> schema = (Schema<T>) cachedSchema.get(cls);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(cls);
            if (schema != null) {
                cachedSchema.put(cls, schema);
            }
        }
        return schema;
    }
}
