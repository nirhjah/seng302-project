package nz.ac.canterbury.seng302.tab.entity.competition;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;

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

  public TeamCompetition(String name, Set<User> federationAdmins, String grade) {
    super(name, federationAdmins, grade);
  }
  
  public TeamCompetition(String name, Set<User> federationAdmins, String grade, HashSet<Team> teams) {
    super(name, federationAdmins, grade);
    this.teams = teams;
  }
}
