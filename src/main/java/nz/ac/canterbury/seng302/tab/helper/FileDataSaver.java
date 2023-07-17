package nz.ac.canterbury.seng302.tab.helper;

import org.springframework.security.core.parameters.P;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


public class FileDataSaver {
    private static final Set<String> usedModifiers = new HashSet<>();

    // DON'T CHANGE THESE PATH NAMES!
    private static final String TAB900_FILE_MODIFIER = "team900_seng302";

    private final Path initialPath;

    private Path getPath(Long id) {
        String idString = String.valueOf(id);
        return initialPath
                .resolve(idString);
    }

    public enum DeploymentType {
        PROD("PROD"),
        TEST("TEST");

        private final String value;

        DeploymentType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }

    /**
     * `prefix` is just the prefix before all files.
     * For example, teams prof picture saving should have a prefix like "team_imgs"
     * And all the files are saved similar to so:
     *  `team_imgs_3489489389545`
     *  `team_imgs_5287274389549`
     *  etc.
     */
    public FileDataSaver(String prefix, DeploymentType deploymentType) {
        if (!usedModifiers.add(prefix)) {
            throw new RuntimeException("Duplicate modifier name: " + prefix);
        }

        initialPath = Path.of(
                System.getProperty("user.home"),
                TAB900_FILE_MODIFIER,
                deploymentType.toString(),
                prefix
        );
    }

    /**
     * Saves bytes to a file, and returns true on success, false on failure.
     * @param id A unique ID for the context (e.g. user entity primary key)
     * @param data Bytes of data to save
     * @return true on success, false on failure
     */
    public boolean saveFile(Long id, byte[] data) {
        Path fullPath = getPath(id);
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
        Path fullPath = getPath(id);
        try {
            var bytes = Files.readAllBytes(fullPath);
            return Optional.of(bytes);
        } catch (IOException ex) {
            return Optional.empty();
        }
    }
}
