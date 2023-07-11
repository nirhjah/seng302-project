package nz.ac.canterbury.seng302.tab.helper;

import jakarta.validation.constraints.NotNull;

import java.io.*;
import java.util.*;

import java.nio.file.*;




public class FileDataSaver {

    private static final Set<String> usedModifiers = new HashSet<>();

    /**
     * `prefix` is like the prefix before all files.
     * For example, teams have `prefix = "team"`
     * And all the files are saved like:
     *  `team_34894895`
     *  `team_3287274389549`
     *  etc.
     */
    private final String prefix;

    public FileDataSaver(String prefix) {
        if (!usedModifiers.add(prefix)) {
            throw new RuntimeException("Duplicate modifier name: " + prefix);
        }
        this.prefix = prefix;
    }


    // DON'T CHANGE THESE PATH NAMES!
    private static final String TAB900_FILE_MODIFIER = "team900_seng302";
    private static final String IMAGE_FILE_PATHS = "images";

    private final Path imagePath = Path.of(
            System.getProperty("user.home"),
            TAB900_FILE_MODIFIER,
            IMAGE_FILE_PATHS
    );

    private Path getPath(Long id, @NotNull String modifier) {
        String idString = String.valueOf(id);
        return imagePath
                .resolve(modifier)
                .resolve(idString);
    }


    /**
     * Saves bytes to a file, and returns true on success, false on failure.
     * @param id A unique ID for the context (e.g. user entity primary key)
     * @param data Bytes of data to save
     * @return true on success, false on failure
     */
    public boolean saveFile(Long id, byte[] data) {
        Path fullPath = getPath(id, prefix);
        try {
            Files.createDirectories(fullPath.getParent());
        } catch (IOException ex) {
            return false;
        }

        File newFile = new File(fullPath.toString());

        try (FileOutputStream writer = new FileOutputStream(newFile)) {
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Reads bytes from a file, (returns Optional.empty() on failure).
     * @param id A unique ID (e.g. user entity primary key)
     * @return Optional.empty() if operation failed, else, Optional.of() containing the bytes.
     */
    public Optional<byte[]> readFile(Long id) {
        Path fullPath = getPath(id, prefix);
        try {
            var bytes = Files.readAllBytes(fullPath);
            return Optional.of(bytes);
        } catch (IOException ex) {
            return Optional.empty();
        }
    }
}
