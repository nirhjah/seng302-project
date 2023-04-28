package nz.ac.canterbury.seng302.tab.entity;


import jakarta.persistence.*;
import java.util.Objects;


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
        this.addressLine1 = (addressLine1 == null || addressLine1.equals("")) ? null : addressLine1;
        this.addressLine2 = (addressLine2 == null || addressLine2.equals("")) ? null : addressLine2;
        this.suburb = (suburb == null || suburb.equals("")) ? null : suburb;
        this.postcode = (postcode == null || postcode.equals("")) ? null : postcode;
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
        String output = "";
        int i = 1;
        for (String value : new String[] {addressLine1, addressLine2, suburb, city, postcode, country}) {
            if (value != null && value.length() > 0) {
                output += value;
                output += i > 2 && i < 6 ? ", " : " ";
            }
            i++;
        }
        return output;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (!Objects.equals(locationId, location.locationId)) return false;
        if (getAddressLine1() != null ? !getAddressLine1().equals(location.getAddressLine1()) : location.getAddressLine1() != null)
            return false;
        if (getAddressLine2() != null ? !getAddressLine2().equals(location.getAddressLine2()) : location.getAddressLine2() != null)
            return false;
        if (getSuburb() != null ? !getSuburb().equals(location.getSuburb()) : location.getSuburb() != null)
            return false;
        if (getPostcode() != null ? !getPostcode().equals(location.getPostcode()) : location.getPostcode() != null)
            return false;
        if (getCity() != null ? !getCity().equals(location.getCity()) : location.getCity() != null) return false;
        return getCountry() != null ? getCountry().equals(location.getCountry()) : location.getCountry() == null;
    }

    @Override
    public int hashCode() {
        int result = locationId != null ? locationId.hashCode() : 0;
        result = 31 * result + (getAddressLine1() != null ? getAddressLine1().hashCode() : 0);
        result = 31 * result + (getAddressLine2() != null ? getAddressLine2().hashCode() : 0);
        result = 31 * result + (getSuburb() != null ? getSuburb().hashCode() : 0);
        result = 31 * result + (getPostcode() != null ? getPostcode().hashCode() : 0);
        result = 31 * result + (getCity() != null ? getCity().hashCode() : 0);
        result = 31 * result + (getCountry() != null ? getCountry().hashCode() : 0);
        return result;
    }
}
