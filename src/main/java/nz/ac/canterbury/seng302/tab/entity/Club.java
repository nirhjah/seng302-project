package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Class for Club object which is annotated as a JPA entity.
 */
@Entity(name = "Club")
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clubId")
    private long clubId;

    @Column
    private String sport;

    @Column(nullable = false)
    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    private Location location;

    @Column(columnDefinition = "MEDIUMBLOB")
    private String clubLogo;

    protected Club() {
    }

    public Club(String name, Location location, String sport) throws IOException {
        this.name = name;
        this.location = location;
        this.sport = sport;
        Resource resource = new ClassPathResource("/static/image/default-profile.png");
        InputStream is = resource.getInputStream();
        this.clubLogo = Base64.getEncoder().encodeToString(is.readAllBytes());
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

    public String getClubLogo() {
        return clubLogo;
    }

    public void setClubLogo(String clubLogo) {
        this.clubLogo = clubLogo;
    }

    public String getSport() {
        return sport;
    }
}
