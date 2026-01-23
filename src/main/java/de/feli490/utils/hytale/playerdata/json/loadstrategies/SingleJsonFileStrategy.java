package de.feli490.utils.hytale.playerdata.json.loadstrategies;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.util.RawJsonReader;
import com.hypixel.hytale.logger.HytaleLogger;
import de.feli490.utils.hytale.playerdata.json.JsonPlayerData;
import de.feli490.utils.hytale.playerdata.pojo.PlayerData;
import de.feli490.utils.hytale.utils.FileUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SingleJsonFileStrategy implements JsonFileStrategy {

    private final HytaleLogger logger;
    private final Path playerDataJson;

    public SingleJsonFileStrategy(HytaleLogger logger, Path filePath) throws IOException {
        this.logger = logger.getSubLogger("SingleFileJsonLoadStrategy");
        playerDataJson = FileUtils.loadOrCreateEmptyJson(filePath, "playerdata.json");
    }

    @Override
    public void deleteData() throws IOException {
        Files.delete(playerDataJson);
    }

    @Override
    public Collection<JsonPlayerData> load() throws IOException {

        JsonPlayerDataHolder jsonPlayerDataHolder = RawJsonReader.readSync(playerDataJson, JsonPlayerDataHolder.CODEC, logger);
        if (jsonPlayerDataHolder == null)
            return Collections.emptyList();

        JsonPlayerData[] jsonPlayerData = jsonPlayerDataHolder.getJsonPlayerDataArray();
        if (jsonPlayerData == null)
            return Collections.emptyList();

        return List.of(jsonPlayerData);
    }

    @Override
    public boolean hasData() {
        return Files.exists(playerDataJson);
    }

    @Override
    public void save(JsonPlayerData jsonPlayerData) throws IOException {
        throw new UnsupportedOperationException("Saving to single file is no longer supported.");
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
