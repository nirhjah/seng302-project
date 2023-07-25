package nz.ac.canterbury.seng302.tab.api.response;

/**
 * Class used to define data for json query for general user details related to displaying formation
 */
public class PlayerFormationInfo {
    private String firstName;

    private String pictureString;

    // Default constructor (required by Jackson)
    public PlayerFormationInfo() {
    }

    public PlayerFormationInfo(String firstName, String pictureString) {
        this.firstName = firstName;
        this.pictureString = pictureString;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getPictureString() {
        return pictureString;
    }
}
