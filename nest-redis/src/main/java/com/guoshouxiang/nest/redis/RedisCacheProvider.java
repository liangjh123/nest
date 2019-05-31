package com.guoshouxiang.nest.redis;

import com.guoshouxiang.nest.cache.provider.CacheProvider;
import com.guoshouxiang.nest.utils.JsonUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RedisCacheProvider implements CacheProvider {
    public final static String CODE = "REDIS_CACHE_PROVIDER";

    private JedisPool jedisPool;

    public RedisCacheProvider(JedisPoolConfig jedisPoolConfig) {
        jedisPool = new JedisPool(jedisPoolConfig);
    }

    @Override
    public String getCode() {
        return CODE;
    }

    @Override
    public <T> T get(String groupName, String key, Class<T> clazz) {
        Jedis jedis = jedisPool.getResource();
        String json = jedis.hget(groupName, key);
        T result = JsonUtils.toObj(json, clazz);
        jedis.close();

        return result;
    }

    @Override
    public <T> Map<String, T> get(String groupName, Class<T> clazz, String... keys) {
        Map<String, T> result = new HashMap<>();

        Jedis jedis = jedisPool.getResource();
        for (String key : keys) {
            String json = jedis.hget(groupName, key);
            T obj = JsonUtils.toObj(json, clazz);
            result.put(key, obj);
        }
        jedis.close();

        return result;
    }

    @Override
    public void put(String groupName, String key, Object value, long idleSeconds) {

        Jedis jedis = jedisPool.getResource();
        String json = JsonUtils.toJsonString(value);
        jedis.hset(groupName, key, json);
        jedis.close();
    }

    @Override
    public boolean remove(String groupName, String key) {
        Jedis jedis = jedisPool.getResource();
        jedis.hdel(groupName, key);
        jedis.close();

        return true;
    }

    @Override
    public void removeAll(String groupName) {
        Jedis jedis = jedisPool.getResource();
        jedis.del(groupName);
        jedis.close();

    }

    @Override
    public boolean containsKey(String groupName, String key) {
        Jedis jedis = jedisPool.getResource();
        Boolean result = jedis.hexists(groupName, key);
        jedis.close();

        return result;
    }

    @Override
    public String[] getKeys(String groupName) {
        Jedis jedis = jedisPool.getResource();
        String[] keys = jedis.hkeys(groupName).toArray(new String[]{});
        jedis.close();
        return keys;
    }
}
