package cn.w2n0.genghiskhan.cache;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
/**
 * springCache自动配置
 * @author 无量
 * @date 2021/10/20 14:24
 */
@Configuration
@EnableCaching
public class SpringCacheAutoConfiguration {
    @Bean
    public RedisCache getRedisCache(RedisTemplate redisTemplate) {
        return new RedisCache(redisTemplate);
    }

    @SuppressWarnings("rawtypes")
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate redisTemplate = new RedisTemplate();
        RedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplate.setConnectionFactory(factory);
        redisTemplate.setKeySerializer(stringSerializer);
        return redisTemplate;
    }
}
