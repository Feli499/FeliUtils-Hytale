package de.feli490.utils.hytale.playerdata;

import com.hypixel.hytale.logger.HytaleLogger;
import de.feli490.utils.core.common.tuple.Pair;
import de.feli490.utils.core.sql.SQLConnection;
import de.feli490.utils.hytale.playerdata.json.JsonPlayerData;
import de.feli490.utils.hytale.playerdata.json.JsonPlayerDataLoader;
import de.feli490.utils.hytale.playerdata.json.loadstrategies.MultiJsonFileStrategy;
import de.feli490.utils.hytale.playerdata.json.loadstrategies.SingleJsonFileStrategy;
import de.feli490.utils.hytale.playerdata.sql.SqlPlayerDataLoader;
import java.nio.file.Path;
import java.util.Collection;

public class PlayerDataSetup {

    private final HytaleLogger logger;
    private final Path pluginDirectory;
    private final Pair<SQLConnection, String> sqlConnection;

    public PlayerDataSetup(HytaleLogger logger, Path pluginDirectory, Pair<SQLConnection, String> sqlConnection) {
        this.logger = logger;
        this.pluginDirectory = pluginDirectory;
        this.sqlConnection = sqlConnection;
    }

    public void setupProvider() throws Exception {

        SqlPlayerDataLoader sqlPlayerDataLoader = null;
        if (sqlConnection != null) {
            sqlPlayerDataLoader = new SqlPlayerDataLoader(logger, sqlConnection.getFirst(), sqlConnection.getSecond());
        }

        SingleJsonFileStrategy singleJsonFileStrategy = new SingleJsonFileStrategy(logger, pluginDirectory);
        MultiJsonFileStrategy multiJsonFileStrategy = new MultiJsonFileStrategy(logger, pluginDirectory);
        if (singleJsonFileStrategy.hasData()) {
            Collection<JsonPlayerData> load = singleJsonFileStrategy.load();
            for (JsonPlayerData jsonPlayerData : load) {
                multiJsonFileStrategy.save(jsonPlayerData);
            }
            singleJsonFileStrategy.deleteData();
        }

        if (sqlPlayerDataLoader == null) {
            JsonPlayerDataLoader jsonPlayerDataLoader = new JsonPlayerDataLoader(multiJsonFileStrategy);
            PlayerDataProviderService.set(jsonPlayerDataLoader);
            return;
        }

        if (multiJsonFileStrategy.hasData()) {
            sqlPlayerDataLoader.migrateData(multiJsonFileStrategy.load());
            multiJsonFileStrategy.deleteData();
        }

        PlayerDataProviderService.set(sqlPlayerDataLoader);
    }
}
