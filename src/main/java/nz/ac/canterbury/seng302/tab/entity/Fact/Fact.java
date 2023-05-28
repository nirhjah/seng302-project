package nz.ac.canterbury.seng302.tab.entity.Fact;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.entity.Activity;

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
@DiscriminatorValue("null")
public class Fact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long factID;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_activityID", referencedColumnName = "activityId")
    private Activity activity;

    private String description;

    private String timeOfEvent;

    /**
     * Empty Constructor for JPA
     **/
    public Fact() {}

    public Fact(String description, String timeOfEvent, Activity activity) {
        this.activity = activity;
        this.description = description;
        this.timeOfEvent = timeOfEvent;
    }
}
