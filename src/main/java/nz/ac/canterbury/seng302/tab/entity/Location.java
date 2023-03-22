package nz.ac.canterbury.seng302.tab.entity;


import jakarta.persistence.*;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long locationId;

    @Column
    private String addressLine1;

    @Column
    private String addressLine2;

    @Column
    private String suburb;

    @Column
    private String postcode;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String country;

    public Location (String addressLine1, String addressLine2, String suburb, String city, String postcode,String country ){
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.suburb= suburb;
        this.city=city;
        this.postcode= postcode;
        this.country= country;
    }

    protected Location() {

    }

    public String getAddressLine1(){
        return this.addressLine1;
    }

    public String getAddressLine2(){
        return this.addressLine2;
    }

    public String getSuburb(){
        return this.suburb;
    }

    public String getCity(){
        return this.city;
    }

    public String getPostcode(){
        return this.postcode;
    }

    public String getCountry(){
        return this.country;
    }

    public void setAddressLine1(String addressLine1){
        this.addressLine1=addressLine1;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2=addressLine2;
    }

    public void setSuburb(String suburb){
        this.suburb= suburb;
    }

    public void setCity(String city){
        this.city= city;
    }

    public void setPostcode(String postcode){
        this.postcode= postcode;
    }

    public void setCountry(String country){
        this.country= country;
    }

    public String toString() {
        return Stream.of(this.addressLine1, this.addressLine2, this.suburb)
                .filter(Objects::nonNull)
                .collect(java.util.stream.Collectors.joining(" ")) +
                (this.suburb != null ? ", " + this.suburb : "") +
                ", " + this.city + ", " + this.postcode + ", " + this.country;
    }
}
