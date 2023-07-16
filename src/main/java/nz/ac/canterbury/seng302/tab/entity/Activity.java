package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.entity.Fact.Fact;
import nz.ac.canterbury.seng302.tab.enums.ActivityType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Activity Entity
 */
@Entity(name = "Activity")
public class Activity {

    public ActivityType[] getActivityTypes() {return ActivityType.values();}
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activityId")
    private long id;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ActivityType activityType;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_teamID", referencedColumnName = "teamId")
    private Team team;

    /**
     * <p>The formation associated with this "Activity".</p>
     * <em>Should only apply to Game and Friendly activities (U33)</em>
     */
    @OneToOne(cascade = CascadeType.ALL, optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name="fk_formation", referencedColumnName = "formationId")
    private Formation formation;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime activityStart;

    @Column(nullable = false)
    private LocalDateTime activityEnd;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_userID", referencedColumnName = "Id")
    private User activityOwner;


    @ManyToOne(cascade = CascadeType.ALL)
    private Location location;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
    private List<Fact> activityFacts;

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @Column(name = "activityScore", nullable = false)
    private List<String> activityScore;


    @Column
    private String activityTeamScore;

    @Column
    private String otherTeamScore;

    @Column
    private boolean outcome;




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

    /**
     * <p>Checks whether this Activity can have a formation associated with it.</p>
     * Currently only Activities of type <code>Game</code> or <code>Friendly</code>
     * can have a formation
     */
    public boolean canContainFormation() {
        return activityType != ActivityType.Game || activityType != ActivityType.Friendly;
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

    public String getDescription(){ return this.description;}

    public ActivityType getActivityType(){
        return this.activityType;
    }

    public void setActivityType(ActivityType activityType){
        this.activityType=activityType;
    }

    public void setDescription(String description){
        this.description=description;
    }

    public void setActivityStart(LocalDateTime activityStart){
        this.activityStart = activityStart;
    }

    public void setActivityEnd(LocalDateTime activityEnd){
        this.activityEnd=activityEnd;
    }

    public void setActivityOwner(User activityOwner){
        this.activityOwner=activityOwner;
    }

    public User getActivityOwner(){
        return this.activityOwner;
    }

    /**
     * <p>Returns an Optional containing this activity's associated formation.</p>
     * @return The formation, or empty if it's not set.
     */
    public Optional<Formation> getFormation() {
        return Optional.ofNullable(this.formation);
    }

    public void addFactList(List<Fact> factList) { this.activityFacts = factList;}

    public List<Fact> getFactList() {return this.activityFacts; }

    public void addFactToFactList(Fact fact) {this.activityFacts.add(fact); }


    public String getActivityTeamScore() {
        return activityTeamScore;
    }

    public void setActivityTeamScore(String activityTeamScore) {
        this.activityTeamScore = activityTeamScore;
    }

    public String getOtherTeamScore() {
        return otherTeamScore;
    }

    public void setOtherTeamScore(String otherTeamScore) {
        this.otherTeamScore = otherTeamScore;
    }

    /**
     * Sets the formation for this activity
     * @param formation This formation should be from the same team as this activity (U33 AC1)
     * @throws UnsupportedOperationException If this activity doesn't have a formation
     * @see Activity#canContainFormation()
     */
    public void setFormation(Formation formation) throws UnsupportedOperationException {
        if (!canContainFormation()) {
            throw new UnsupportedOperationException("Can not have a formation on an activity of type: " + activityType);
        }

        this.formation = formation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return id == activity.id
            && activityType == activity.activityType
            && Objects.equals(team, activity.team)
            && Objects.equals(description, activity.description)
            && Objects.equals(activityStart, activity.activityStart)
            && Objects.equals(activityEnd, activity.activityEnd)
            && Objects.equals(activityOwner, activity.activityOwner)
            && Objects.equals(formation, activity.formation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, activityType, team, description, activityStart, activityEnd, activityOwner);
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", activityType=" + activityType +
                ", team=" + team +
                ", description='" + description + '\'' +
                ", activityStart=" + activityStart +
                ", activityEnd=" + activityEnd +
                ", activityOwner=" + activityOwner +
                '}';
    }

}
