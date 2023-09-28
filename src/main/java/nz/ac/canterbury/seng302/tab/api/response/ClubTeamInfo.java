package nz.ac.canterbury.seng302.tab.api.response;

public class ClubTeamInfo {

    private Long teamId;

    private String name;


    // Default constructor (required by Jackson)
    public ClubTeamInfo() {
    }

    public ClubTeamInfo(Long teamId, String name) {
        this.teamId = teamId;
        this.name = name;
    }


    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}


