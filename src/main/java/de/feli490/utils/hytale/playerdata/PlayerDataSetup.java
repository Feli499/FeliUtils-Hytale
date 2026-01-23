package de.feli490.utils.hytale.playerdata;

import com.hypixel.hytale.logger.HytaleLogger;
import de.feli490.utils.hytale.playerdata.json.JsonPlayerData;
import de.feli490.utils.hytale.playerdata.json.JsonPlayerDataLoader;
import de.feli490.utils.hytale.playerdata.json.loadstrategies.MultiJsonFileStrategy;
import de.feli490.utils.hytale.playerdata.json.loadstrategies.SingleJsonFileStrategy;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

public class PlayerDataSetup {

    private final HytaleLogger logger;
    private final Path pluginDirectory;

    public PlayerDataSetup(HytaleLogger logger, Path pluginDirectory) {
        this.logger = logger;
        this.pluginDirectory = pluginDirectory;
    }

    public void setupProvider() throws IOException {

        SingleJsonFileStrategy singleJsonFileStrategy = new SingleJsonFileStrategy(logger, pluginDirectory);
        MultiJsonFileStrategy multiJsonFileStrategy = new MultiJsonFileStrategy(logger, pluginDirectory);
        if (singleJsonFileStrategy.hasData()) {
            Collection<JsonPlayerData> load = singleJsonFileStrategy.load();
            for (JsonPlayerData jsonPlayerData : load) {
                multiJsonFileStrategy.save(jsonPlayerData);
            }
            singleJsonFileStrategy.deleteData();
        }

        JsonPlayerDataLoader jsonPlayerDataLoader = new JsonPlayerDataLoader(multiJsonFileStrategy);
        PlayerDataProviderInstance.set(jsonPlayerDataLoader);
    }
}
