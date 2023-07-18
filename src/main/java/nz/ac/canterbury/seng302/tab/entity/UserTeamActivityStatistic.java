package nz.ac.canterbury.seng302.tab.entity;

import java.util.List;

public class UserTeamActivityStatistic {

    public UserTeamActivityStatistic(long goalsScored, long minutesPlayed, List<String> subOffTimings, List<String> subOnTimings, Activity activity) {
        this.goalsScored = goalsScored;
        this.minutesPlayed = minutesPlayed;
        this.subOffTimings = subOffTimings;
        this.subOnTimings = subOnTimings;
        this.activity = activity;
    }

    private Activity activity;

    private long goalsScored;

    private long minutesPlayed;

    private List<String> subOffTimings;

    private List<String> subOnTimings;

    public long getGoalsScored() {
        return goalsScored;
    }

    public long getMinutesPlayed() {
        return minutesPlayed;
    }

    public List<String> getSubOffTimings() {
        return subOffTimings;
    }

    public List<String> getSubOnTimings() {
        return subOnTimings;
    }

    public Activity getActivity() {return activity;}
}
