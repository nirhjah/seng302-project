package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;

@Entity
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String sport;

    @Column(columnDefinition = "MEDIUMBLOB")
    private String pictureString;

    protected Team() {
    }

    public Team(String name, String location, String sport) {
        this.name = name;
        this.location = location;
        this.sport = sport;
    }

    public Team(String name, String location, String sport, String pictureString) {
        this.name = name;
        this.location = location;
        this.sport = sport;
        this.pictureString = pictureString;
    }

    public Long getTeamId() {
        return this.teamId;
    }

    public String getName() {
        return this.name;
    }

    public String getLocation() {
        return this.location;
    }

    public String getSport() {
        return this.sport;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String getPictureString() {
        return this.pictureString;
    }

    public void setPictureString(String pictureString) {
        this.pictureString = pictureString;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }
}
