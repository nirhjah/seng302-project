package nz.ac.canterbury.seng302.tab.unit.service;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.service.LocationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
@Import(LocationService.class)
public class LocationServiceTest {
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private LocationService locationService;

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
