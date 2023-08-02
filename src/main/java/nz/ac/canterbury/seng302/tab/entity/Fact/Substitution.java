package nz.ac.canterbury.seng302.tab.entity.Fact;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.User;

import java.time.LocalTime;

/**
 * This is a subclass on the Fact entity
 * The Fact entity stores all of its subclasses in 1 table, with a fact type column to tell the differents between subtypes.
 * Substitution is made when one player comes off and another goes on.
 */

@Entity
@DiscriminatorValue("2")
public class Substitution extends Fact{

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_userIDPlayerOff", referencedColumnName = "Id")
    private User playerOff;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_userIDPlayerOn", referencedColumnName = "Id")
    private User playerOn;

    /**
     * Empty Constructor for JPA
     **/
    public Substitution() {}

    public Substitution(String description, Activity activity, User playerOff, User playerOn, LocalTime timeEventTest) {
        super(description, activity,timeEventTest);
        this.playerOff = playerOff;
        this.playerOn = playerOn;
    }

    public User getPlayerOff() {
        return playerOff;
    }

    public User getPlayerOn() {
        return playerOn;
    }
}
