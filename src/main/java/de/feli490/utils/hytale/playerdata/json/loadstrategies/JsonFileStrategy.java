package de.feli490.utils.hytale.playerdata.json.loadstrategies;

import de.feli490.utils.hytale.playerdata.json.JsonPlayerData;
import java.io.IOException;
import java.util.Collection;

public interface JsonFileStrategy {

    Collection<JsonPlayerData> load() throws IOException;

    boolean hasData();

    void save(JsonPlayerData jsonPlayerData) throws IOException;
}
