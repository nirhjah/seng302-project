package nz.ac.canterbury.seng302.tab.unit.service;

import nz.ac.canterbury.seng302.tab.entity.Club;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.service.ClubService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.io.IOException;

@DataJpaTest
@Import(ClubService.class)
public class ClubServiceTest {

    @Autowired
    private ClubService clubService;

    Location location = new Location(null, null, null, "Christchurch", null,
            "New Zealand");

    @Test
    void testFindClubById() throws IOException {
        Club club = new Club("Rugby Club", location, "soccer",null);
        clubService.updateOrAddClub(club);
        Assertions.assertEquals(clubService.findClubById(1L).get(), club);
    }

    @Test
    void testUpdatingOrAddingClub() throws IOException {
        Club club = new Club("Rugby Club", location, "Rugby",null);
        clubService.updateOrAddClub(club);
        Assertions.assertEquals(clubService.findAll().size(), 1);
    }
}
