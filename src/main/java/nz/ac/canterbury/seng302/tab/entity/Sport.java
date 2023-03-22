package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;


/**
 * Sport entity for storing information about sports.
 */

@Entity
public class Sport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sportId;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "Id")
    private User user;

    public Sport(String name) {
        this.name = name;
    }

    public Sport() {}

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
