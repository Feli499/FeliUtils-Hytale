package de.feli490.utils.hytale.redis;

import redis.clients.jedis.RedisClient;

public class RedisClientService {
    private static RedisInitializer INSTANCE;

    public static boolean hasConnection() {
        if(INSTANCE == null)
            return false;

        return INSTANCE.hasConnection();
    }

    public static RedisClient get() {
        if (!hasConnection())
            return null;
        return RedisClientService.INSTANCE.getRedisClient();
    }

    static void set(RedisInitializer sqlInitializer) {
        RedisClientService.INSTANCE = sqlInitializer;
    }
}
