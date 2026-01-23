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
                        .build();
    private String storageToUse;
    private boolean enableSQLProvider;
    private MySQLSettings mySQLSettings;

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
}
