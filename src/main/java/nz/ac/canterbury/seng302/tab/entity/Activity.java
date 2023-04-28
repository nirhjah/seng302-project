package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Activity Entity
 */
@Entity(name = "Activity")
public class Activity {

    /**
     * Enum of all possible activity types
     */
    enum ActivityType {
        Game,
        Friendly,
        Training,
        Competiton,
        Other
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activityId")
    private long id;

    @Column(nullable = false)
    private ActivityType activityType;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_teamID", referencedColumnName = "teamId")
    private Team team;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime activityStart;

    @Column(nullable = false)
    private LocalDateTime activityEnd;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_userI", referencedColumnName = "Id")
    private User activityOwner;

    /**
     * Empty Constructor for JPA
     */
    public Activity() {}

    /**
     * Generic Constructor for Activity
     * @param activityType - enum value of activity type
     * @param team - the team that this activity relates too
     * @param description - short description of the event
     * @param activityStart - the date and time of start of activity
     * @param activityEnd - the end date and time of activity
     */
    public Activity(ActivityType activityType, Team team, String description, LocalDateTime activityStart, LocalDateTime activityEnd, User creator) {
        this.activityType = activityType;
        this.team = team;
        this.description = description;
        this.activityStart = activityStart;
        this.activityEnd = activityEnd;
        this.activityOwner = creator;
    }
}
