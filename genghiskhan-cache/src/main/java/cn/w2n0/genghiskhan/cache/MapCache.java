package cn.w2n0.genghiskhan.cache;


import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MapCache
 * @author 无量
 * date 2021/10/20 14:24
 */
@Component
public class MapCache {

    public RedisTemplate redisTemplate;

    public MapCache(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 添加or更新hash的值
     *
     * @param key 键
     * @param field 字段
     * @param value 值
     * @param <T> 泛型对象
     */
    public <T> void hset(final String key, final String field, final T value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    /**
     * 获取hash中field对应的值
     *
     * @param key 键
     * @param field 字段
     * @param <T> 泛型对象
     * @return 对象
     */
    public <T> T hget(final String key, final String field) {
        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(key, field);
    }

    /**
     * 删除hash中field这一对kv
     *
     * @param key 键
     * @param field 字段
     */
    public void hdel(String key, String field) {
        redisTemplate.opsForHash().delete(key, field);
    }


    /**  使变量中的键以value值的大小进行自增长。
     * @param key 键
     * @param field 字段
     * @param value 值
     * @return 数量
     */
    public long hincr(String key, String field, long value) {
        return redisTemplate.opsForHash().increment(key, field, value);
    }


    /**
     * 命令用于返回哈希表中，一个或多个给定字段的值
     * @param key 键
     * @param fields 字段列表
     * @param <T> 泛型对象
     * @return 泛型map
     */
    public <T> Map<String, T> hmget(String key, List<String> fields) {
        List<String> result = redisTemplate.<String, String>opsForHash().multiGet(key, fields);
        Map<String, T> ans = new HashMap<>(fields.size());
        int index = 0;
        for (String field : fields) {
            if (result.get(index) == null) {
                continue;
            }
            ans.put(field, (T)result.get(index));
        }
        return ans;
    }
}
