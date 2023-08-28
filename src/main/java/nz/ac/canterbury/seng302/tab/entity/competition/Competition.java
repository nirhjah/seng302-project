package nz.ac.canterbury.seng302.tab.entity.competition;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.entity.Grade;
import nz.ac.canterbury.seng302.tab.entity.Location;

import java.util.Date;
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

    @Column(nullable = true)
    private Date competitionStart;

    @Column(nullable = true)
    private Date competitionEnd;

    protected Competition() {}
    
    /**
     * main constructor
     * @param name competition name
     * @param grade competition grade
     * @param sport competition sport
     * @param location competition location 
    */

    /**
     * main constructor
     * @param name
     * @param grade
     * @param sport
     * @param location
     * @param competitionStart
     * @param competitionEnd
     * */
    protected Competition(String name, Grade grade, String sport, Location location, Date competitionStart, Date competitionEnd) {
        this.name = name;
        this.grade = grade;
        this.sport = sport;
        this.location = location;
        this.competitionStart = competitionStart;
        this.competitionEnd = competitionEnd;
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
     * Sets the date in UTC format. (Milliseconds since unix epoch.)
     * You can get the milliseconds by doing Date.getTime()
     * @param startDate The start time of the competition
     * @param endDate The end time for the competition
     */
    public void setDate(Date startDate, Date endDate) {
        this.competitionStart = startDate;
        this.competitionEnd = endDate;
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

    public Date getCompetitionEnd() {
        return competitionEnd;
    }

    public Date getCompetitionStart() {
        return competitionStart;
    }

}
