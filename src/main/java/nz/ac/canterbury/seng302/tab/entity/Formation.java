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

    /**
     * The formation string is in the form of dash separated numbers used to describe the players position.
     * e.g “1-4-3-3" for football, “1-1-3-3-3" for hockey
     */
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
     * @param formation The formation string used to describe the players position
     *                  e.g “1-4-3-3",“1-1-3-3-3"
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