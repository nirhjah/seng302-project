package nz.ac.canterbury.seng302.tab.entity;

import io.cucumber.java.bs.I;
import jakarta.persistence.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

/**
 * Class for Team object which is annotated as a JPA entity.
 */
@Entity
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamId;

    @ManyToOne(cascade = CascadeType.ALL)
    private Location location;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String sport;

    @Column(columnDefinition = "MEDIUMBLOB")
    private String pictureString;

    @Column(nullable = true)
    private Date creationDate;

    @ManyToMany
    @JoinTable(
            name = "team_members",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> teamMembers = new HashSet<User>();

    protected Team() {
    }

    public Team(String name, String sport, Location location) throws IOException {
        this.name = name;
        this.location = location;
        this.sport = sport;
        Resource resource = new ClassPathResource("/static/image/default-profile.png");
        InputStream is = resource.getInputStream();
        this.pictureString = Base64.getEncoder().encodeToString(is.readAllBytes());
        this.creationDate = new Date();
    }

    /**
     * Should be used for testing ONLY!
     * TODO: Remove this constructor, use builder pattern. same for user
     * @param name
     * @param sport
     */
    public Team(String name, String sport) throws IOException {
        this.name = name;
        // create a dummy location
        this.location = new Location("address1", "address2", "suburb", "chch", "8052", "new zealand");
        this.sport = sport;
        Resource resource = new ClassPathResource("/static/image/default-profile.png");
        InputStream is = resource.getInputStream();
        this.pictureString = Base64.getEncoder().encodeToString(is.readAllBytes());
        this.creationDate = new Date();
    }

    public Long getTeamId() {
        return this.teamId;
    }

    public String getName() {
        return this.name;
    }

    public String getSport() {
        return this.sport;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public Date getCreationDate() {return creationDate;}

    public Set<User> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(Set<User> teamMembers) {
        this.teamMembers = teamMembers;
    }

}
