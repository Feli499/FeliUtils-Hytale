package de.feli490.utils.hytale;

import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import de.feli490.utils.hytale.events.PlayerReadySavePlayerDataEventListener;
import de.feli490.utils.hytale.playerdata.PlayerDataSaver;
import de.feli490.utils.hytale.playerdata.json.SingleFileJsonPlayerDataLoader;
import java.io.IOException;
import java.util.logging.Level;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class FeliUtilsPlugin extends JavaPlugin {

    private PlayerDataSaver playerDataSaver;

    public FeliUtilsPlugin(@NonNullDecl JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {

        try {

            SingleFileJsonPlayerDataLoader playerDataProvider = new SingleFileJsonPlayerDataLoader(getLogger(), getDataDirectory());
            PlayerDataProviderInstance.set(playerDataProvider);
            playerDataSaver = playerDataProvider;

        } catch (IOException e) {
            getLogger().at(Level.SEVERE)
                       .withCause(e)
                       .log("Failed to initialize player data loader");
        }

        getLogger().at(Level.INFO).log("FeliUtilsPlugin is setup!");
    }

    @Override
    protected void start() {
        
        getEventRegistry().registerGlobal(PlayerReadyEvent.class, new PlayerReadySavePlayerDataEventListener(getLogger(), playerDataSaver));

        getLogger().at(Level.INFO).log("FeliUtilsPlugin is started!");
    }

    @Override
    protected void shutdown() {
        getLogger().at(Level.INFO).log("FeliUtilsPlugin is shutdown!");
    }
}
