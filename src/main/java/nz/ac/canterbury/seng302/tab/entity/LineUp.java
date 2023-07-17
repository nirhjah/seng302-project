package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity(name="LineUp")
public class LineUp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lineUp_id;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "players",
            joinColumns = @JoinColumn(name = "lineUp_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> players = new HashSet<User>();

    @OneToOne
    @JoinColumn(name = "fk_formationId", referencedColumnName = "formationId")
    private Formation formation;

    @OneToOne
    @JoinColumn(name = "fk_teamId", referencedColumnName = "teamId")
    private Team team;

    /**
     * Default constructor for Line-up.
     * Required by JPA.
     */
    protected LineUp() {}

    /**
     * Constructs a LineUp with the specified formation, players and team.
     *
     * @param formation Formation object for which the line-up will be generated on
     * @param players   The players in the line-up. The order of the set determines the position on the line-up, where
     *                  first users in list are in the starting line-up, and then subs.
     * @param team      The team associated with the line-up.
     */
    public LineUp(Formation formation, Set<User> players, Team team) {
        this.formation = formation;
        this.players = players;
        this.team = team;
    }


    public Set<User> getPlayers() {
        return players;
    }

    public void setPlayers(Set<User> players) {
        this.players = players;
    }

    public Formation getFormation() {
        return formation;
    }

    public void setFormation(Formation formation) {
        this.formation = formation;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
