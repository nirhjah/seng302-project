package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/**
 * Class for Club object which is annotated as a JPA entity.
 */
@Entity(name = "Club")
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clubId")
    private long clubId;
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String sport;

    @ManyToOne(cascade = CascadeType.ALL)
    private Location location;

    @Column(columnDefinition = "MEDIUMBLOB")
    private String clubLogo;

    @ManyToOne
    private User manager;

    private boolean hasCustomLogo;

    protected Club() {
    }

    public Club(String name, Location location, String sport, User manager) throws IOException {
        this.name = name;
        this.location = location;
        this.sport = sport;
        this.manager = manager;

        Resource resource = new ClassPathResource("/static/image/icons/club-logo.svg");
        InputStream is = resource.getInputStream();
        this.clubLogo = Base64.getEncoder().encodeToString(is.readAllBytes());
    }

    public Club(String name, Location location, String sport, User manager, String clubLogo) {
        this.name = name;
        this.location = location;
        this.sport = sport;
        this.manager = manager;
        this.clubLogo=clubLogo;
    }

    public long getClubId() {
        return clubId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public User getManager() {
        return manager;
    }

    public boolean isManagedBy(User user) {
        return user.getUserId() == getManager().getUserId();
    }

    public void setClubLogo(String clubLogo) {
        this.clubLogo = clubLogo;
    }

    public String getClubLogo(){
        return this.clubLogo;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public boolean getHasCustomLogo(){
        return this.hasCustomLogo;
    }

    public void setHasCustomLogo(boolean flag){
        this.hasCustomLogo= flag;
    }
}
