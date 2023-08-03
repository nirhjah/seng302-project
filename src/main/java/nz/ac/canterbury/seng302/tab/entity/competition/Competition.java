package nz.ac.canterbury.seng302.tab.entity.competition;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.entity.Grade;
import nz.ac.canterbury.seng302.tab.entity.Location;

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
    private long competitionId;
  
    @Column(nullable = false)
    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private Grade grade;

    @Column(nullable = false)
    private String sport;

    @ManyToOne(cascade = CascadeType.ALL)
    private Location location;

    public Competition() {}
    
    /**
     * main constructor
     * @param name competition name
     * @param grade competition grade
     * @param sport competition sport
     * @param location competition location 
    */
    public Competition(String name, Grade grade, String sport, Location location) {
        this.name = name;
        this.grade = grade;
        this.sport = sport;
        this.location = location;
    }
    
    /**
     * constructor without setting location -- for testing purposes 
     * @param name competition name
     * @param grade competition grade
     * @param sport competition sport
    */
    public Competition(String name, Grade grade, String sport) {
        this.name = name;
        this.grade = grade;
        this.sport = sport;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Competition competition = (Competition) o;
        return Objects.equals(competitionId, competition.getCompetitionId());
    }

    public long getCompetitionId() {
        return this.competitionId;
    }

    public String getName() {
        return this.name;
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
}
