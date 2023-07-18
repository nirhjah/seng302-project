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

    protected Club() {
    }

    private static String defaultClubLogo;
    static {
        // Load default logo once in a shared location.
        try {
            defaultClubLogo = Base64.getEncoder().encodeToString(
                    new ClassPathResource("/static/image/icons/default-profile.png").getInputStream().readAllBytes()
            );
        } catch (IOException exception) {
            // crap!!!
        }
    }

    public Club(String name, Location location, String sport) {
        this.name = name;
        this.location = location;
        this.sport = sport;
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
        if (clubLogo == null) {
            return defaultClubLogo;
        }
        return clubLogo;
    }

    public void setClubLogo(String clubLogo) {
        this.clubLogo = clubLogo;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }


}
