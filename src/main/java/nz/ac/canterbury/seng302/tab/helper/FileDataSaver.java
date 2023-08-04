package nz.ac.canterbury.seng302.tab.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


/**
 * An abstract class that Services should extend,
 * that allows for saving/loading files from the filesystem.
 */
public abstract class FileDataSaver {

    private static final String TAB900_FILE_MODIFIER = "team900_seng302";

    private static final Logger logger = LoggerFactory.getLogger(FileDataSaver.class);

    private final Path initialPath;

    private final DeploymentType deploymentType;

    private final FileRestrictions fileRestrictions;

    public static final FileRestrictions DEFAULT_IMAGE_RESTRICTIONS = new FileRestrictions(
            10_000_000, Set.of("jpg", "png", "jpeg", "svg")
    );

    /**
     * Takes a long id as input, and generates a path from that id.
     * id should usually be the id of a JPA entity.
     * For example, TeamImageService:
     * getPath(1035) --> C:/Users/john/team900_seng302/TEAMS/1035
     * @param id A unique id (i.e. data
     * @return The full path as a string
     */
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
     * A data class that holds restriction values for files.
     * For example, we can restrict files:
     *      Max size:  10mb
     *      Valid extensions:  {"png", "jpg"}
     */
    static class FileRestrictions {
        public FileRestrictions(int maxSize, Collection<String> validExtensions) {
            this.maxSize = maxSize;
            this.validExtensions = validExtensions;
        }
        int maxSize;
        Collection<String> validExtensions;
    }

    /**
     * Gets a filename's extension by subbing the last characters.
     * This is kinda bad, we should be using a library for this probably.
     * @param filename The filename to check
     * @return The extension, excluding dot. (i.e. jpg)
     */
    protected Optional<String> getExtension(String filename) {
        int i = filename.lastIndexOf(".");
        if (i >= 0) {
            String extension = filename.substring(i + 1);
            if (extension.length() > 0) {
                return Optional.of(extension);
            } else {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    /**
     * Gets a file's filename, prioritizing the original name.
     * @param file the file
     * @return String filename
     */
    private String getFilename(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (!Objects.isNull(name) && name.length() > 0) {
            return name;
        }
        return file.getName();
    }

    /**
     * Checks if a file is valid.
     * @param file The file to check
     * @return true if all OK, false otherwise.
     */
    public boolean isFileValid(MultipartFile file) {
        String originalName = getFilename(file);
        if (Objects.isNull(originalName)) {
            // If there's no filename, return false.
            logger.error("No filename!");
            return false;
        }

        originalName = StringUtils.cleanPath(getFilename(file));
        Optional<String> optExtension = getExtension(originalName);
        if (optExtension.isEmpty()) {
            // If we can't find an extension, return false.
            logger.error("Couldn't find an extension for file");
            return false;
        }

        if (!fileRestrictions.validExtensions.contains(optExtension.get())) {
            // If the extension is not whitelisted, return false.
            logger.error("Extension was not whitelisted");
            return false;
        }

        if (file.getSize() > fileRestrictions.maxSize) {
            logger.error("File too big");
            return false; // File is too darn big!
        }

        return true; // OK.
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
     * To clarify: You should never really be calling this method!!!!
     * Pls be careful with it.
     *
     * @param folder The folder to be deleted
     * @return boolean true if success, false if failure.
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

    /**
     * The current deployment that is being used.
     * Defaults to TEST.
     * On prod, this should be set to PROD.
     */
    public enum DeploymentType {
        PROD("PROD"), // Production deployment
        STAGING("STAGING"), // Dev deployment
        TEST("TEST"); // everything else. i.e: Local deployment, JUnit tests, integration tests.

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
     * The reason this is not abstract, is that some FileDataSavers may not have default
     * bytes. We still want to support the optional API in that situation.
     * @return An array of bytes as the default.
     */
    public byte[] getDefaultBytes() {
        return new byte[] {};
    }

    /**
     * Allows for specification of default response entities.
     * Example usage:
     * If the default profile picture is an SVG,
     * this should return a response entity to accommodate SVGs.
     * @return An array of bytes as the default.
     */
    public ResponseEntity<byte[]> getDefaultResponseEntity() {
        return null;
    }

    /**
     * Creates a FileDataSaver.
     * deploymentType is PROD when on production,
     * otherwise, deploymentType is TEST.
     * @param deploymentType the deploymentType
     */
    protected FileDataSaver(DeploymentType deploymentType, FileRestrictions fileRestrictions) {
        String prefix = getFolderName();

        this.fileRestrictions = fileRestrictions;

        this.deploymentType = deploymentType;
        initialPath = getDeploymentPath(deploymentType).resolve(prefix);
    }

    public DeploymentType getDeploymentType() {
        return deploymentType;
    }

    /**
     * Saves a file directly.
     * @param id The id of the file. Normally a JPA pk.
     * @param file The file to save
     * @return
     */
    public boolean saveFile(Long id, MultipartFile file) {
        if (isFileValid(file)) {
            try {
                byte[] bytes = file.getBytes();
                return saveBytes(id, bytes);
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    /**
     * Saves bytes directly to a file, and returns true on success, false on failure.
     * @param id A unique ID for the context (e.g. user entity primary key)
     * @param data Bytes of data to save
     * @return true on success, false on failure
     */
    public boolean saveBytes(Long id, byte[] data) {
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
