package nz.ac.canterbury.seng302.tab.entity.competition;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.entity.Grade;
import nz.ac.canterbury.seng302.tab.entity.Location;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

/**
 * Class for Competition object which is annotated as a JPA entity.
 */
@Entity(name = "Competition")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "competitionType", discriminatorType = DiscriminatorType.STRING)
public abstract class Competition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CompetitionId")
    protected long competitionId;
  
    @Column(nullable = false)
    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn
    protected Grade grade;

    @Column(nullable = false)
    protected String sport;

    @ManyToOne(cascade = CascadeType.ALL)
    private Location location;

    /*
     * The reason we must have longs here, is because JPA REFUSES to allow for
     * date comparison in custom queries, for some reason.
     * So instead, we represent dates as a long; time in seconds since the 1970 UNIX epoch.
     */
    @Column()
    private long competitionStart;
    @Column()
    private long competitionEnd;

    // Timezone of where the app is running.
    // This should be set to 0, since in our application, we have no timezones.
    private static final ZoneOffset DEFAULT_ZONE = ZoneOffset.ofHours(0);

    protected Competition() {}
    
    /**
     * main constructor
     * @param name
     * @param grade
     * @param sport
     * @param location
     * @param competitionStart
     * @param competitionEnd
     * */
    protected Competition(String name, Grade grade, String sport, Location location, LocalDateTime competitionStart, LocalDateTime competitionEnd) {
        this.name = name;
        this.grade = grade;
        this.sport = sport;
        this.location = location;
        this.competitionStart = competitionStart.toEpochSecond(DEFAULT_ZONE);
        this.competitionEnd = competitionEnd.toEpochSecond(DEFAULT_ZONE);
    }
    
    /**
     * constructor without setting location and time -- for testing purposes
     * @param name competition name
     * @param grade competition grade
     * @param sport competition sport
    */
    protected Competition(String name, Grade grade, String sport) {
        this.name = name;
        this.grade = grade;
        this.sport = sport;
    }

    /**
     * Compares this Competition instance with another object for equality. Two instances are considered equal if they have the same competition ID.
     *
     * @param o The object to compare against.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Competition competition = (Competition) o;
        return Objects.equals(competitionId, competition.getCompetitionId());
    }

    /**
     * Computes a hash code value for a competition. The hash code is calculated based on the competition ID, grade, and sport of the instance.
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return 31 + Objects.hash(competitionId, grade, sport);
    }

    public long getCompetitionId() {
        return this.competitionId;
    }

    public String getName() {
        return this.name;
    }

    /**
     * Sets the start/end dates of a competition, given Local date-times.
     * @param startDate The start time of the competition
     * @param endDate The end time for the competition
     */
    public void setDate(LocalDateTime startDate, LocalDateTime endDate) {
        long start = startDate.toEpochSecond(DEFAULT_ZONE);
        long end = endDate.toEpochSecond(DEFAULT_ZONE);
        setDateAsEpochSecond(start, end);
    }

    public void setDateAsEpochSecond(long competitionStart, long competitionEnd) {
        this.competitionStart = competitionStart;
        this.competitionEnd = competitionEnd;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Grade getGrade() {
        return this.grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public String getSport() {
        return this.sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * The reason we must have longs here, is because JPA REFUSES to allow for
     * date comparison in custom queries, for some reason.
     * So instead, we represent dates as a long; time in seconds since the 1970 UNIX epoch.
     * @return long the competition end date
     */
    public long getCompetitionEnd() {
        return competitionEnd;
    }
    public long getCompetitionStart() {
        return competitionStart;
    }

    public LocalDateTime getCompetitionEndDate() {
        return LocalDateTime.ofEpochSecond(competitionEnd, 0, DEFAULT_ZONE);
    }
    public LocalDateTime getCompetitionStartDate() {
        return LocalDateTime.ofEpochSecond(competitionStart, 0, DEFAULT_ZONE);
    }
}
