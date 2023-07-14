package nz.ac.canterbury.seng302.tab.helper;

import jakarta.validation.constraints.NotNull;

import java.io.*;
import java.util.*;

import java.nio.file.*;




public class FileDataSaver {


    /**
     * `prefix` is like the prefix before all files.
     * For example, teams have `prefix = "team"`
     * And all the files are saved like:
     *  `team_34894895`
     *  `team_3287274389549`
     *  etc.
     */
    private final Path addPath;

    private static final Set<String> usedModifiers = new HashSet<>();

    public FileDataSaver(String prefix, String deploymentType) {
        if (!usedModifiers.add(prefix)) {
            throw new RuntimeException("Duplicate modifier name: " + prefix);
        }
        addPath = Path.of(
                deploymentType,
                prefix
        );
    }


    // DON'T CHANGE THESE PATH NAMES!
    private static final String TAB900_FILE_MODIFIER = "team900_seng302";
    private static final String IMAGE_FILE_PATHS = "images";

    private final Path imagePath = Path.of(
            System.getProperty("user.home"),
            TAB900_FILE_MODIFIER,
            IMAGE_FILE_PATHS
    );

    private Path getPath(Long id, Path addPath) {
        String idString = String.valueOf(id);
        return imagePath
                .resolve(addPath)
                .resolve(idString);
    }

    /**
     * Saves bytes to a file, and returns true on success, false on failure.
     * @param id A unique ID for the context (e.g. user entity primary key)
     * @param data Bytes of data to save
     * @return true on success, false on failure
     */
    public boolean saveFile(Long id, byte[] data) {
        Path fullPath = getPath(id, addPath);
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
        Path fullPath = getPath(id, addPath);
        try {
            var bytes = Files.readAllBytes(fullPath);
            return Optional.of(bytes);
        } catch (IOException ex) {
            return Optional.empty();
        }
    }
}
