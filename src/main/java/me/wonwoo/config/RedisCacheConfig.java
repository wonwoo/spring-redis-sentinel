package me.wonwoo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.wonwoo.entity.Hello;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

/**
 * Created by wonwoo on 2016. 4. 14..
 */
@Configuration
@EnableCaching(proxyTargetClass = true)
public class RedisCacheConfig {

  private static final Class CACHE_TYPE = Hello.class;
  private static final String CACHE_NAME = "cache.hello";
  private static final String CACHE_TTL = "${cache.hello.timetolive:60}";
  private static final String NETWORK_CACHE_NAME = "cache.network";

  @Value(CACHE_TTL)
  private Long cacheNetworkTimeToLive;

  @Value(CACHE_TTL)
  private Long cacheBlogTimeToLive;


  @Bean
  public RedisConnectionFactory jedisConnectionFactory() {
    RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration() .master("mymaster")
      .sentinel("127.0.0.1", 26379).sentinel("127.0.0.1", 26380).sentinel("127.0.0.1", 26381);
    return new JedisConnectionFactory(sentinelConfig);
  }

  @Bean
  public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory,
                                        ObjectMapper objectMapper) {

    RedisCacheManager cacheManager = new RedisCacheManager(redisConnectionFactory);

    cacheManager.withCache(NETWORK_CACHE_NAME, this.cacheNetworkTimeToLive);

    JsonRedisTemplate helloTemplate = new JsonRedisTemplate<>(redisConnectionFactory, objectMapper, CACHE_TYPE);
    cacheManager.withCache(CACHE_NAME, helloTemplate, this.cacheBlogTimeToLive);

    return cacheManager;
  }
}
