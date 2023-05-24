package nz.ac.canterbury.seng302.tab.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

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

    public Location (String addressLine1, String addressLine2, String suburb, String city, String postcode, String country ){
        this.addressLine1 = (addressLine1 == null || addressLine1.isBlank()) ? null : addressLine1;
        this.addressLine2 = (addressLine2 == null || addressLine2.isBlank()) ? null : addressLine2;
        this.suburb = (suburb == null || suburb.isBlank()) ? null : suburb;
        this.postcode = (postcode == null || postcode.isBlank()) ? null : postcode;
        this.country= country;
        this.city = city;
    }

    protected Location() {

    }

    public Long getLocationId() {
        return this.locationId;
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
        this.addressLine1 = (addressLine1 == null || addressLine1.equals("")) ? null : addressLine1;

    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = (addressLine2 == null || addressLine2.equals("")) ? null : addressLine2;

    }

    public void setSuburb(String suburb){
        this.suburb = (suburb == null || suburb.equals("")) ? null : suburb;

    }

    public void setCity(String city){
        this.city= city;
    }

    public void setPostcode(String postcode){
        this.postcode = (postcode == null || postcode.equals("")) ? null : postcode;
    }

    public void setCountry(String country){
        this.country= country;
    }

    public String toString() {
        return String.format(
            "%s %s %s, %s, %s, %s",
            addressLine1, addressLine2, suburb, city, postcode, country
        );
    }
}
