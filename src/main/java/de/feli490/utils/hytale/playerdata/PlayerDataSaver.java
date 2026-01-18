package de.feli490.utils.hytale.playerdata;

import java.io.IOException;
import java.util.UUID;

public interface PlayerDataSaver {

    void savePlayerData(UUID playerUUID, String name) throws IOException;
}
