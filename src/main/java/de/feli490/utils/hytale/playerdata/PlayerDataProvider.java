package de.feli490.utils.hytale.playerdata;

import de.feli490.utils.hytale.playerdata.pojo.PlayerData;
import java.util.Collection;
import java.util.UUID;

public interface PlayerDataProvider {

    String getLastPlayerName(UUID uuid);

    PlayerData getPlayerData(UUID uuid);

    boolean isKnownPlayer(UUID uuid);

    UUID getPlayerUUIDByLastName(String lastName);

    PlayerData getPlayerDataByLastName(String lastName);

    Collection<String> getKnownPlayerNames(UUID uuid);

    Collection<UUID> getAllKnownPlayerUUIDs();
}
