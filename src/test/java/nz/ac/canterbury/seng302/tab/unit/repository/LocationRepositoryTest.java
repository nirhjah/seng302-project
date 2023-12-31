package nz.ac.canterbury.seng302.tab.unit.repository;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.repository.LocationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DataJpaTest
public class LocationRepositoryTest {

    @Autowired
    private LocationRepository locationRepository;

    @Test
    public void testGettingLocationById(){
        Location testLocation = new Location("addressline1:", "addressline2", "suburb", "Christchurch", "postcode", "New Zealand");
        testLocation = locationRepository.save(testLocation);
        assertEquals(testLocation.getAddressLine1(), locationRepository.findById(testLocation.getLocationId()).get().getAddressLine1());
        assertEquals(testLocation.getAddressLine2(), locationRepository.findById(testLocation.getLocationId()).get().getAddressLine2());
        assertEquals(testLocation.getSuburb(), locationRepository.findById(testLocation.getLocationId()).get().getSuburb());
        assertEquals(testLocation.getPostcode(), locationRepository.findById(testLocation.getLocationId()).get().getPostcode());
        assertEquals(testLocation.getCity(), locationRepository.findById(testLocation.getLocationId()).get().getCity());
        assertEquals(testLocation.getCountry(), locationRepository.findById(testLocation.getLocationId()).get().getCountry());
    }

    @Test
    public void testGettingLocationList(){
        Location location = new Location("addressline1:", "addressline2", "suburb", "Christchurch", "postcode", "New Zealand");
        Location location1 = new Location("addressline1:", "addressline2", "suburb", "Christchurch", "postcode", "New Zealand");
        Location location2 = new Location("addressline1:", "addressline2", "suburb", "Christchurch", "postcode", "New Zealand");
        List<Location> list = Arrays.asList(location, location1, location2);

        locationRepository.save(location);
        locationRepository.save(location1);
        locationRepository.save(location2);

        assertEquals(list.toString(), locationRepository.findAll().toString());
    }
}
