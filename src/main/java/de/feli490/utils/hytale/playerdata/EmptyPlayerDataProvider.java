package de.feli490.utils.hytale.playerdata;

import de.feli490.utils.hytale.playerdata.pojo.PlayerData;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class EmptyPlayerDataProvider implements PlayerDataProvider {

    @Override
    public String getLastPlayerName(UUID uuid) {
        return "";
    }

    @Override
    public PlayerData getPlayerData(UUID uuid) {
        return null;
    }

    @Override
    public boolean isKnownPlayer(UUID uuid) {
        return false;
    }

    @Override
    public UUID getPlayerUUIDByLastName(String lastName) {
        return null;
    }

    @Override
    public PlayerData getPlayerDataByLastName(String lastName) {
        return null;
    }

    @Override
    public Collection<String> getKnownPlayerNames(UUID uuid) {
        return List.of();
    }

    @Override
    public Collection<UUID> getAllKnownPlayerUUIDs() {
        return List.of();
    }
}
