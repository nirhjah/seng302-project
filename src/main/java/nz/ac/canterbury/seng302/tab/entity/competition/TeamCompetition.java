package nz.ac.canterbury.seng302.tab.entity.competition;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.entity.Grade;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.helper.exceptions.UnmatchedGradeException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
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

    public TeamCompetition(String name, Grade grade, String sport, Location location, LocalDateTime competitionStart, LocalDateTime competitionEnd) {
        super(name, grade, sport, location, competitionStart, competitionEnd);
    }

    public TeamCompetition(String name, Grade grade, String sport, Location location,  LocalDateTime competitionStart, LocalDateTime competitionEnd, Team team) {
        super(name, grade, sport, location, competitionStart, competitionEnd);
        this.teams.add(team);
    }

    public TeamCompetition(String name, Grade grade, String sport, Location location, LocalDateTime competitionStart, LocalDateTime competitionEnd, Set<Team> teams) {
        super(name, grade, sport, location, competitionStart, competitionEnd);
        this.teams = teams;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Competition competition = (Competition) o;
        return Objects.equals(super.competitionId, competition.getCompetitionId());
    }

    @Override
    public int hashCode() {
        return 31 + Objects.hash(competitionId, grade, sport);
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
