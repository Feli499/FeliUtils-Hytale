package de.feli490.utils.hytale.playerdata;

import de.feli490.utils.hytale.playerdata.pojo.PlayerData;
import java.util.UUID;

public abstract class AbstractPlayerDataLoader implements PlayerDataProvider, PlayerDataSaver {

    @Override
    public String getLastPlayerName(UUID uuid) {
        PlayerData playerData = getPlayerData(uuid);
        if (playerData == null)
            return null;

        return playerData.getLastKnownUsername()
                         .getName();
    }

    @Override
    public UUID getPlayerUUIDByLastName(String lastName) {

        PlayerData playerDataByLastName = getPlayerDataByLastName(lastName);
        if (playerDataByLastName == null)
            return null;

        return playerDataByLastName.getUuid();
    }
}
