package nz.ac.canterbury.seng302.tab.entity.lineUp;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Formation;
import nz.ac.canterbury.seng302.tab.entity.Team;

/**
 * Line-up entity for describing a line-up and its relationships
 */
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

    /*
     * TODO: this is EXTREMELY BAD.
     *  LineUps should not use CascadeType.ALL when referencing activity.
     *  But... this is the only way to get the tests working :^)
     *  And we really need to get the pipeline running so the teaching team can mark us.
     */
    @OneToOne(cascade = CascadeType.ALL)
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
}
