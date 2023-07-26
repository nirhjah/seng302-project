package nz.ac.canterbury.seng302.tab.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Optional;


/**
 * An abstract class that Services should extend,
 * that allows for saving/loading files from the filesystem.
 */
public abstract class FileDataSaver {

    // DON'T CHANGE THESE PATH NAMES!
    private static final String TAB900_FILE_MODIFIER = "team900_seng302";

    private static final Logger logger = LoggerFactory.getLogger(FileDataSaver.class);

    private final Path initialPath;

    private final DeploymentType deploymentType;

    private Path getPath(Long id) {
        String idString = String.valueOf(id);
        return initialPath
                .resolve(idString);
    }

    /**
     * Takes the spring active profile,
     * i.e. \@Value("${spring.profiles.active}")
     * And returns the deploymentType associated with the profile.
     * If the active profile contains `prod`, then PROD deploymentType is used.
     * Else, TEST deploymentType is used.
     * @param profile the spring active profile
     * @return appropriate DeploymentType enum
     */
    public static DeploymentType getDeploymentType(String profile) {
        if (profile == null) {
            // If there is no profile, assume it's a test.
            return DeploymentType.TEST;
        }

        // This is a bit hacky, but oh well
        if (profile.contains("staging")) {
            return DeploymentType.STAGING;
        } else if (profile.contains("prod")){
            return DeploymentType.PROD;
        } else {
            return DeploymentType.TEST;
        }
    }


    /**
     * Returns a (global) path that matches the deployment type.
     * <br>
     * For example:
     * getDeploymentPath( TEST ) -> C:/users/oli/home/seng302/TEST
     * getDeploymentPath( PROD ) -> C:/users/oli/home/seng302/PROD
     * @param depType the deployment type
     * @return The global path
     */
    private static Path getDeploymentPath(DeploymentType depType) {
        return Path.of(
                System.getProperty("user.home"),
                TAB900_FILE_MODIFIER,
                depType.toString()
        );
    }

    /**
     * WARNING: Be very (very) careful when calling this.
     * This will purge the filesystem database if called incorrectly!
     * (Also it will purge your computer's filesystem if called incorrectly too)
     */
    private static boolean deleteFolder(File folder) {
        boolean success = true;
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    success &= deleteFolder(file);
                }
            }
        }
        success &= folder.delete();
        return success;
    }

    /**
     * Clears the test folder (deletes all files and nested directories)
     */
    public static void clearTestFolder() {
        Path path = getDeploymentPath(DeploymentType.TEST);

        File folder = new File(path.toUri());
        boolean ok = deleteFolder(folder);
        if (!ok)
            logger.warn("Delete folder failed!");
    }

    static {
        clearTestFolder();
    }

    public enum DeploymentType {
        PROD("PROD"),
        STAGING("STAGING"),
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
     * `folderName` should be unique in global context.
     * For example, teams prof picture saving should have a folderName like "team_imgs"
     * And all the files are saved similar to so:
     * `team_imgs_3489489389545`
     * `team_imgs_5287274389549`
     * etc.
     */
    public abstract String getFolderName();

    /**
     * Allows for specification of default bytes.
     * Think of this like a default profile picture, or a default image.
     * (If the IO operation fails, these bytes will be returned when possible)
     *
     * The reason this is not abstract, is that some FileDataSavers may not have default
     * bytes. We still want to support the optional API in that situation.
     * @return An array of bytes as the default.
     */
    public byte[] getDefaultBytes() {
        return null;
    };

    /**
     * Creates a FileDataSaver.
     * deploymentType is PROD when on production,
     * otherwise, deploymentType is TEST.
     * @param deploymentType the deploymentType
     */
    public FileDataSaver(DeploymentType deploymentType) {
        String prefix = getFolderName();

        this.deploymentType = deploymentType;
        initialPath = getDeploymentPath(deploymentType).resolve(prefix);
    }

    public DeploymentType getDeploymentType() {
        return deploymentType;
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
     * Reads bytes from a file given an id.
     * If the file doesn't exist, (or the operation fails) returns Optional.empty()
     * Else, returns an Optional of the data.
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

    /**
     * Reads bytes from a file.
     * If the file doesn't exist, or the operation fails, returns `defaultBytes`
     * @param id A unique ID (e.g. user entity primary key)
     * @return An array of bytes representing the save data
     */
    public byte[] readFileOrDefault(Long id) {
        Optional<byte[]> optBytes = readFile(id);
        if (optBytes.isPresent()) {
            // If the file exists, return the default bytes
            return optBytes.get();
        } else {
            // Else, return default bytes
            return getDefaultBytes();
        }
    }


    /**
     * Reads bytes from a file.
     * If the file doesn't exist, or the operation fails, returns `defaultBytes`
     * @param id A unique ID (e.g. user entity primary key)
     * @return An array of bytes representing the save data
     */
    public String readFileOrDefaultB64(Long id) {
        return Base64.getEncoder().encodeToString(
                readFileOrDefault(id)
        );
    }
}