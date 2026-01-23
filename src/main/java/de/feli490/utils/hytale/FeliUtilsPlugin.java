package de.feli490.utils.hytale;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.plugin.PluginManager;
import de.feli490.utils.hytale.events.PlayerReadySavePlayerDataEventListener;
import de.feli490.utils.hytale.playerdata.PlayerDataSaver;
import de.feli490.utils.hytale.playerdata.PlayerDataSetup;
import de.feli490.utils.hytale.sql.SqlInitializer;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class FeliUtilsPlugin extends JavaPlugin {

    private PlayerDataSaver playerDataSaver;
    private SqlInitializer sqlInitializer;

    private FeliUtilsConfig config;

    public FeliUtilsPlugin(@NonNullDecl JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {

        HytaleLogger logger = getLogger();
        try {
            config = FeliUtilsConfig.load(getDataDirectory(), logger);
        } catch (IOException e) {
            logger.at(Level.SEVERE)
                  .withCause(e)
                  .log("Failed to load config.json... Shutting plugin down.");
            shutdownPlugin();
            return;
        }

        sqlInitializer = new SqlInitializer(logger, config);
        try {
            sqlInitializer.init();
        } catch (SQLException e) {
            logger.at(Level.SEVERE)
                  .withCause(e)
                  .log("Failed to initialize SQL connection... Shutting plugin down.");
            shutdownPlugin();
            return;
        }

        PlayerDataSetup playerDataSetup = new PlayerDataSetup(logger, getDataDirectory());
        try {
            playerDataSetup.setupProvider();
        } catch (IOException e) {
            logger.at(Level.SEVERE)
                  .withCause(e)
                  .log("Failed to initialize player data provider... Shutting plugin down.");
            shutdownPlugin();
            return;
        }

        logger.at(Level.INFO)
              .log("FeliUtilsPlugin is setup!");
    }

    @Override
    protected void start() {
        
        getEventRegistry().registerGlobal(PlayerReadyEvent.class, new PlayerReadySavePlayerDataEventListener(getLogger(), playerDataSaver));

        getLogger().at(Level.INFO).log("FeliUtilsPlugin is started!");
    }

    private void shutdownPlugin() {
        PluginManager.get()
                     .unload(getIdentifier());
    }

    @Override
    protected void shutdown() {

        try {
            sqlInitializer.shutdown();
        } catch (IOException e) {
            getLogger().at(Level.SEVERE)
                       .withCause(e)
                       .log("Failed to shutdown SQL connection!");
        }

        getLogger().at(Level.INFO).log("FeliUtilsPlugin is shutdown!");
    }
}
