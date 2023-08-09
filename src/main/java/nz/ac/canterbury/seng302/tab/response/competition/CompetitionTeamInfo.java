package nz.ac.canterbury.seng302.tab.response.competition;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.helper.ImageType;

/**
 * Class used to define data for json query for general team details on create competition page
 */
public class CompetitionTeamInfo {

    private Long userTeamID;

    private String name;


    // Default constructor (required by Jackson)
    public CompetitionTeamInfo() {
    }

    public CompetitionTeamInfo(Long teamID, String name) {
        this.userTeamID = teamID;
        this.name = name;
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


}

