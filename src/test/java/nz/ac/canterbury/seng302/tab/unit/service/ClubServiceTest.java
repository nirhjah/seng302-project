package nz.ac.canterbury.seng302.tab.unit.service;

import nz.ac.canterbury.seng302.tab.entity.Club;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.service.ClubService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;


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

    @Test
    void testFilteringClubBySportWithNoLocationOrName() throws IOException {
        Club club = new Club("Rugby Club", location, "Rugby",null);
        clubService.updateOrAddClub(club);
        Page<Club> clubs = clubService.findClubFilteredByLocationsAndSports(Pageable.ofSize(1), List.of(), List.of("rugby"), "");
        
        Assertions.assertEquals(1, clubs.getContent().size());
        Assertions.assertEquals(List.of(club), clubs.getContent());
    }


    // @Test
    // void testFilteringClubByNameWithNoLocationOrSport() throws IOException {
    //     Club club = new Club("Rugby Club", location, "Rugby",null);
    //     List<String> filteredLocations = List.of();
    //     List<String> filteredSports = List.of("Rugby");
    //     Page<Club> clubs = clubService.findClubFilteredByLocationsAndSports(Pageable.ofSize(1), List.of(), List.of(), club.getName());
    //     
    //     Assertions.assertEquals(List.of(club), clubs);
    // }
    
}
