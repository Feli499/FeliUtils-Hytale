package de.feli490.utils.hytale;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.util.RawJsonReader;
import com.hypixel.hytale.logger.HytaleLogger;
import de.feli490.utils.core.sql.SQLConfig;
import de.feli490.utils.hytale.utils.FileUtils;
import java.io.IOException;
import java.nio.file.Path;

public class FeliUtilsConfig {

    public static final BuilderCodec<FeliUtilsConfig> CODEC = //
            BuilderCodec.builder(FeliUtilsConfig.class, FeliUtilsConfig::new)
                        .append(new KeyedCodec<>("StorageToUse", Codec.STRING),
                                (data, storageSetting, extraInfo) -> data.storageToUse = storageSetting,
                                (data, extraInfo) -> data.storageToUse)
                        .add()
                        .append(new KeyedCodec<>("EnableSQLProvider", Codec.BOOLEAN),
                                (data, enableSQLProvider, extraInfo) -> data.enableSQLProvider = enableSQLProvider,
                                (data, extraInfo) -> data.enableSQLProvider)
                        .add()
                        .append(new KeyedCodec<>("MySQLSettings", MySQLSettings.CODEC),
                                (data, mySqlSettings, extraInfo) -> data.mySQLSettings = mySqlSettings,
                                (data, extraInfo) -> data.mySQLSettings)
                        .add()
                        .append(new KeyedCodec<>("EnableRedisProvider", Codec.BOOLEAN),
                                (data, enableRedisProvider, extraInfo) -> data.enableRedisProvider = enableRedisProvider,
                                (data, extraInfo) -> data.enableRedisProvider)
                        .add()
                        .append(new KeyedCodec<>("RedisSettings", RedisSettings.CODEC),
                                (data, redisSettings, extraInfo) -> data.redisSettings = redisSettings,
                                (data, redisSettings) -> data.redisSettings)
                        .add()
                        .build();
    private String storageToUse;

    private boolean enableSQLProvider;
    private MySQLSettings mySQLSettings;

    private boolean enableRedisProvider;
    private RedisSettings redisSettings;

    private FeliUtilsConfig() {}

    public String getStorageToUse() {
        return storageToUse;
    }

    public boolean isEnableSQLProvider() {
        return enableSQLProvider;
    }

    public SQLConfig getSQLConfig() {
        return mySQLSettings.getSQLConfig();
    }

    public String getTableprefix() {
        return mySQLSettings.getTableprefix();
    }

    public boolean isEnableRedisProvider() {
        return enableRedisProvider;
    }

    public String getRedisConnectionString() {
        if (redisSettings == null)
            return null;
        return redisSettings.getRedisConnectionString();
    }

    public boolean useSQLStorage() {
        return storageToUse.equalsIgnoreCase("mysql");
    }

    public static FeliUtilsConfig load(Path directory, HytaleLogger logger) throws IOException {
        Path configPath = FileUtils.loadOrCreateJson(directory, "config.json", FeliUtilsConfig.class.getResourceAsStream("/config.json"));
        return RawJsonReader.readSync(configPath, FeliUtilsConfig.CODEC, logger);
    }

    private static class MySQLSettings {

        public static final BuilderCodec<MySQLSettings> CODEC = //
                BuilderCodec.builder(MySQLSettings.class, MySQLSettings::new)
                            .append(new KeyedCodec<>("Host", Codec.STRING),
                                    (mySQLSettings, host, extraInfo) -> mySQLSettings.host = host,
                                    (mySQLSettings, extraInfo) -> mySQLSettings.host)
                            .add()
                            .append(new KeyedCodec<>("Port", Codec.INTEGER),
                                    (mySQLSettings, port, extraInfo) -> mySQLSettings.port = port,
                                    (mySQLSettings, extraInfo) -> mySQLSettings.port)
                            .add()
                            .append(new KeyedCodec<>("Database", Codec.STRING),
                                    (mySQLSettings, database, extraInfo) -> mySQLSettings.database = database,
                                    (mySQLSettings, extraInfo) -> mySQLSettings.database)
                            .add()
                            .append(new KeyedCodec<>("Username", Codec.STRING),
                                    (mySQLSettings, username, extraInfo) -> mySQLSettings.username = username,
                                    (mySQLSettings, extraInfo) -> mySQLSettings.username)
                            .add()
                            .append(new KeyedCodec<>("Password", Codec.STRING),
                                    (mySQLSettings, password, extraInfo) -> mySQLSettings.password = password,
                                    (mySQLSettings, extraInfo) -> mySQLSettings.password)
                            .add()
                            .append(new KeyedCodec<>("Tableprefix", Codec.STRING),
                                    (mySQLSettings, tableprefix, extraInfo) -> mySQLSettings.tableprefix = tableprefix,
                                    (mySQLSettings, extraInfo) -> mySQLSettings.tableprefix)
                            .add()
                            .build();

        private String host;
        private int port;
        private String database;
        private String username;
        private String password;
        private String tableprefix;

        public SQLConfig getSQLConfig() {
            return new SQLConfig(host, port, database, username, password);
        }

        public String getTableprefix() {
            return tableprefix;
        }
    }

    private static class RedisSettings {

        public static final BuilderCodec<RedisSettings> CODEC = //
                BuilderCodec.builder(RedisSettings.class, RedisSettings::new)
                            .append(new KeyedCodec<>("Host", Codec.STRING),
                                    (redisSettings, host, extraInfo) -> redisSettings.host = host,
                                    (redisSettings, extraInfo) -> redisSettings.host)
                            .add()
                            .append(new KeyedCodec<>("Port", Codec.INTEGER),
                                    (redisSettings, port, extraInfo) -> redisSettings.port = port,
                                    (redisSettings, extraInfo) -> redisSettings.port)
                            .add()
                            .append(new KeyedCodec<>("Password", Codec.STRING),
                                    (redisSettings, password, extraInfo) -> redisSettings.password = password,
                                    (redisSettings, extraInfo) -> redisSettings.password)
                            .add()
                            .build();

        private String host;
        private int port;
        private String password;

        public int getPort() {
            return port;
        }

        public String getHost() {
            return host;
        }

        public String getPassword() {
            return password;
        }

        public String getRedisConnectionString() {
            return "redis://:" + getPassword() + "@" + getHost() + ":" + getPort();
        }
    }
}
