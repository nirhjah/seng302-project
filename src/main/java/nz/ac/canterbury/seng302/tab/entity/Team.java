package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

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

    public Team(String name, String location, String sport) throws IOException {
        this.name = name;
        this.location = location;
        this.sport = sport;
        //Retrieving the default profile image and converting it to byte array string to be stored in database
        Resource resource = new ClassPathResource("/static/image/default-profile.png");
        File file = resource.getFile();
        this.pictureString = Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
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

    public void setTeamId(long teamId){
        this.teamId=teamId;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }



}
