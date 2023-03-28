package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.repository.LocationRepository;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(SpringExtension.class)
@DataJpaTest
public class LocationServiceTest {
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private LocationService locationService;

    @Autowired
    private LocationRepository locationRepository;

    @BeforeEach
    public void beforeEach() {
        teamRepository.deleteAll();
        locationRepository.deleteAll();
    }

    @Test
    public void testGettingLocationList() throws IOException {
        List<Location> locationList = locationService.getLocationList();
        assertTrue(locationList.isEmpty());
        Location testLocation = new Location("addressline1:", "addressline2", "suburb", "Christchurch", "postcode", "New Zealand");
        Team team = new Team("test", "Hockey", testLocation);
        teamRepository.save(team);
        assertEquals(testLocation.getLocationId(),teamRepository.findById(team.getTeamId()).get().getLocation().getLocationId());
    }

    @Test
    public void testAddingLocation() throws IOException {
        Location location = new Location ("addressline1", "addressline2", "suburb", "city", "postcode", "country");
        Team team = new Team("test", "Hockey", location);
        teamRepository.save(team);
        assertEquals(location.getAddressLine1(), teamRepository.findById(team.getTeamId()).get().getLocation().getAddressLine1());
        assertEquals(location.getAddressLine2(), teamRepository.findById(team.getTeamId()).get().getLocation().getAddressLine2());
        assertEquals(location.getSuburb(), teamRepository.findById(team.getTeamId()).get().getLocation().getSuburb());
        assertEquals(location.getPostcode(), teamRepository.findById(team.getTeamId()).get().getLocation().getPostcode());
        assertEquals(location.getCity(), teamRepository.findById(team.getTeamId()).get().getLocation().getCity());
        assertEquals(location.getCountry(), teamRepository.findById(team.getTeamId()).get().getLocation().getCountry());

    }
}
