package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Activity Entity
 */
@Entity(name = "Activity")
public class Activity {

    /**
     * Enum of all possible activity types
     */
    public enum ActivityType {
        Game,
        Friendly,
        Training,
        Competition,
        Other
    }

    public ActivityType[] getActivityTypes() {return ActivityType.values();}
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


    @ManyToOne(cascade = CascadeType.ALL)
    private Location location;


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
     * @param location - the location of the activity
     */
    public Activity(ActivityType activityType, Team team, String description, LocalDateTime activityStart, LocalDateTime activityEnd, User creator, Location location) {
        this.activityType = activityType;
        this.team = team;
        this.description = description;
        this.activityStart = activityStart;
        this.activityEnd = activityEnd;
        this.activityOwner = creator;
        this.location = location;

    }

    public long getId() {
        return id;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public LocalDateTime getActivityStart() {
        return activityStart;
    }

    public LocalDateTime getActivityEnd() {
        return activityEnd;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return id == activity.id && activityType == activity.activityType && Objects.equals(team, activity.team) && Objects.equals(description, activity.description) && Objects.equals(activityStart, activity.activityStart) && Objects.equals(activityEnd, activity.activityEnd) && Objects.equals(activityOwner, activity.activityOwner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, activityType, team, description, activityStart, activityEnd, activityOwner);
    }
}
