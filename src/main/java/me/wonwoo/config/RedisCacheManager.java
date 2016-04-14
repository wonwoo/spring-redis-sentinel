package me.wonwoo.config;

import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;
import org.springframework.data.redis.cache.DefaultRedisCachePrefix;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCachePrefix;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by wonwoo on 2016. 4. 14..
 */
public class RedisCacheManager extends AbstractCacheManager {


  private final RedisConnectionFactory redisConnectionFactory;
  private final RedisTemplate defaultTemplate;
  private final Map<String, RedisTemplate> templates;

  private boolean usePrefix = true;
  private RedisCachePrefix cachePrefix = new DefaultRedisCachePrefix();
  private boolean dynamic = false;

  // 0 - never expire
  private long defaultExpiration = 0;
  private final Map<String, Long> expires;

  public RedisCacheManager(RedisConnectionFactory redisConnectionFactory) {
    this.redisConnectionFactory = redisConnectionFactory;
    this.defaultTemplate = new RedisTemplate();
    this.defaultTemplate.setConnectionFactory(this.redisConnectionFactory);
    this.defaultTemplate.afterPropertiesSet();
    this.templates = new ConcurrentHashMap<>();
    this.expires = new ConcurrentHashMap<>();
  }

  public void setUsePrefix(boolean usePrefix) {
    this.usePrefix = usePrefix;
  }

  public void setCachePrefix(RedisCachePrefix cachePrefix) {
    this.cachePrefix = cachePrefix;
  }

  public void setDynamic(boolean dynamic) {
    this.dynamic = dynamic;
  }

  public void setDefaultExpiration(long defaultExpiration) {
    this.defaultExpiration = defaultExpiration;
  }

  public RedisCacheManager withCache(String cacheName, long expiration) {
    return withCache(cacheName, this.defaultTemplate, expiration);
  }

  public RedisCacheManager withCache(String cacheName, RedisTemplate template, long expiration) {
    this.templates.put(cacheName, template);
    this.expires.put(cacheName, expiration);
    RedisCache cache = createCache(cacheName, template, expiration);
    addCache(cache);
    return this;
  }

  @Override
  public Cache getCache(String name) {
    Cache cache = super.getCache(name);
    if (cache == null && this.dynamic) {
      return createCache(name, this.defaultTemplate, this.defaultExpiration);
    }
    return cache;
  }

  protected RedisCache createCache(String cacheName, RedisTemplate template, long expiration) {
    return new RedisCache(cacheName, (usePrefix ? cachePrefix.prefix(cacheName) : null), template, expiration);
  }

  @Override
  protected Collection<? extends Cache> loadCaches() {
    Assert.notNull(this.defaultTemplate, "A redis template is required in order to interact with data store");
    return this.getCacheNames().stream().map(name -> getCache(name)).collect(Collectors.toList());
  }
}