package cn.w2n0.genghiskhan.cache;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;

/**
 * 有序列表
 * @author 无量
 * @date 2021/10/20 14:24
 */
public class ZsetCache {

    public RedisTemplate redisTemplate;

    public ZsetCache(RedisTemplate redisTemplate){
        this.redisTemplate=redisTemplate;

    }
    /**
     * 添加一个元素, zset与set最大的区别就是每个元素都有一个score，因此有个排序的辅助功能;  zadd
     *
     * @param key
     * @param value
     * @param score
     */
    public <T> void add(String key, T value, double score) {
        redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 删除元素 zrem
     *
     * @param key
     * @param value
     */
    public <T> void remove(String key, T value) {
        redisTemplate.opsForZSet().remove(key, value);
    }

    /**
     * score的增加or减少 zincrby
     *
     * @param key
     * @param value
     * @param score
     */
    public <T> Double incrScore(String key, T value, double score) {
        return redisTemplate.opsForZSet().incrementScore(key, value, score);
    }

    /**
     * 查询value对应的score   zscore
     *
     * @param key
     * @param value
     * @return
     */
    public <T> Double score(String key, T value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    /**
     * 判断value在zset中的排名  zrank
     *
     * @param key
     * @param value
     * @return
     */
    public <T> Long rank(String key, T value) {
        return redisTemplate.opsForZSet().rank(key, value);
    }

    /**
     * 查询集合中指定顺序的值， 0 -1 表示获取全部的集合内容  zrange
     *
     * 返回有序的集合，score小的在前面
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public <T> Set<T>  range(String key, int start, int end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * 查询集合中指定顺序的值和score，0, -1 表示获取全部的集合内容
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public <T>Set<ZSetOperations.TypedTuple<T>> rangeWithScore(String key, int start, int end) {
        return redisTemplate.opsForZSet().rangeWithScores(key, start, end);
    }

    /**
     * 查询集合中指定顺序的值  zrevrange
     *
     * 返回有序的集合中，score大的在前面
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public <T> Set<T>  revRange(String key, int start, int end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * 根据score的值，来获取满足条件的集合  zrangebyscore
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public <T> Set<T> sortRange(String key, int min, int max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * 返回集合的长度
     *
     * @param key
     * @return
     */
    public Long size(String key) {
        return redisTemplate.opsForZSet().zCard(key);
    }

}
