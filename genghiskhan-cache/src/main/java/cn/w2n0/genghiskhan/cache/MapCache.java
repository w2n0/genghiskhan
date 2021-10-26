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
 * @date 2021/10/20 14:24
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
     * @param key
     * @param field
     * @param value
     */
    public <T> void hset(final String key, final String field, final T value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    /**
     * 获取hash中field对应的值
     *
     * @param key
     * @param field
     * @return
     */
    public <T> T hget(final String key, final String field) {
        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(key, field);
    }

    /**
     * 删除hash中field这一对kv
     *
     * @param key
     * @param field
     */
    public void hdel(String key, String field) {
        redisTemplate.opsForHash().delete(key, field);
    }

    // hash 结构的计数

    public long hincr(String key, String field, long value) {
        return redisTemplate.opsForHash().increment(key, field, value);
    }




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
