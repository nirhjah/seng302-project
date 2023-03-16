package nz.ac.canterbury.seng302.tab.entity;


import jakarta.persistence.*;

@Entity
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long locationId;

    @Column(nullable = false)
    private String address;

    @Column
    private String suburb;

    @Column(nullable = false)
    private String city;

    @Column
    private long postcode;
    @Column(nullable = false)
    private String country;

//    @OneToOne(mappedBy = "locations")
//    private Team team;

    public Location (String address, String suburb, String city, long postcode,String country ){
        this.address= address;
        this.suburb= suburb;
        this.city=city;
        this.postcode= postcode;
        this.country= country;
    }

    protected Location() {

    }
}
