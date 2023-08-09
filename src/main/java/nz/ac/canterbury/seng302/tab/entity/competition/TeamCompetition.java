package nz.ac.canterbury.seng302.tab.entity.competition;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.entity.Grade;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.helper.exceptions.UnmatchedGradeException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Class for Team Competition - competition composed of teams only
 */
@Entity(name ="TeamCompetition") 
@DiscriminatorValue("TEAM")
public class TeamCompetition extends Competition {
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Team> teams = new HashSet<>();

    public TeamCompetition() {}

    public TeamCompetition(String name, Grade grade, String sport) {
        super(name, grade, sport);
    }

    public TeamCompetition(String name, Grade grade, String sport, Location location) {
        super(name, grade, sport, location);
    }

    public TeamCompetition(String name, Grade grade, String sport, Location location, Team team) {
        super(name, grade, sport, location);
        this.teams.add(team);
    }

    public TeamCompetition(String name, Grade grade, String sport, Location location, Set<Team> teams) {
      super(name, grade, sport, location);
      this.teams = teams;
    }

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
        if (!canAddTeam(team)) {
            throw new UnmatchedGradeException(team.getGrade(), getGrade());
        }
        this.teams.add(team);
    }

    public Set<Team> getTeams() {
      return Collections.unmodifiableSet(this.teams);
    }

    public void setTeams(Collection<Team> teams) {
      this.teams = Set.copyOf(teams);
    }
}
