package nz.ac.canterbury.seng302.tab.service;

import org.springframework.security.core.parameters.P;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import java.nio.file.*;




public class FileDataService {
    private UUID getUUID() {
        return UUID.randomUUID();
    }

    // DON'T CHANGE THIS PATH NAME!
    private static final String TAB900_FILE_MODIFIER = "team900_seng302";

    private final Path imagePath = Path.of(
            System.getProperty("user.home"),
            TAB900_FILE_MODIFIER
    );

    private Path getPath(String modifier) {
        if (modifier != null) {
            return imagePath
                    .resolve(modifier)
                    .resolve(getUUID().toString());
        } else {
            return imagePath.resolve(getUUID().toString());
        }
    }

    public Optional<String> saveFile(MultipartFile file, String modifier) {
        Path fullPath = getPath(modifier);
        File newFile = new File(fullPath.toString());
        try {
            file.transferTo(newFile);
        } catch (IOException | IllegalStateException ex) {
            // failure.
            return Optional.empty();
        }

        return Optional.of(fullPath.toString());
    }

    public Optional<String> saveFile( file) {

    }

    public Optional<byte[]> getData(String fileName) {

    };

    public boolean deleteFile(String name) {

    }
}
