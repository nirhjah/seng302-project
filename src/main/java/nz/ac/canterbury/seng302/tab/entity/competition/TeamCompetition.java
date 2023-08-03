package nz.ac.canterbury.seng302.tab.entity.competition;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.entity.Grade;
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

    public TeamCompetition(String name, Set<User> federationAdmins, Grade grade, String sport, Set<Team> teams) {
      super(name, grade, sport);
      this.teams = teams;
    }
  
    public TeamCompetition(String name, Set<User> federationAdmins, Grade grade, String sport, Team team) {
      super(name, grade, sport);
      this.teams.add(team);
    }

    public TeamCompetition(String name, Grade grade, String sport) {
      super(name, grade, sport);
    }
    
    public TeamCompetition() {}

    /**
     * adds a team to the competition
     * @param team the team to be added to the competition
     */
    public void addTeam(Team team) {
      this.teams.add(team);
    }
}
