package nz.ac.canterbury.seng302.tab.response.competition;

public class CompetitionUserInfo {

    private Long userTeamID;

    private String name;

    // Default constructor (required by Jackson)
    public CompetitionUserInfo() {
    }

    public CompetitionUserInfo(Long userID, String firstName, String lastName) {
        this.userTeamID = userID;
        this.name = firstName + " " + lastName;
    }


    public Long getUserTeamID() {
        return userTeamID;
    }

    public void setUserTeamID(Long userID) {
        this.userTeamID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String firstName, String lastName) {
        this.name = firstName + " " + lastName;
    }


}
