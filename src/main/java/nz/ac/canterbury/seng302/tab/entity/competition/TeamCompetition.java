package nz.ac.canterbury.seng302.tab.entity.competition;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.entity.Team;

import java.util.HashSet;
import java.util.Set;

/**
 * Class for Competition object which is annotated as a JPA entity.
 */
@Entity(name ="TeamCompetition") 
@DiscriminatorValue("TEAM")
public class TeamCompetition extends Competition {
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Team> teams = new HashSet<>();
}


