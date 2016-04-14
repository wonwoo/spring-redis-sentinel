package me.wonwoo.controller;

import me.wonwoo.entity.Hello;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by wonwoo on 2016. 4. 14..
 */
@RestController
public class HelloController {

  @Autowired
  private CacheManager cacheManager;

  @PostMapping("/hello")
  public Hello save(){
    Hello hello = new Hello(1L, "wonwoo", "12300199","test@test.com");
    Cache cache = cacheManager.getCache("cache.hello");
    cache.put("hello", hello);
    return hello;
  }
  @GetMapping("/hello")
  public Hello getHello(){
    Cache cache = cacheManager.getCache("cache.hello");
    return cache.get("hello", Hello.class);
  }
}
