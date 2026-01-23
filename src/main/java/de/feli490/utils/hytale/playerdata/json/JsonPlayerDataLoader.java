package de.feli490.utils.hytale.playerdata.json;

import de.feli490.utils.core.common.tuple.Pair;
import de.feli490.utils.hytale.playerdata.AbstractPlayerDataLoader;
import de.feli490.utils.hytale.playerdata.json.loadstrategies.JsonFileStrategy;
import de.feli490.utils.hytale.playerdata.pojo.PlayerData;
import de.feli490.utils.hytale.playerdata.pojo.UsedNameData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class JsonPlayerDataLoader extends AbstractPlayerDataLoader {

    protected final Map<UUID, JsonPlayerData> playerData = new HashMap<>();
    protected final Map<String, JsonPlayerData> playerDataByLastName = new HashMap<>();
    private final JsonFileStrategy jsonFileStrategy;

    public JsonPlayerDataLoader(JsonFileStrategy jsonFileStrategy) throws IOException {
        this.jsonFileStrategy = Objects.requireNonNull(jsonFileStrategy);
        reloadData();
    }

    public void reloadData() throws IOException {

        Collection<JsonPlayerData> jsonPlayerData = jsonFileStrategy.load();
        if (jsonPlayerData.isEmpty())
            return;

        for (JsonPlayerData jsonPlayerDatum : jsonPlayerData) {
            playerData.put(jsonPlayerDatum.uuid(), jsonPlayerDatum);
        }

        Map<String, List<Pair<Long, JsonPlayerData>>> lastKnownNames = new HashMap<>();
        for (JsonPlayerData jsonPlayerDatum : jsonPlayerData)
            for (UsedNameData usedNameData : jsonPlayerDatum.usedNames()) {

                String name = usedNameData.name();
                if (!lastKnownNames.containsKey(name))
                    lastKnownNames.put(name, new ArrayList<>());

                lastKnownNames.get(name)
                              .add(new Pair<>(usedNameData.firstSeen(), jsonPlayerDatum));
            }

        for (Map.Entry<String, List<Pair<Long, JsonPlayerData>>> entry : lastKnownNames.entrySet()) {
            String name = entry.getKey();
            List<Pair<Long, JsonPlayerData>> value = entry.getValue();

            value.sort((o1, o2) -> Long.compare(o2.getFirst(), o1.getFirst()));
            Pair<Long, JsonPlayerData> first = value.getFirst();
            playerDataByLastName.put(name, first.getSecond());
        }
    }

    private void save(UUID playerUUID) throws IOException {
        JsonPlayerData playerData = this.playerData.get(playerUUID);
        jsonFileStrategy.save(playerData);
    }

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
