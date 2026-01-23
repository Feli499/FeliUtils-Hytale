package de.feli490.utils.hytale.playerdata.json.loadstrategies;

import com.hypixel.hytale.codec.util.RawJsonReader;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.util.BsonUtil;
import de.feli490.utils.hytale.playerdata.json.JsonPlayerData;
import de.feli490.utils.hytale.utils.FileUtils;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MultiJsonFileStrategy implements JsonFileStrategy {

    private final HytaleLogger logger;
    private final Path directory;

    public MultiJsonFileStrategy(HytaleLogger logger, Path directory) {
        this.logger = logger;
        this.directory = directory.resolve("playerdata");
    }

    @Override
    public void deleteData() throws IOException {
        FileUtils.deleteDirectoryRecursive(directory);
    }

    @Override
    public Collection<JsonPlayerData> load() throws IOException {

        DirectoryStream<Path> paths = Files.newDirectoryStream(directory, "*.json");

        Set<JsonPlayerData> jsonPlayerDataSet = new HashSet<>();
        for (Path path : paths) {
            JsonPlayerData jsonPlayerData = RawJsonReader.readSync(path, JsonPlayerData.CODEC, logger);
            jsonPlayerDataSet.add(jsonPlayerData);
        }

        return jsonPlayerDataSet;
    }

    @Override
    public boolean hasData() {
        return Files.exists(directory);
    }

    @Override
    public void save(JsonPlayerData jsonPlayerData) throws IOException {
        Path path = FileUtils.loadOrCreateEmptyJson(directory.resolve(jsonPlayerData.uuid() + ".json"));
        BsonUtil.writeSync(path, JsonPlayerData.CODEC, jsonPlayerData, logger);
    }
}
