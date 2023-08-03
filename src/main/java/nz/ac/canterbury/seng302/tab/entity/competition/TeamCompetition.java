package nz.ac.canterbury.seng302.tab.entity.competition;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.entity.Grade;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.helper.exceptions.UnmatchedGradeException;

import java.util.HashSet;
import java.util.Set;

/**
 * Class for Competition object which is annotated as a JPA entity.
 */
@Entity(name ="TeamCompetition") 
@DiscriminatorValue("TEAM")
public class TeamCompetition extends Competition {
    @ManyToMany(fetch = FetchType.LAZY)
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
     * Checks whether or not we can add a team to a competition.
     * @param team The team in question
     * @return true if we can add the team, false otherwise.
     */
    public boolean canAddTeam(Team team) {
        boolean gradeOk = team.getGrade().equals(getGrade()) ;
        boolean sportOk = team.getSport().equals(getSport());
        return gradeOk && sportOk;
    }

    /**
     * adds a team to the competition
     * @param team the team to be added to the competition
     */
    public void addTeam(Team team) {
        if (!team.getGrade().equals(getGrade())) {
            throw new UnmatchedGradeException(team.getGrade(), getGrade());
        }
        this.teams.add(team);
    }
}
