package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.helper.ImageType;
import nz.ac.canterbury.seng302.tab.helper.interfaces.HasImage;
import nz.ac.canterbury.seng302.tab.helper.interfaces.Identifiable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Class for Club object which is annotated as a JPA entity.
 */
@Entity(name = "Club")
public class Club implements Identifiable, HasImage {
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

    @Enumerated(value = EnumType.STRING)
    private ImageType logoType;

    @ManyToOne
    private User manager;

    protected Club() {
    }

    public Club(String name, Location location, String sport, User manager) throws IOException {
        this.name = name;
        this.location = location;
        this.sport = sport;
        this.manager = manager;

        Resource resource = new ClassPathResource("/static/image/icons/club-logo.svg");
        InputStream is = resource.getInputStream();
    }

    public ImageType getImageType() {
        return logoType;
    }
    public void setImageType(ImageType imageType) {
        logoType = imageType;
    }

    public long getClubId() {
        return clubId;
    }

    @Override
    public long getId() {
        return getClubId();
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

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }
}
