package de.feli490.utils.hytale.playerdata.json;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.util.RawJsonReader;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.util.BsonUtil;
import de.feli490.utils.core.common.tuple.Pair;
import de.feli490.utils.hytale.playerdata.pojo.PlayerData;
import de.feli490.utils.hytale.playerdata.pojo.UsedNameData;
import de.feli490.utils.hytale.utils.FileUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SingleFileJsonPlayerDataLoader extends AbstractJsonPlayerDataLoader {

    private final HytaleLogger logger;
    private final Path playerDataJson;


    public SingleFileJsonPlayerDataLoader(HytaleLogger logger, Path filePath) throws IOException {

        this.logger = logger;
        playerDataJson = FileUtils.loadOrCreateEmptyJson(filePath, "playerdata.json");

        JsonPlayerDataHolder jsonPlayerDataHolder = RawJsonReader.readSync(playerDataJson, JsonPlayerDataHolder.CODEC, logger);
        JsonPlayerData[] jsonPlayerData = jsonPlayerDataHolder.getJsonPlayerDataArray();
        if (jsonPlayerData == null)
            return;

        for (JsonPlayerData jsonPlayerDatum : jsonPlayerData) {
            playerData.put(jsonPlayerDatum.getUuid(), jsonPlayerDatum);
        }

        Map<String, List<Pair<Long, JsonPlayerData>>> lastKnownNames = new HashMap<>();
        for (JsonPlayerData jsonPlayerDatum : jsonPlayerData)
            for (UsedNameData usedNameData : jsonPlayerDatum.getUsedNames()) {

                String name = usedNameData.getName();
                if(!lastKnownNames.containsKey(name))
                    lastKnownNames.put(name, new ArrayList<>());

                lastKnownNames.get(name)
                              .add(new Pair<>(usedNameData.getFirstSeen(), jsonPlayerDatum));
            }

        for (Map.Entry<String, List<Pair<Long, JsonPlayerData>>> entry : lastKnownNames.entrySet()) {
            String name = entry.getKey();
            List<Pair<Long, JsonPlayerData>> value = entry.getValue();

            value.sort((o1, o2) -> Long.compare(o2.getFirst(), o1.getFirst()));
            Pair<Long, JsonPlayerData> first = value.getFirst();
            playerDataByLastName.put(name, first.getSecond());
        }
    }

    @Override
    protected void save(UUID playerUUID) throws IOException {
        BsonUtil.writeSync(playerDataJson, JsonPlayerDataHolder.CODEC, JsonPlayerDataHolder.create(playerData.values()), logger);
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

        public static JsonPlayerDataHolder create(Collection<PlayerData> playerData) {

            JsonPlayerData[] jsonPlayerData = new JsonPlayerData[playerData.size()];
            int i = 0;
            for (PlayerData cachedPlayerDatum : playerData) {
                jsonPlayerData[i++] = (JsonPlayerData) cachedPlayerDatum;
            }

            JsonPlayerDataHolder jsonPlayerDataHolder = new JsonPlayerDataHolder();
            jsonPlayerDataHolder.jsonPlayerDataArray = jsonPlayerData;
            return jsonPlayerDataHolder;
        }
    }
}
