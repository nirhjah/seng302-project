package nz.ac.canterbury.seng302.tab.entity.lineUp;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Formation;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;

import java.util.HashSet;
import java.util.Set;

@Entity(name="LineUp")
public class LineUp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lineUpId;


    @OneToOne
    @JoinColumn(name = "fk_formationId", referencedColumnName = "formationId")
    private Formation formation;

    @OneToOne
    @JoinColumn(name = "fk_teamId", referencedColumnName = "teamId")
    private Team team;

    @OneToOne
    @JoinColumn(name = "fk_activityId", referencedColumnName = "activityId")
    private Activity activity;
    /**
     * Default constructor for Line-up.
     * Required by JPA.
     */
    protected LineUp() {}

    /**
     * Constructs a LineUp with the specified formation, players and team.
     *
     * @param formation Formation object for which the line-up will be generated on
     * @param team      The team associated with the line-up.
     * @param activity  The activity the lineup is associated with
     */
    public LineUp(Formation formation, Team team, Activity activity) {
        this.formation = formation;
        this.team = team;
        this.activity = activity;
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
