package nz.ac.canterbury.seng302.tab.response;

import nz.ac.canterbury.seng302.tab.entity.Location;

/**
 * Class used to define data for json query for general team details on create competition page
 */
public class CompetitionTeamInfo {

    private Long teamID;

    private String name;

    private String pictureString;

    // Default constructor (required by Jackson)
    public CompetitionTeamInfo() {
    }

    public CompetitionTeamInfo(Long teamID, String name, String pictureString) {
        this.teamID = teamID;
        this.name = name;
        this.pictureString = pictureString;
    }


    public Long getTeamID() {
        return teamID;
    }

    public void setTeamID(Long teamID) {
        this.teamID = teamID;
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

