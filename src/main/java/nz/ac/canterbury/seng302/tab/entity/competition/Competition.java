package nz.ac.canterbury.seng302.tab.entity.competition;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.entity.User;

import java.util.HashSet;
import java.util.Set;

/**
 * Class for Competition object which is annotated as a JPA entity.
 */
@Entity(name = "Competition")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "competition_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Competition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CompetitionId")
    private long competitionId;
  
    @Column(nullable = false)
    private String name;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "federation_administrators",
            joinColumns = @JoinColumn(name = "competition_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> federationAdmins = new HashSet<User>();

    // TODO: we are asking the PO for permission to change this to a set of predefined grades
    @Column(nullable = false)
    private String grade;

    @Column(nullable = false)
    private String sport;
}
