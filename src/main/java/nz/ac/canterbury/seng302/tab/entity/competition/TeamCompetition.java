package nz.ac.canterbury.seng302.tab.entity.competition;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.entity.Grade;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.helper.exceptions.UnmatchedGradeException;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Class for Team Competition - competition composed of teams only
 */
@Entity
@DiscriminatorValue("TEAM")
public class TeamCompetition extends Competition {

    // This should really be a LAZY fetchType, but we are getting
    // issues with LazyInitializationException, and after a bit of research,
    // there doesn't seem to be an easy fix.
    // See more: https://vladmihalcea.com/the-best-way-to-handle-the-lazyinitializationexception/:
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Team> teams = new HashSet<>();

    public TeamCompetition() {}

    public TeamCompetition(String name, Grade grade, String sport) {
        super(name, grade, sport);
    }

    public TeamCompetition(String name, Grade grade, String sport, Location location, LocalDateTime competitionStart, LocalDateTime competitionEnd, Set<Team> teams) {
        super(name, grade, sport, location, competitionStart, competitionEnd);
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
      return Set.copyOf(this.teams);
    }

    public void setTeams(Set<Team> teams) {
      this.teams = teams;
    }
}
