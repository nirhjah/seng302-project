package nz.ac.canterbury.seng302.tab.response;

import nz.ac.canterbury.seng302.tab.entity.Location;

public class CompetitionUserInfo {

    private Long userID;

    private String firstName;

    private String lastName;

    private String pictureString;


    // Default constructor (required by Jackson)
    public CompetitionUserInfo() {
    }

    public CompetitionUserInfo(Long userID, String firstName, String lastName, String pictureString) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.pictureString = pictureString;
    }


    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }


    public String getPictureString() {
        return pictureString;
    }

    public void setPictureString(String pictureString) {
        this.pictureString = pictureString;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
