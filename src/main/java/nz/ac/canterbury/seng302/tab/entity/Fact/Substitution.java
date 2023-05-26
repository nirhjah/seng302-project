package nz.ac.canterbury.seng302.tab.entity.Fact;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.entity.User;

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
}
