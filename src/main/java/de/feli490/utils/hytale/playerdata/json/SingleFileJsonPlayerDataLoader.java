package de.feli490.utils.hytale.playerdata.json;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.util.RawJsonReader;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.util.BsonUtil;
import de.feli490.utils.core.common.tuple.Pair;
import de.feli490.utils.hytale.playerdata.AbstractPlayerDataLoader;
import de.feli490.utils.hytale.utils.FileUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SingleFileJsonPlayerDataLoader extends AbstractPlayerDataLoader {

    private final HytaleLogger logger;
    private final Path playerDataJson;

    private Map<UUID, JsonPlayerData> playerData;
    private Map<String, JsonPlayerData> playerDataByLastName;

    public SingleFileJsonPlayerDataLoader(HytaleLogger logger, Path filePath) throws IOException {

        this.logger = logger;
        playerDataJson = FileUtils.loadOrCreateEmptyJson(filePath, "playerdata.json");
        playerData = new HashMap<>();
        playerDataByLastName = new HashMap<>();

        JsonPlayerDataHolder jsonPlayerDataHolder = RawJsonReader.readSync(playerDataJson, JsonPlayerDataHolder.CODEC, logger);
        JsonPlayerData[] jsonPlayerData = jsonPlayerDataHolder.getJsonPlayerDataArray();
        if (jsonPlayerData == null)
            return;

        for (JsonPlayerData jsonPlayerDatum : jsonPlayerData) {
            playerData.put(jsonPlayerDatum.getUuid(), jsonPlayerDatum);
        }

        Map<String, List<Pair<Long, JsonPlayerData>>> lastKnownNames = new HashMap<>();
        for (JsonPlayerData jsonPlayerDatum : jsonPlayerData)
            for (JsonPlayerData.KnownPlayerName knownPlayerName : jsonPlayerDatum.getKnownPlayerNames()) {

                String name = knownPlayerName.getName();
                if(!lastKnownNames.containsKey(name))
                    lastKnownNames.put(name, new ArrayList<>());

                lastKnownNames.get(name).add(new Pair<>(knownPlayerName.getFirstSeen(), jsonPlayerDatum));
            }

        for (Map.Entry<String, List<Pair<Long, JsonPlayerData>>> entry : lastKnownNames.entrySet()) {
            String name = entry.getKey();
            List<Pair<Long, JsonPlayerData>> value = entry.getValue();

            value.sort((o1, o2) -> Long.compare(o2.getFirst(), o1.getFirst()));
            Pair<Long, JsonPlayerData> first = value.getFirst();
            playerDataByLastName.put(name, first.getSecond());
        }
    }

    private void save() throws IOException {
        BsonUtil.writeSync(playerDataJson, JsonPlayerDataHolder.CODEC, JsonPlayerDataHolder.create(playerData.values()), logger);
    }

    @Override
    public String getLastPlayerName(UUID uuid) {
        return playerData.get(uuid).getLastKnownName().getName();
    }

    @Override
    public UUID getPlayerUUIDByLastName(String lastName) {
        return playerDataByLastName.get(lastName).getUuid();
    }

    @Override
    public Collection<String> getKnownPlayerNames(UUID uuid) {
        return playerData.get(uuid).getKnownPlayerNames().stream().map(JsonPlayerData.KnownPlayerName::getName).toList();
    }

    @Override
    public void savePlayerData(UUID playerUUID, String lastName) throws IOException {

        boolean needsSaving = handlePlayerDataIfNecessary(playerUUID, lastName);
        if(!needsSaving)
            return;

        save();
    }

    private boolean handlePlayerDataIfNecessary(UUID playerUUID, String name) {
        if(!playerData.containsKey(playerUUID)){
            JsonPlayerData jsonPlayerData = JsonPlayerData.create(playerUUID, name);
            playerData.put(playerUUID, jsonPlayerData);
            playerDataByLastName.put(name, jsonPlayerData);
            return true;
        }

        String lastPlayerName = getLastPlayerName(playerUUID);
        if(lastPlayerName.equals(name))
            return false;

        JsonPlayerData jsonPlayerData = playerData.get(playerUUID);
        jsonPlayerData.addKnownPlayerName(name);
        playerDataByLastName.put(name, jsonPlayerData);
        return true;
    }

    private static class JsonPlayerDataHolder {

        public static final ArrayCodec<JsonPlayerData> ARRAY_CODEC = new ArrayCodec<>(JsonPlayerData.CODEC,
                                                                                      JsonPlayerData[]::new,
                                                                                      JsonPlayerData::new);
        public static final BuilderCodec<JsonPlayerDataHolder> CODEC = //
                BuilderCodec.builder(JsonPlayerDataHolder.class, JsonPlayerDataHolder::new)
                            .append(new KeyedCodec<>("Data", JsonPlayerDataHolder.ARRAY_CODEC),
                                    (jsonPlayerDataHolder, array, extraInfo) -> jsonPlayerDataHolder.jsonPlayerDataArray = array,
                                    (jsonPlayerDataHolder, extraInfo) -> jsonPlayerDataHolder.getJsonPlayerDataArray())
                            .add()
                            .build();

        private JsonPlayerData[] jsonPlayerDataArray;

        public JsonPlayerData[] getJsonPlayerDataArray() {
            return jsonPlayerDataArray;
        }

        public static JsonPlayerDataHolder create(Collection<JsonPlayerData> jsonPlayerDataCollection) {
            JsonPlayerDataHolder jsonPlayerDataHolder = new JsonPlayerDataHolder();
            jsonPlayerDataHolder.jsonPlayerDataArray = jsonPlayerDataCollection.toArray(new JsonPlayerData[0]);
            return jsonPlayerDataHolder;
        }
    }
}
