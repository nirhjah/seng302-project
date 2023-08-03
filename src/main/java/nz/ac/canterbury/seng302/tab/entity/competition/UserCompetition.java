package nz.ac.canterbury.seng302.tab.entity.competition;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.entity.Grade;
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
    @JoinTable(
            name = "user_competition_players",
            joinColumns = @JoinColumn(name = "competition_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> players = new HashSet<>();

    public UserCompetition(String name, Set<User> federationAdmins, Grade grade, String sport) {
        super(name, grade, sport);
    }
    
    public UserCompetition() {}

    public UserCompetition(String name, Grade grade, String sport) {
      super(name, grade, sport);
    }

    public UserCompetition(String name, Set<User> federationAdmins, Grade grade, String sport, Set<User> players) {
        super(name, grade, sport);
        this.players = players;
    }

    public Set<User> getPlayers() {
        return players;
    }

    public void addPlayer(User player) {
        players.add(player);
    }
}
