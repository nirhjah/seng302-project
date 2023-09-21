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
import java.util.Set;

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
    void testFilteringClubByNameWithNoLocationOrSport() throws IOException {
        Club club = new Club("Rugby Club", location, "Rugby",null);
        clubService.updateOrAddClub(club);
        Page<Club> clubs = clubService.findClubFilteredByLocationsAndSports(Pageable.ofSize(1), List.of(), List.of("rugby"), "");
        
        Assertions.assertEquals(1, clubs.getContent().size());
        Assertions.assertEquals(List.of(club), clubs.getContent());
    }


    // @Test
    // void testFilteringClubByLocationWithNoNameOrSport() throws IOException {
    //     Club club = new Club("Rugby Club", location, "Rugby",null);
    //     List<String> filteredLocations = List.of();
    //     List<String> filteredSports = List.of("Rugby");
    //     Page<Club> clubs = clubService.findClubFilteredByLocationsAndSports(Pageable.ofSize(1), List.of(), List.of(), club.getName());
    //     
    //     Assertions.assertEquals(List.of(club), clubs);
    // }

    @Test
    void testGettingListOfSportsForClub() throws IOException {
        Club c = new Club("Test", location, "Rugby", null);
        Club c1 = new Club("Another one", location, "Hockey", null);
        Club c2 = new Club("Testing", location, "Soccer", null);
        Club c3 = new Club("hello", location, "Rugby", null);
        clubService.updateOrAddClub(c);
        clubService.updateOrAddClub(c1);
        clubService.updateOrAddClub(c2);
        clubService.updateOrAddClub(c3);

        List<String> sports = List.of(c.getSport(), c1.getSport(), c2.getSport());
        Assertions.assertTrue(sports.containsAll(clubService.getClubSports()));

    }

    @Test
    void testGettingListOfLocationsForClub() throws IOException {
        Location l1 = new Location("abc", "def", "hjk", "chch", "888", "NZ");
        Club c = new Club("Test", l1, "Rugby", null);
        Location l2 = new Location(null, null, null, "city", "111", "chch");
        Club c1 = new Club("Another one",l2 , "Hockey", null);
        Location l3 = new Location("fds", null, null, "city", "56", "chch");
        Club c2 = new Club("Testing", l3, "Soccer", null);
        clubService.updateOrAddClub(c);
        clubService.updateOrAddClub(c1);
        clubService.updateOrAddClub(c2);

        List<String> sports = List.of(c1.getLocation().getCity(), c.getLocation().getCity());
        Assertions.assertTrue(sports.containsAll(clubService.getClubCities()));

    }
    
}
