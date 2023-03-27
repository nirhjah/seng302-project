package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class LocationServiceTest {

    @Autowired
    private LocationService locationService;

    @Autowired
    private LocationRepository locationRepository;

    @BeforeEach
    public void beforeEach() {
        locationRepository.deleteAll();
    }

    @Test
    public void testGettingLocationList(){
        List<Location> locationList = locationService.getLocationList();
        assertTrue(locationList.isEmpty());
        Location location = new Location("addressline1:", "addressline2", "suburb", "Christchurch", "postcode", "New Zealand");
        Location location1 = new Location("addressline1:", "addressline2", "suburb", "Christchurch", "postcode", "New Zealand");
        Location location2 = new Location("addressline1:", "addressline2", "suburb", "Christchurch", "postcode", "New Zealand");
        List<Location> list = Arrays.asList(location, location1, location2);

        locationRepository.save(location);
        locationRepository.save(location1);
        locationRepository.save(location2);
        assertEquals(list.toString(), locationRepository.findAll().toString());
    }

    @Test
    public void testAddingLocation(){
        Location location = new Location ("addressline1", "addressline2", "suburb", "city", "postcode", "country");
        locationService.addLocation(location);
        assertEquals(location.getAddressLine1(), locationRepository.findById(location.getLocationId()).get().getAddressLine1());
        assertEquals(location.getAddressLine2(), locationRepository.findById(location.getLocationId()).get().getAddressLine2());
        assertEquals(location.getSuburb(), locationRepository.findById(location.getLocationId()).get().getSuburb());
        assertEquals(location.getPostcode(), locationRepository.findById(location.getLocationId()).get().getPostcode());
        assertEquals(location.getCity(), locationRepository.findById(location.getLocationId()).get().getCity());
        assertEquals(location.getCountry(), locationRepository.findById(location.getLocationId()).get().getCountry());

    }
}
