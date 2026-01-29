package de.feli490.utils.hytale.redis;

import com.hypixel.hytale.logger.HytaleLogger;
import de.feli490.utils.hytale.FeliUtilsConfig;
import redis.clients.jedis.RedisClient;
import redis.clients.jedis.exceptions.JedisException;

public class RedisInitializer {

    private final HytaleLogger hytaleLogger;
    private final FeliUtilsConfig config;
    private RedisClient redisClient;

    public RedisInitializer(HytaleLogger hytaleLogger, FeliUtilsConfig config) {
        this.hytaleLogger = hytaleLogger.getSubLogger("RedisInitializer");
        this.config = config;
    }



    public void init() {
        RedisClientService.set(this);
        reconnect();
    }

    public void reconnect() {

        if(redisClient != null) {
            hytaleLogger.atInfo().log("Closing Redis connection due to reconnection.");
            redisClient.close();
        }

        String redisConnectionString = config.getRedisConnectionString();
        if (redisConnectionString == null) {
            return;
        }

        try {
            redisClient = RedisClient.create(redisConnectionString);
        }catch (JedisException e) {
            hytaleLogger.atSevere().withCause(e).log("Could not connect to Redis.");
        }
    }

    public void shutdown() {

        if (redisClient == null)
            return;

        redisClient.close();
    }

    public RedisClient getRedisClient() {
        return redisClient;
    }

    public boolean hasConnection() {

        if(redisClient == null)
            return false;

        return redisClient.ping().equals("PONG");
    }
}
