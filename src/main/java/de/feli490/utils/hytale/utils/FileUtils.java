package de.feli490.utils.hytale.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {

    public static final String EMPTY_JSON = "{}";
    public static final String EMPTY_ARRAY = "[]";

    private FileUtils() {}

    public static Path loadOrCreateEmptyJson(Path directory, String fileName) throws IOException {
        Path filePath = directory.resolve(fileName);
        return loadOrCreateEmptyJson(filePath);
    }

    public static Path loadOrCreateEmptyArray(Path directory, String fileName) throws IOException {
        Path filePath = directory.resolve(fileName);
        return loadOrCreateEmptyArray(filePath);
    }

    public static Path loadOrCreateJson(Path filePath, InputStream defaultContent) throws IOException {
        if (Files.exists(filePath)) {
            return filePath;
        }
        String content = new String(defaultContent.readAllBytes());
        defaultContent.close();
        return loadOrCreate(filePath, content);
    }

    public static void deleteDirectoryRecursive(Path directory) throws IOException {

        try (DirectoryStream<Path> paths = Files.newDirectoryStream(directory)) {
            for (Path path : paths) {
                if (Files.isDirectory(path)) {
                    deleteDirectoryRecursive(path);
                }
                Files.deleteIfExists(path);
            }
        }
        Files.deleteIfExists(directory);
    }

    public static Path loadOrCreateJson(Path directory, String fileName, InputStream defaultContent) throws IOException {
        return loadOrCreateJson(directory.resolve(fileName), defaultContent);
    }

    public static Path loadOrCreateEmptyJson(Path path) throws IOException {
        return loadOrCreate(path, EMPTY_JSON);
    }

    public static Path loadOrCreateEmptyArray(Path path) throws IOException {
        return loadOrCreate(path, EMPTY_ARRAY);
    }

    public static Path loadOrCreate(Path path) throws IOException {
        return loadOrCreate(path, "");
    }

    public static Path loadOrCreate(Path path, String content) throws IOException {
        var file = path.toFile();

        if (file.isDirectory())
            throw new IllegalArgumentException("Path is not a JSON-file: " + path);

        checkDirectoryExistsOrCreate(path.getParent());

        if (!file.exists()) {
            file.createNewFile();
            if (!content.isBlank())
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(content);
                }
        }
        return file.toPath();
    }

    private static void checkDirectoryExistsOrCreate(Path parent) throws IOException {

        if (Files.exists(parent))
            return;

        Files.createDirectories(parent);
    }
}
