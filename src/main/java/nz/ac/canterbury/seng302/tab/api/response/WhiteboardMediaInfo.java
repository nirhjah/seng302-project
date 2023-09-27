package nz.ac.canterbury.seng302.tab.api.response;

import nz.ac.canterbury.seng302.tab.entity.WhiteBoardRecording;
import nz.ac.canterbury.seng302.tab.entity.WhiteboardScreenshot;

public class WhiteboardMediaInfo {

    private Long id;
    private String name;

    // True is the media is publically accessible
    private boolean isPublic;

    // True if the media is a recording (i.e. video)
    private boolean isRecording;

    // Imagedata as base64 encoded string
    private String imageData;


    // Default constructor (required by Jackson)
    public WhiteboardMediaInfo() {
    }

    public WhiteboardMediaInfo(WhiteBoardRecording recording) {
        id = recording.getId();
        name = recording.getName();
        isPublic = recording.isPublic();
        isRecording = false;
    }

    public WhiteboardMediaInfo(WhiteboardScreenshot screenshot) {
        id = screenshot.getId();
        name = screenshot.getTag();
        isPublic = screenshot.isPublic();
        isRecording = true;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public boolean isPublic() {
        return this.isPublic;
    }

    public boolean isRecording() {
        return this.isRecording;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }
}
