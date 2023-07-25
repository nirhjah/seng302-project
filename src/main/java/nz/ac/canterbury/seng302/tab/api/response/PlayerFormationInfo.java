package nz.ac.canterbury.seng302.tab.api.response;

/**
 * Class used to define data for json query for general user details related to displaying formation
 */
public class PlayerFormationInfo {

    private Long playerID;

    private String firstName;

    private String pictureString;

    // Default constructor (required by Jackson)
    public PlayerFormationInfo() {
    }

    public PlayerFormationInfo(Long playerID, String firstName, String pictureString) {
        this.playerID = playerID;
        this.firstName = firstName;
        this.pictureString = pictureString;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getPictureString() {
        return pictureString;
    }

    public Long getPlayerID() {
        return playerID;
    }
}
