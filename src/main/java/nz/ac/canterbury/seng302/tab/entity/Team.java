package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;

@Entity
public class Team {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long teamId;

    @Column (nullable= false)
    private String name;

    @Column (nullable= false)
    private String location;

    @Column(nullable = false)
    private String sport;

    protected Team(){}

    public Team (String name, String location, String sport){
        this.name = name;
        this.location = location;
        this.sport= sport;
    }

    public Long getTeamId(){
        return this.teamId;
    }

    public String getName(){
        return this.name;
    }

    public String getLocation(){
        return this.location;
    }

    public String getSport(){
        return this.sport;
    }




}
