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
@DiscriminatorValue("1")
public class Goal extends Fact{

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_userID", referencedColumnName = "Id")
    private User scorer;

    /**
     * Empty Constructor for JPA
     **/
    public Goal() {}
    public Goal(String description, Activity activity, User scorer, LocalTime timeOfEvent) {
        super(description,activity,timeOfEvent);
        this.scorer = scorer;
    }

    public User getScorer() {
        return this.scorer;
    }
}
