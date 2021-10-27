package cn.w2n0.genghiskhan.cache;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;

/**
 * set集合
 * @author 无量
 * date 2021/10/20 14:24
 */
public class SetCache {
    public RedisTemplate redisTemplate;

    public SetCache(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    /**
     * 新增一个  sadd
     *
     * @param key 键
     * @param value 值
     * @param <T> 泛型对象
     */
    public <T> void add(String key, T value) {
        redisTemplate.opsForSet().add(key, value);
    }

    /**
     * 删除集合中的值  srem
     *
     * @param key 键
     * @param value 值
     * @param <T> 泛型对象
     */
    public <T> void remove(String key, T value) {
        redisTemplate.opsForSet().remove(key, value);
    }

    /**
     * 判断是否包含  sismember
     *
     * @param key 键
     * @param value 值
     * @param <T> 泛型对象
     */
    public <T> void contains(String key, T value) {
        redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 获取集合中所有的值 smembers
     *
     * @param key 键
     * @param <T> 泛型对象
     * @return smembers
     */
    public <T> Set<T> values(String key) {
        return redisTemplate.opsForSet().members(key);
    }


    /**
     * @param key1 键1
     * @param key2 键2
     * @param <T> 泛型对象
     * @return 集合对象
     */
    public <T> Set<T> union(String key1, String key2) {
        return redisTemplate.opsForSet().union(key1, key2);
    }

    /**
     * 返回多个集合的交集 sinter
     *
     * @param key1 键1
     * @param key2 键2
     * @param <T> 泛型对象
     * @return 集合对象
     */
    public <T> Set<T> intersect(String key1, String key2) {
        return redisTemplate.opsForSet().intersect(key1, key2);
    }

    /**
     * 返回集合key1中存在，但是key2中不存在的数据集合  sdiff
     *
     * @param key1 键1
     * @param key2 键2
     * @param <T> 泛型对象
     * @return 集合对象
     */
    public <T> Set<T> diff(String key1, String key2) {
        return redisTemplate.opsForSet().difference(key1, key2);
    }

}
