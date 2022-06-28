package cn.w2n0.genghiskhan.cache;

import lombok.Getter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 缓存处理类
 * @author 无量
 * date 2021/10/20 14:24
 */
@Getter
public class RedisCache {

    private RedisTemplate redisTemplate;

    private SetCache setCache;

    private ZsetCache zSetCache;

    private MapCache mapCache;

    private ListCache listCache;

    public RedisCache(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.setCache = new SetCache(redisTemplate);
        this.zSetCache = new ZsetCache(redisTemplate);
        this.mapCache = new MapCache(redisTemplate);
        this.listCache=new ListCache(redisTemplate);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     * @param <T> 泛型对象
     */
    public <T> void setCacheObject(final String key, final T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     * @param <T> 泛型对象
     */
    public <T> void setCacheObject(final String key, final T value, final Integer timeout, final TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout, final TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @param <T> 泛型对象
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(final String key) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }

    /**
     * 删除单个对象
     *
     * @param key 键
     * @return 删除成功状态
     */
    public boolean deleteObject(final String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 删除集合对象
     *
     * @param collection 多个对象
     * @return 数量
     */
    public long deleteObject(final Collection collection) {
        return redisTemplate.delete(collection);
    }

    /**
     * 缓存List数据
     *
     * @param key      缓存的键值
     * @param dataList 待缓存的List数据
     * @param <T> 泛型对象
     * @return 缓存的对象
     */
    public <T> long setCacheList(final String key, final List<T> dataList) {
        Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
        return count == null ? 0 : count;
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @param <T> 泛型对象
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getCacheList(final String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 缓存Set
     *
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @param <T> 泛型对象
     * @return 缓存数据的对象
     */
    public <T> long   setCacheSet(final String key, final Set<T> dataSet) {
        if(dataSet!=null)
        {
            SetOperations<String, T> set= redisTemplate.opsForSet();
            dataSet.forEach((s)->{
                set.add(key,s);
            });
            return dataSet.size();
        }
        return 0;
    }

    /**
     * 获得缓存的set
     *
     * @param key 键
     * @param <T> 泛型对象
     * @return 集合
     */
    public <T> Set<T> getCacheSet(final String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 缓存Map
     *
     * @param key 键
     * @param <T> 泛型对象
     * @param dataMap 数据集合
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap) {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

    /**
     * 获得缓存的Map
     *
     * @param key 键
     * @param <T> 泛型对象
     * @return map
     */
    public <T> Map<String, T> getCacheMap(final String key) {
        return redisTemplate.opsForHash().entries(key);
    }


    /**
     * 获取多个Hash中的数据
     *
     * @param key   Redis键
     * @param hKeys Hash键集合
     * @param <T> 泛型对象
     * @return Hash对象集合
     */
    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<Object> hKeys) {
        return redisTemplate.opsForHash().multiGet(key, hKeys);
    }

    /**
     * 删除hashMap的子键
     *
     * @param key 键
     * @param hashKey 自键
     * @return 删除数量
     */
    public long deleteKey(final String key, final String hashKey) {
        return redisTemplate.opsForHash().delete(key, hashKey);
    }

    /**
     * 获得缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public Collection<String> keys(final String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * 获得缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     * @param <T> 泛型对象
     * @return 对象列表
     */
    public <T> List<T> getCacheListObject(final String pattern) {
        Collection<String> keys = keys(pattern);
        return getCacheListObject(keys);
    }

    /**
     * 获取多个key的数据
     *
     * @param keys Redis键
     * @param <T> 泛型对象
     * @return 对象集合
     */
    public <T> List<T> getCacheListObject(final Collection<String> keys) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.multiGet(keys);
    }
    /**
     * lua 脚本
     */
    public static final String LUA_SCRIPT = "return redis.call('cl.throttle',KEYS[1], ARGV[1], ARGV[2], ARGV[3], ARGV[4])";

    /**
     * 测试
     *
     * @param key
     * @param maxBurst
     * @param countPerPeriod
     * @param period
     * @param quantity
     * @return
     */
    public boolean redisCellDemo(String key, int maxBurst, int countPerPeriod, int period, int quantity) {
        try {
            DefaultRedisScript<List> script = new DefaultRedisScript<>(LUA_SCRIPT, List.class);
            /**
             * KEYS[1]需要设置的key值，可以结合业务需要
             * ARGV[1]参数是漏斗的大小 最大爆发
             * ARGV[2]频率次数，结合ARGV[3]一起使用
             * ARGV[3]周期（秒），结合ARGV[2]一起使用
             *      最后的速率就是ARGV[2]次/ARGV[3]秒
             * ARGV[4]申请令牌数，默认是1
             */
            List<Long> rst = (List<Long>) redisTemplate.execute(script, Arrays.asList(key), maxBurst, countPerPeriod, period, quantity);

            /**
             * 1. 动作是否受限：
             *   0 表示允许该操作。
             *   1 表示该操作受到限制/阻止。
             * 2. 密钥的总限制 (max_burst+1)。这相当于常见的X-RateLimit-Limit HTTP返回头。
             * 3. 密钥的剩余限制。相当于X-RateLimit-Remaining。
             * 4. 用户应重试之前的秒数，-1如果允许操作，则始终如此。相当于Retry-After。
             * 5. 限制将重置为其最大容量之前的秒数。相当于X-RateLimit-Reset。
             */
            //这里只关注第一个元素0表示正常，1表示过载
            return rst.get(0) == 0;
        } catch (Exception e) {

            return false;
        }
    }
}