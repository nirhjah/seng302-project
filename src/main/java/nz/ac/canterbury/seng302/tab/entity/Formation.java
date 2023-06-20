package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;

/**
 * The Formation entity which contains the information involved with the formation.
 */
@Entity(name="Formation")
public class Formation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "formationId")
    private long formationId;

    @Column(nullable = false)
    private String formation;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_teamID", referencedColumnName = "teamId")
    private Team team;

    /**
     * Default constructor for Formation.
     * Required by JPA.
     */
    protected Formation() {}


    /**
     * Constructs a Formation with the specified formation string and team.
     *
     * @param formation The formation string.
     * @param team      The team associated with the formation.
     */
    public Formation(String formation,  Team team) {
        this.formation = formation;
        this.team = team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public void setFormation(String formation) {
        this.formation = formation;
    }

    public Team getTeam(){
        return this.team;
    }

    public String getFormation(){
        return this.formation;
    }

    public long getFormationId(){ return this.formationId;}


}
