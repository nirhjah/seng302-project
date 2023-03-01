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

    @Column(nullable = false, length = 64)
    private String picture="/image/default-profile.png";

    protected Team(){}

    public Team (String name, String location, String sport){
        this.name = name;
        this.location = location;
        this.sport= sport;
    }

    public Team (String name, String location, String sport, String photo){
        this.name = name;
        this.location = location;
        this.sport= sport;
        this.picture=picture;
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

    public String getPicturePath() {
        return this.picture;
    }

    public void setPicturePath(String picture){
        this.picture = picture;
    }



}
