package nz.ac.canterbury.seng302.tab.entity;

import java.time.LocalTime;
import java.util.List;

public class UserTeamActivityStatistic {
    private final Activity activity;

    private final long goalsScored;

    private final long minutesPlayed;

    private final List<LocalTime> subOffTimings;

    private final List<LocalTime> subOnTimings;

    public long getGoalsScored() {
        return goalsScored;
    }

    public long getMinutesPlayed() {
        return minutesPlayed;
    }

    public List<LocalTime> getSubOffTimings() {
        return subOffTimings;
    }

    public List<LocalTime> getSubOnTimings() {
        return subOnTimings;
    }

    public Activity getActivity() {return activity;}

    public UserTeamActivityStatistic(long goalsScored, long minutesPlayed, List<LocalTime> subOffTimings, List<LocalTime> subOnTimings, Activity activity) {
        this.goalsScored = goalsScored;
        this.minutesPlayed = minutesPlayed;
        this.subOffTimings = subOffTimings;
        this.subOnTimings = subOnTimings;
        this.activity = activity;
    }

}
