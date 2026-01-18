package de.feli490.utils.hytale.playerdata;

import java.util.UUID;

public interface PlayerDataSaver {

    public void savePlayerData(UUID playerUUID, String lastName);
}
