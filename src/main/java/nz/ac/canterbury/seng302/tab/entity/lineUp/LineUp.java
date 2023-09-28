package nz.ac.canterbury.seng302.tab.entity.lineUp;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Formation;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Line-up entity for describing a line-up and its relationships
 */
@Entity(name="LineUp")
public class LineUp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lineUpId;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_formationId", referencedColumnName = "formationId")
    private Formation formation;

    @OneToOne
    @JoinColumn(name = "fk_teamId", referencedColumnName = "teamId")
    private Team team;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_activityId", referencedColumnName = "activityId")
    private Activity activity;

    @Column
    private String lineUpName;

    @ManyToMany
    @JoinTable(
            name = "lineup_subs",
            joinColumns = @JoinColumn(name = "lineup_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> subs;

    public Set<User> getSubs() {
        if (subs == null) {
            subs = new HashSet<>();
        }
        return subs;
    }

    public void setSubs(Set<User> subs) {
        this.subs = subs;
    }

    /**
     * Default constructor for Line-up.
     * Required by JPA.
     */
    public LineUp() {}

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
        this.subs = new HashSet<>();
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

    public Long getLineUpId() {
        return lineUpId;
    }

    public void setLineUpId(Long lineUpId) {
        this.lineUpId = lineUpId;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public String getLineUpName() {
        return lineUpName;
    }

    public void setLineUpName(String lineUpName) {
        this.lineUpName = lineUpName;
    }

    @Override
    public String toString() {
        // Return the line-up name if set
        if (lineUpName != null && !lineUpName.isBlank()) {
            return lineUpName;
        } else {
            // If not, return the formation's name
            return formation.toString();
        }
    }

}
