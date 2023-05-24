package nz.ac.canterbury.seng302.tab.unit.entity;

import nz.ac.canterbury.seng302.tab.entity.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class LocationTest {

    public Location location;

    @BeforeEach
    public void beforeEach() {
        this.location = new Location ("addressline1", "addressline2", "suburb", "city", "postcode", "country");
    }

    @Test
    public void testLocationConstructor(){
        assertEquals("addressline1", this.location.getAddressLine1());
        assertEquals("addressline2", this.location.getAddressLine2());
        assertEquals("suburb", this.location.getSuburb());
        assertEquals("city", this.location.getCity());
        assertEquals("postcode", this.location.getPostcode());
        assertEquals("country", this.location.getCountry());
    }

    @Test
    public void testGettingAddressLine1(){
        assertEquals("addressline1", location.getAddressLine1());
        this.location.setAddressLine1("updated addressline1");
        assertEquals("updated addressline1", this.location.getAddressLine1());
    }

    @Test
    public void testGettingAddressLine2(){
        assertEquals("addressline2", this.location.getAddressLine2());
        this.location.setAddressLine2("updated addressline2");
        assertEquals("updated addressline2", this.location.getAddressLine2());
    }

    @Test
    public void testGettingSuburb(){
        assertEquals("suburb", this.location.getSuburb());
        this.location.setSuburb("updated suburb");
        assertEquals("updated suburb", this.location.getSuburb());
    }

    @Test
    public void testGettingCity(){
        assertEquals("city", this.location.getCity());
        this.location.setCity("updated city");
        assertEquals("updated city", this.location.getCity());
    }

    @Test
    public void testGettingPostcode(){
        assertEquals("postcode", this.location.getPostcode());
        this.location.setPostcode("updated postcode");
        assertEquals("updated postcode", this.location.getPostcode());
    }

    @Test
    public void testGettingCountry(){
        assertEquals("country", this.location.getCountry());
        this.location.setCountry("updated country");
        assertEquals("updated country", this.location.getCountry());
    }

    @Test
    public void testGettingLocationString(){
        assertEquals("addressline1 addressline2 suburb, city, postcode, country", this.location.toString());
    }
}
