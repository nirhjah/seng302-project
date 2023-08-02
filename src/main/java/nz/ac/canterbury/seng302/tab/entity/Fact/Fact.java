package nz.ac.canterbury.seng302.tab.entity.Fact;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.entity.Activity;

import java.time.LocalTime;

/**
 * A fact is information about an activity
 * The inheritance is all kept in a single table as there are many overlapping values
 * The fact type column will allow filtering of different types of facts, EG: Goal, Substitution and Fact
 * Goal as discriminator value 1, Substitution has value 2 and a Fact has null for a value
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="fact_type",
        discriminatorType = DiscriminatorType.INTEGER)
@DiscriminatorValue("0")
public class Fact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long factID;

    /**
     * By setting the fact type column as in attribute, it allows us to filter using the JPA queries
     * By making them not insertable and not updatable means we can't interfere to change their values
     * It allows the discriminator values to populate still though.
     * This was adaptede from a simular question on stack over flow:
     * https://stackoverflow.com/a/59306312
     */
    @Column(name = "fact_type", insertable=false, updatable = false)
    private int factType;

    @ManyToOne
    @JoinColumn(name = "fk_activityID", referencedColumnName = "activityId")
    private Activity activity;

    private String description;

    private LocalTime timeOfEvent;

    /**
     * Empty Constructor for JPA
     **/
    public Fact() {}
    public Fact(String description, Activity activity, LocalTime timeEventTest) {
        this.activity = activity;
        this.description = description;
        this.timeOfEvent= timeEventTest;
    }

    public String getDescription() {
        return description;
    }
    public LocalTime getTimeEventTest(){ return this.timeOfEvent;}
}
