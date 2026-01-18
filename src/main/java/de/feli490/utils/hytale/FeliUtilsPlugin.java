package de.feli490.utils.hytale;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import java.util.logging.Level;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class FeliUtilsPlugin extends JavaPlugin {

    public FeliUtilsPlugin(@NonNullDecl JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        getLogger().at(Level.INFO).log("FeliUtilsPlugin is setup!");
    }

    @Override
    protected void start() {
        getLogger().at(Level.INFO).log("FeliUtilsPlugin is started!");
    }

    @Override
    protected void shutdown() {
        getLogger().at(Level.INFO).log("FeliUtilsPlugin is shutdown!");
    }
}
