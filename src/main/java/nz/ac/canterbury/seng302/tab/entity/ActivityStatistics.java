package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.entity.Fact.Fact;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Activity Statistics Entity
 */

@Entity
public class ActivityStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activityStatisticsID")
    private long activityStatisticsID;

    @Column(nullable = false)
    private LocalDateTime timeOfStatistic;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_userID", referencedColumnName = "Id")
    private User statisticPlayer;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_activityID", referencedColumnName = "activityId")
    private Activity activity;

    @ElementCollection(targetClass = Fact.class, fetch = FetchType.EAGER)

    private List<Fact> activityStatisticFacts;

   // @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    private List<String> activityScore;

    public ActivityStatistics() {}

    public ActivityStatistics(User player, Activity activity, List<String> activityScore, List<Fact> activityFacts) {
        this.statisticPlayer = player;
        this.timeOfStatistic = LocalDateTime.now();
        this.activity = activity;
        this.activityStatisticFacts = activity.getFactList();
        this.activityScore = activity.getActivityScore();



    }

    public long getActivityStatisticsID() {
        return activityStatisticsID;
    }

    public void setActivityStatisticsID(long activityStatisticsID) {
        this.activityStatisticsID = activityStatisticsID;
    }

    public LocalDateTime getTimeOfStatistic() {
        return timeOfStatistic;
    }

    public User getStatisticPlayer() {
        return statisticPlayer;
    }

    public void setStatisticPlayer(User statisticPlayer) {
        this.statisticPlayer = statisticPlayer;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
