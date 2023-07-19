package nz.ac.canterbury.seng302.tab.entity;

public class UserTeamStatistic {

    private Team team;
    private int totalGoalsScored;
    private long totalMinutesPlayed;
    private long totalMatchesPlayed;

    public UserTeamStatistic(Team team, int totalGoalsScored, long totalMinutesPlayed, long totalMatchesPlayed) {
        this.team = team;
        this.totalGoalsScored = totalGoalsScored;
        this.totalMinutesPlayed = totalMinutesPlayed;
        this.totalMatchesPlayed = totalMatchesPlayed;
    }

    public Team getTeam() {
        return team;
    }

    public int getTotalGoalsScored() {
        return totalGoalsScored;
    }

    public long getTotalMinutesPlayed() {
        return totalMinutesPlayed;
    }

    public long getTotalMatchesPlayed() {
        return totalMatchesPlayed;
    }
}
