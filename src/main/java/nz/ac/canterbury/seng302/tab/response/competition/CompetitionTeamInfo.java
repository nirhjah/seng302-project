package nz.ac.canterbury.seng302.tab.response.competition;

import nz.ac.canterbury.seng302.tab.entity.Location;

/**
 * Class used to define data for json query for general team details on create competition page
 */
public class CompetitionTeamInfo {

    private Long userTeamID;

    private String name;

    private String pictureString;

    // Default constructor (required by Jackson)
    public CompetitionTeamInfo() {
    }

    public CompetitionTeamInfo(Long teamID, String name, String pictureString) {
        this.userTeamID = teamID;
        this.name = name;
        this.pictureString = pictureString;
    }


    public Long getUserTeamID() {
        return userTeamID;
    }

    public void setUserTeamID(Long teamID) {
        this.userTeamID = teamID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPictureString() {
        return pictureString;
    }

    public void setPictureString(String pictureString) {
        this.pictureString = pictureString;
    }

}

