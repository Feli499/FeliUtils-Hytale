package de.feli490.utils.hytale;

import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import de.feli490.utils.hytale.events.PlayerReadySavePlayerDataEventListener;
import de.feli490.utils.hytale.playerdata.AbstractPlayerDataLoader;
import de.feli490.utils.hytale.playerdata.PlayerDataProvider;
import de.feli490.utils.hytale.playerdata.json.SingleFileJsonPlayerDataLoader;
import java.io.IOException;
import java.util.logging.Level;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class FeliUtilsPlugin extends JavaPlugin {

    private AbstractPlayerDataLoader playerDataLoader;

    public FeliUtilsPlugin(@NonNullDecl JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {

        try {
            playerDataLoader = new SingleFileJsonPlayerDataLoader(getLogger(), getDataDirectory());
        } catch (IOException e) {
            getLogger().at(Level.SEVERE)
                       .withCause(e)
                       .log("Failed to initialize player data loader");
        }

        getLogger().at(Level.INFO).log("FeliUtilsPlugin is setup!");
    }

    @Override
    protected void start() {

        PlayerReadySavePlayerDataEventListener playerReadySavePlayerDataEventListener = new PlayerReadySavePlayerDataEventListener(getLogger(),
                                                                                                                                   playerDataLoader);
        getEventRegistry().registerGlobal(PlayerReadyEvent.class, playerReadySavePlayerDataEventListener);

        getLogger().at(Level.INFO).log("FeliUtilsPlugin is started!");
    }

    @Override
    protected void shutdown() {
        getLogger().at(Level.INFO).log("FeliUtilsPlugin is shutdown!");
    }

    public PlayerDataProvider getPlayerDataProvider() {
        return playerDataLoader;
    }
}
