package nz.ac.canterbury.seng302.tab.entity.Fact;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.User;

import java.time.LocalTime;

/**
 * This is a subclass on the Fact entity
 * The Fact entity stores all of its subclasses in 1 table, with a fact type column to differienate between different
 * subtypes.
 * Goal is scored by a player of a team.
 */

@Entity
@DiscriminatorValue("3")
public class OppositionGoal extends Fact{

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_userID", referencedColumnName = "Id")
    private User scorer;

    private int goalValue;

    /**
     * Empty Constructor for JPA
     **/
    public OppositionGoal() {}

    public OppositionGoal(String description, LocalTime timeOfEvent, Activity activity,int goalValue) {
        super(description,  activity,timeOfEvent);
        this.goalValue = goalValue;
    }
}
