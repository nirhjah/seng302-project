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

    public Location (String address, String suburb, String city, long postcode,String country ){
        this.address= address;
        this.suburb= suburb;
        this.city=city;
        this.postcode= postcode;
        this.country= country;
    }

    protected Location() {
    }

    public String getAddress(){
        return this.address;
    }

    public String getSuburb(){
        return this.suburb;
    }

    public String getCity(){
        return this.city;
    }

    public long getPostcode(){
        return this.postcode;
    }

    public String getCountry(){
        return this.country;
    }

    public void setAddress(String address){
        this.address=address;
    }

    public void setSuburb(String suburb){
        this.suburb= suburb;
    }

    public void setCity(String city){
        this.city= city;
    }

    public void setPostcode(long postcode){
        this.postcode= postcode;
    }

    public void setCountry(String country){
        this.country= country;
    }

    public String toString(){
        return this.address + " " + this.suburb + ", " + this.city + ", " + this.postcode + ", "+ this.country;
    }
}
