package de.feli490.utils.hytale.playerdata.json;

import de.feli490.utils.hytale.playerdata.AbstractPlayerDataLoader;
import de.feli490.utils.hytale.playerdata.pojo.PlayerData;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractJsonPlayerDataLoader extends AbstractPlayerDataLoader {

    protected final Map<UUID, PlayerData> playerData = new HashMap<>();
    protected final Map<String, PlayerData> playerDataByLastName = new HashMap<>();

    protected abstract void save(UUID playerUUID) throws IOException;

    @Override
    public PlayerData getPlayerData(UUID uuid) {
        return playerData.get(uuid);
    }

    @Override
    public PlayerData getPlayerDataByLastName(String lastName) {
        return playerDataByLastName.get(lastName);
    }

    @Override
    public boolean isKnownPlayer(UUID uuid) {
        return playerData.containsKey(uuid);
    }

    @Override
    public Collection<UUID> getAllKnownPlayerUUIDs() {
        return playerData.keySet();
    }

    @Override
    public Collection<String> getKnownPlayerNames(UUID uuid) {
        return playerDataByLastName.keySet();
    }

    @Override
    public void addPlayerNameIfUnknown(UUID playerUUID, String lastName) throws IOException {

        boolean needsSaving = handlePlayerDataIfNecessary(playerUUID, lastName);
        if (!needsSaving)
            return;

        save(playerUUID);
    }

    private boolean handlePlayerDataIfNecessary(UUID playerUUID, String name) {
        if (!playerData.containsKey(playerUUID)) {
            JsonPlayerData jsonPlayerData = JsonPlayerData.create(playerUUID, name);
            playerData.put(playerUUID, jsonPlayerData);
            playerDataByLastName.put(name, jsonPlayerData);
            return true;
        }

        String lastPlayerName = getLastPlayerName(playerUUID);
        if (lastPlayerName.equals(name))
            return false;

        JsonPlayerData jsonPlayerData = (JsonPlayerData) playerData.get(playerUUID);
        jsonPlayerData.addKnownPlayerName(name);
        playerDataByLastName.put(name, jsonPlayerData);
        return true;
    }
}
