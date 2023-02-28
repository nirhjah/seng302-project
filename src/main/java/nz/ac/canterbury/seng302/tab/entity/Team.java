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
    private String photo;

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
        this.photo=photo;
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

    public String getPhoto() {
        return this.photo;
    }

    public void setPhoto(String photo){
        this.photo = photo;
    }

    /**
     * @param teamId Team entity's primary key, team id
     * @param photo Filename of profile picture
     * @return  The file path of profile picture
     */
    @Transient
    public String getPhotosImagePath(long teamId, String photo) {
        return "resources/image/" + teamId+ "/" + photo;
    }


}
