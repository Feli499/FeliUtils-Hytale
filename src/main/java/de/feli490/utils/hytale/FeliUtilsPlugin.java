package de.feli490.utils.hytale;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.plugin.PluginManager;
import de.feli490.utils.core.common.tuple.Pair;
import de.feli490.utils.core.sql.SQLConnection;
import de.feli490.utils.hytale.commands.PlayerInfoCommand;
import de.feli490.utils.hytale.events.PlayerReadySavePlayerDataEventListener;
import de.feli490.utils.hytale.message.MessageBuilderFactory;
import de.feli490.utils.hytale.playerdata.PlayerDataProviderService;
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
    private MessageBuilderFactory messageBuilderFactory;

    public FeliUtilsPlugin(@NonNullDecl JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {

        this.messageBuilderFactory = new MessageBuilderFactory("#00ff00", "#ffffff");

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

        Pair<SQLConnection, String> sqlConnectionPair = null;
        if (sqlInitializer.hasConnection() && config.useSQLStorage())
            sqlConnectionPair = new Pair<>(sqlInitializer.loadConnection(), config.getTableprefix());

        PlayerDataSetup playerDataSetup = new PlayerDataSetup(logger, getDataDirectory(), sqlConnectionPair);
        try {
            playerDataSaver = playerDataSetup.setupProvider();
        } catch (Exception e) {
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

        getCommandRegistry().registerCommand(new PlayerInfoCommand(messageBuilderFactory, PlayerDataProviderService.get()));

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
