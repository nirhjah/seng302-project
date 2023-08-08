package nz.ac.canterbury.seng302.tab.response.competition;

import nz.ac.canterbury.seng302.tab.entity.Location;

public class CompetitionUserInfo {

    private Long userTeamID;

    private String name;

    private String pictureString;


    // Default constructor (required by Jackson)
    public CompetitionUserInfo() {
    }

    public CompetitionUserInfo(Long userID, String firstName, String lastName, String pictureString) {
        this.userTeamID = userID;
        this.name = firstName + " " + lastName;
        this.pictureString = pictureString;
    }


    public Long getUserTeamID() {
        return userTeamID;
    }

    public void setUserTeamID(Long userID) {
        this.userTeamID = userID;
    }

    public String getPictureString() {
        return pictureString;
    }

    public void setPictureString(String pictureString) {
        this.pictureString = pictureString;
    }

    public String getName() {
        return name;
    }

    public void setName(String firstName, String lastName) {
        this.name = firstName + " " + lastName;
    }


}
