package nz.ac.canterbury.seng302.tab.utility;

import org.springframework.mock.web.MockMultipartFile;

/**
 * A MockMultipartFile, but the extension actually works.
 * -
 * In regular MockMultipartFiles, for some reason, getExtension doesn't work :-)
 */
public class MockMultiPartFileExtension extends MockMultipartFile {

    private final String extension;
    public MockMultiPartFileExtension(String name, byte[] content, String extension) {
        super(name, content);
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}

