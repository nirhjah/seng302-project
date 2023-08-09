package nz.ac.canterbury.seng302.tab.entity.competition;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.entity.Grade;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;

import java.util.HashSet;
import java.util.Set;

/**
 * Class for user Competition - competition composed of players only
 */
@Entity 
@DiscriminatorValue("USER")
public class UserCompetition extends Competition {
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<User> players = new HashSet<>();

    public UserCompetition(String name, Grade grade, String sport, Location location) {
        super(name, grade, sport, location);
    }
    
    public UserCompetition() {}

    public UserCompetition(String name, Grade grade, String sport) {
      super(name, grade, sport);
    }

    public UserCompetition(String name, Grade grade, String sport, Location location, Set<User> players) {
        super(name, grade, sport, location);
        this.players = players;
    }

    public Set<User> getPlayers() {
        return players;
    }

    public void addPlayer(User player) {
        players.add(player);
    }

    public void setPlayers(Set<User> players) {
        this.players = players;
    }
}
