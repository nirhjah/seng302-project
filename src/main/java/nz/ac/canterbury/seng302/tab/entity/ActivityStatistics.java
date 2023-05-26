package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Activity Statistics Entity
 */
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
    @JoinColumn(name = "fk_activityID", referencedColumnName = "Id")
    private Activity activity;


    public ActivityStatistics() {}

    public ActivityStatistics(User player, Activity activity) {
        this.statisticPlayer = player;
        this.timeOfStatistic = LocalDateTime.now();
        this.activity = activity;

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
