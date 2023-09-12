package nz.ac.canterbury.seng302.tab.entity.fact;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.User;

/**
 * This is a subclass on the Fact entity
 * The Fact entity stores all of its subclasses in 1 table, with a fact type column to differienate between different
 * subtypes.
 * Goal is scored by a player of a team.
 */

@Entity
@DiscriminatorValue("1")
public class Goal extends Fact{

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_userID", referencedColumnName = "Id")
    private User scorer;

    private int goalValue;

    /**
     * Empty Constructor for JPA
     **/
    public Goal() {}

    public Goal(String description, String timeOfEvent, Activity activity, User scorer, int goalValue) {
        super(description, timeOfEvent, activity);
        this.scorer = scorer;
        this.goalValue = goalValue;
    }

    public User getScorer() {
        return this.scorer;
    }

    public int getGoalValue() {
        return goalValue;
    }

    public void setGoalValue(int goalValue) {
        this.goalValue = goalValue;
    }
}
