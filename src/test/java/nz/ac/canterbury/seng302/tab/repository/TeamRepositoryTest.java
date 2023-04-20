package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@Import(TeamService.class)
public class TeamRepositoryTest {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private TeamService teamService;

    @Test
    public void testGettingTeamById() throws IOException {
        List<Team> teamList = teamService.getTeamList();
        assertTrue(teamList.isEmpty());
        Location testLocation = new Location(null, null, null, "Christchurch", null, "New Zealand");
        Team team = new Team("test", "Hockey", testLocation);
        teamRepository.save(team);
        assertEquals(team.getTeamId(), teamRepository.findById(team.getTeamId()).get().getTeamId());
        assertEquals(team.getLocation().getAddressLine2(), teamRepository.findById(team.getTeamId()).get().getLocation().getAddressLine1());
        assertEquals(team.getSport(), teamRepository.findById(team.getTeamId()).get().getSport());
        assertEquals(team.getName(), teamRepository.findById(team.getTeamId()).get().getName());

    }

    @Test
    public void testGettingTeamList() throws IOException {
        assertTrue(teamService.getTeamList().isEmpty());
        Location testLocation = new Location(null, null, null, "Christchurch", null, "New Zealand");

        Team team = new Team("test", "Hockey", testLocation);
        Team team2= new Team ("test2", "Netball", testLocation);

        List<Team> expectedTeamList = List.of(team); // We will filter the search by hockey so only the first team is expected
        teamRepository.save(team);
        teamRepository.save(team2);
        List<String> searchedSports = new ArrayList<String>();
        searchedSports.add("Hockey");
        assertEquals(expectedTeamList.toString(), teamRepository.findTeamByNameAndSportIn(PageRequest.of(0,10), searchedSports, "").toList().toString());

    }

    @Test void filteringSearchBySport_filteringAllTeamsByHockeyAndNetball_correctListReturned() throws IOException {
        assertTrue(teamService.getTeamList().isEmpty());
        Location testLocation = new Location(null, null, null, "Christchurch", null, "New Zealand");

        Team team = new Team("test", "Hockey", testLocation);
        Team team2= new Team ("test2", "Netball", testLocation);

        List<Team> expectedTeamList = List.of(team, team2); // We will filter the search by hockey so only the first team is expected
        teamRepository.save(team);
        teamRepository.save(team2);
        List<String> searchedSports = new ArrayList<String>();
        searchedSports.add("Hockey");
        searchedSports.add("Netball");
        assertEquals(expectedTeamList.toString(), teamRepository.findTeamByNameAndSportIn(PageRequest.of(0,10), searchedSports, "").toList().toString());
    }

    @Test void filteringSearchBySport_filteringTeamsNamedTestByHockey_correctListReturned() throws IOException {
        assertTrue(teamService.getTeamList().isEmpty());
        Location testLocation = new Location(null, null, null, "Christchurch", null, "New Zealand");

        Team team = new Team("test",  "Hockey", testLocation);
        Team team2= new Team ("test2", "Netball", testLocation);
        Team team3 = new Team("Team", "Hockey", testLocation);

        List<Team> expectedTeamList = List.of(team); // We will filter the search by hockey so only the first team is expected
        teamRepository.save(team);
        teamRepository.save(team2);
        teamRepository.save(team3);
        List<String> searchedSports = new ArrayList<String>();
        searchedSports.add("Hockey");
        assertEquals(expectedTeamList.toString(), teamRepository.findTeamByNameAndSportIn(PageRequest.of(0,10), searchedSports, "test").toList().toString());
    }

    @Test
    public void filteringTeamsByCities_noCitiesSelected_allCitiesDisplayed() throws IOException {
        List<Team> teamList = teamService.getTeamList();
        assertTrue(teamList.isEmpty());
        Team team1 = new Team("team1", "Hockey", new Location("123 Test1 road", "", "Suburb1", "Christchurch", "1111", "NZ"));
        Team team2 = new Team("team2", "Hockey", new Location("213 Test2 road", "", "Suburb2", "Dunedin", "1111", "NZ"));
        Team team3 = new Team("team3", "Hockey", new Location("321 Test3 road", "", "Suburb3", "Auckland", "1111", "NZ"));

        List<Team> expectedTeams = Arrays.asList(team1, team2, team3);
        teamRepository.save(team1);
        teamRepository.save(team2);
        teamRepository.save(team3);

        ArrayList<String> filteredLocations = new ArrayList<>();

        assertEquals(expectedTeams.toString(), teamRepository.findTeamByFilteredLocations(filteredLocations, PageRequest.of(0,10), "team").toList().toString());

    }

    @Test
    public void filteringTeamsByCities_oneCitySelected_allTeamsWithCityDisplayed() throws IOException {
        final String WANTED_CITY = "Christchurch";
        List<Team> teamList = teamService.getTeamList();
        assertTrue(teamList.isEmpty());
        Team team1 = new Team("team1", "Hockey", new Location("123 Test1 road", "", "Suburb1", WANTED_CITY, "1111", "NZ"));
        Team team2 = new Team("team2", "Hockey", new Location("213 Test2 road", "", "Suburb2", "Dunedin", "1111", "NZ"));
        Team team3 = new Team("team3", "Hockey", new Location("321 Test3 road", "", "Suburb3", "Auckland", "1111", "NZ"));

        teamRepository.save(team1);
        teamRepository.save(team2);
        teamRepository.save(team3);

        List<Team> expectedTeams = Arrays.asList(team1);

        ArrayList<String> filteredLocations = new ArrayList<>();
        filteredLocations.add(WANTED_CITY);

        assertEquals(expectedTeams.toString(), teamRepository.findTeamByFilteredLocations(filteredLocations, PageRequest.of(0,10), "team").toList().toString());

    }

    @Test
    public void filteringTeamsByCities_twoCitiesSelected_allTeamsWithThoseCitiesDisplayed() throws IOException {
        final String WANTED_CITY1 = "Christchurch";
        final String WANTED_CITY2 = "Auckland";

        List<Team> teamList = teamService.getTeamList();
        assertTrue(teamList.isEmpty());
        Team team1 = new Team("team1", "Hockey", new Location("123 Test1 road", "", "Suburb1", WANTED_CITY1, "1111", "NZ"));
        Team team2 = new Team("team2", "Hockey", new Location("213 Test2 road", "", "Suburb2", "Dunedin", "1111", "NZ"));
        Team team3 = new Team("team3", "Hockey", new Location("321 Test3 road", "", "Suburb3", WANTED_CITY2, "1111", "NZ"));

        List<Team> expectedTeams = Arrays.asList(team1, team3);

        teamRepository.save(team1);
        teamRepository.save(team2);
        teamRepository.save(team3);

        ArrayList<String> filteredLocations = new ArrayList<>();
        filteredLocations.add(WANTED_CITY1);
        filteredLocations.add(WANTED_CITY2);

        assertEquals(expectedTeams.toString(), teamRepository.findTeamByFilteredLocations(filteredLocations, PageRequest.of(0,10), "team").toList().toString());

    }
    @Test
    public void filteringTeamsByCities_twoCitiesSelectedAndTeamNamesDifferent_displayCitiesAndTeams() throws IOException {

        final String WANTED_CITY1 = "Christchurch";
        final String WANTED_CITY2 = "Dunedin";

        List<Team> teamList = teamService.getTeamList();
        assertTrue(teamList.isEmpty());
        Team team1 = new Team("team1", "Hockey", new Location("123 Test1 road", "", "Suburb1", WANTED_CITY1, "1111", "NZ"));
        Team team2 = new Team("team2", "Hockey", new Location("213 Test2 road", "", "Suburb2", WANTED_CITY1, "1111", "NZ"));
        Team team3 = new Team("test3", "Hockey", new Location("321 Test3 road", "", "Suburb3", WANTED_CITY2, "1111", "NZ"));
        Team team4 = new Team("test4", "Hockey", new Location("222 Test4 road", "", "Suburb4", WANTED_CITY2, "1111", "NZ"));

        List<Team> expectedTeams = Arrays.asList(team1, team2);
        teamRepository.save(team1);
        teamRepository.save(team2);
        teamRepository.save(team3);
        teamRepository.save(team4);

        ArrayList<String> filteredLocations = new ArrayList<>();
        filteredLocations.add(WANTED_CITY1);
        filteredLocations.add(WANTED_CITY2);

        assertEquals(expectedTeams.toString(), teamRepository.findTeamByFilteredLocations(filteredLocations, PageRequest.of(0,10), "team").toList().toString());
    }

    @Test
    public void filteringTeamsByCities_allCitiesSelected_allCitiesDisplayed() throws IOException {
        final String WANTED_CITY1 = "Christchurch";
        final String WANTED_CITY2 = "Dunedin";
        final String WANTED_CITY3 = "Auckland";

        List<Team> teamList = teamService.getTeamList();
        assertTrue(teamList.isEmpty());
        Team team1 = new Team("team1", "Hockey", new Location("123 Test1 road", "", "Suburb1", WANTED_CITY1, "1111", "NZ"));
        Team team2 = new Team("team2", "Hockey", new Location("213 Test2 road", "", "Suburb2", WANTED_CITY1, "1111", "NZ"));
        Team team3 = new Team("team3", "Hockey", new Location("321 Test3 road", "", "Suburb3", WANTED_CITY2, "1111", "NZ"));
        Team team4 = new Team("team4", "Hockey", new Location("222 Test4 road", "", "Suburb4", WANTED_CITY2, "1111", "NZ"));


        List<Team> expectedTeams = Arrays.asList(team1, team2, team3, team4);

        teamRepository.save(team1);
        teamRepository.save(team2);
        teamRepository.save(team3);
        teamRepository.save(team4);

        ArrayList<String> filteredLocations = new ArrayList<>();
        filteredLocations.add(WANTED_CITY1);
        filteredLocations.add(WANTED_CITY2);
        filteredLocations.add(WANTED_CITY3);

        assertEquals(expectedTeams.toString(), teamRepository.findTeamByFilteredLocations(filteredLocations, PageRequest.of(0,10), "team").toList().toString());

    }

    @Test
    void filteringSearchBySport_filteringAllTeamsAndNoSports_correctListReturned() throws IOException {
        assertTrue(teamService.getTeamList().isEmpty());
        Location testLocation = new Location(null, null, null, "Christchurch", null, "New Zealand");

        Team team1 = new Team("test", "Hockey", testLocation);
        Team team2 = new Team("test2", "Netball", testLocation);
        Team team3 = new Team("Team", "Hockey", testLocation);

        List<Team> expectedTeamList = List.of(team3, team1, team2); // We will filter the search by hockey so only the first team is expected
        teamRepository.save(team1);
        teamRepository.save(team2);
        teamRepository.save(team3);
        List<String> searchedSports = new ArrayList<>();
        assertEquals(expectedTeamList.toString(), teamRepository.findTeamByNameAndSportIn(PageRequest.of(0,10), searchedSports, "").toList().toString());
    }


    @Test
    void filteringByCityAndSport_filterByOneSportAndCity_correctListReturned() throws Exception {
        Location testLocation = new Location(null, null, null, "Christchurch", null, "New Zealand");

        Team team1 = new Team("test1", "Hockey", testLocation);
        Team team2 = new Team("test2", "Netball", testLocation);
        Team team3 = new Team("test3", "Hockey", testLocation);
        teamRepository.save(team1);
        teamRepository.save(team2);
        teamRepository.save(team3);

        var sports = List.of("Netball");
        var cities = List.of("Christchurch");
        var pageable = PageRequest.of(0, 10);
        var output = teamService.findPaginatedTeamsByCityAndSports(pageable, cities, sports, "").toList();

        assertEquals(1, output.size());
        assertEquals(team2.getName(), output.get(0).getName());
    }

    @Test
    void filteringByCityAndSport_filterByTwoSportsAndOneCity_correctListReturned() throws Exception {
        Location testLocation = new Location(null, null, null, "Christchurch", null, "New Zealand");

        Team team1 = new Team("test1", "Hockey", testLocation);
        Team team2 = new Team("test2", "Netball", testLocation);
        Team team3 = new Team("test3", "Hockey", testLocation);
        Team team4 = new Team("test4", "CheeseEating", testLocation);
        teamRepository.save(team1);
        teamRepository.save(team2);
        teamRepository.save(team3);
        teamRepository.save(team4);

        var sports = List.of("Netball", "Hockey");
        var cities = List.of("Christchurch");
        var pageable = PageRequest.of(0, 10);
        var output = teamService.findPaginatedTeamsByCityAndSports(pageable, cities, sports, "").toSet();

        assertEquals(3, output.size());
        assertEquals(Set.of(team1, team2, team3), output);
    }

    @Test
    void filteringByCityAndSport_filterByTwoSportsAndTwoCities_correctListReturned() throws Exception {
        Location chchLocation = new Location(null, null, null, "Christchurch", null, "New Zealand");
        Location auckLocation = new Location(null, null, null, "Auckland", null, "New Zealand");

        Team team1 = new Team("test1", "Hockey", chchLocation);
        Team team2 = new Team("test2", "Hockey", auckLocation);
        Team team3 = new Team("test3", "Netball", chchLocation);
        Team team4 = new Team("test4", "Netball", auckLocation);
        Team team5 = new Team("test5", "CheeseEating", chchLocation);
        teamRepository.save(team1);
        teamRepository.save(team2);
        teamRepository.save(team3);
        teamRepository.save(team4);
        teamRepository.save(team5);

        var sports = List.of("Netball", "Hockey");
        var cities = List.of("Christchurch", "Auckland");
        var pageable = PageRequest.of(0, 10);
        var output = teamService.findPaginatedTeamsByCityAndSports(pageable, cities, sports, "").toSet();

        assertEquals(4, output.size());
        assertEquals(Set.of(team1, team2, team3, team4), output);
    }

    @Test
    void filteringByCityAndSport_filterByNameAndOneSportAndCity_correctListReturned() throws Exception {
        Location chchLocation = new Location(null, null, null, "Christchurch", null, "New Zealand");
        Location auckLocation = new Location(null, null, null, "Auckland", null, "New Zealand");

        Team team1 = new Team("test1", "Hockey", chchLocation);
        Team team2 = new Team("test2", "Hockey", auckLocation);
        Team team3 = new Team("test3", "Netball", chchLocation);
        Team team4 = new Team("wrong1", "Netball", auckLocation);
        Team team5 = new Team("wrong2", "CheeseEating", chchLocation);
        teamRepository.save(team1);
        teamRepository.save(team2);
        teamRepository.save(team3);
        teamRepository.save(team4);
        teamRepository.save(team5);

        var sports = List.of("Hockey");
        var cities = List.of("Christchurch");
        var pageable = PageRequest.of(0, 10);
        var output = teamService.findPaginatedTeamsByCityAndSports(pageable, cities, sports, "test").toList();

        assertEquals(1, output.size());
        assertEquals(team1, output.get(0));
    }

    @Test
    void filteringByCityAndSport_emptyFilters_returnEverything() throws Exception {
        Location chchLocation = new Location(null, null, null, "Christchurch", null, "New Zealand");
        Location auckLocation = new Location(null, null, null, "Auckland", null, "New Zealand");

        Team team1 = new Team("test1", "Hockey", chchLocation);
        Team team2 = new Team("test2", "Hockey", auckLocation);
        Team team3 = new Team("test3", "Netball", chchLocation);
        Team team4 = new Team("test4", "Netball", auckLocation);
        Team team5 = new Team("test5", "CheeseEating", chchLocation);
        teamRepository.save(team1);
        teamRepository.save(team2);
        teamRepository.save(team3);
        teamRepository.save(team4);
        teamRepository.save(team5);

        List<String> sports = List.of();
        List<String> cities = List.of();
        var pageable = PageRequest.of(0, 10);
        var output = teamService.findPaginatedTeamsByCityAndSports(pageable, cities, sports, "").toSet();

        assertEquals(5, output.size());
        assertEquals(Set.of(team1, team2, team3, team4, team5), output);
    }

    @Test
    void filteringByCityAndSport_filterByJustName_correctListReturned() throws Exception {
        Location chchLocation = new Location(null, null, null, "Christchurch", null, "New Zealand");
        Location auckLocation = new Location(null, null, null, "Auckland", null, "New Zealand");

        Team team1 = new Team("test1", "Hockey", chchLocation);
        Team team2 = new Team("test2", "Hockey", auckLocation);
        Team team3 = new Team("test3", "Netball", chchLocation);
        Team team4 = new Team("wrong1", "Netball", auckLocation);
        Team team5 = new Team("wrong2", "CheeseEating", chchLocation);
        teamRepository.save(team1);
        teamRepository.save(team2);
        teamRepository.save(team3);
        teamRepository.save(team4);
        teamRepository.save(team5);

        List<String> sports = List.of();
        List<String> cities = List.of();
        var pageable = PageRequest.of(0, 10);
        var output = teamService.findPaginatedTeamsByCityAndSports(pageable, cities, sports, "test").toSet();

        assertEquals(3, output.size());
        assertEquals(Set.of(team1, team2, team3), output);
    }
    @Test
    void filteringByCityAndSport_filterByOneSport_correctListReturned() throws Exception {
        Location chchLocation = new Location(null, null, null, "Christchurch", null, "New Zealand");
        Location auckLocation = new Location(null, null, null, "Auckland", null, "New Zealand");

        Team team1 = new Team("test1", "Hockey", chchLocation);
        Team team2 = new Team("test2", "Hockey", auckLocation);
        Team team3 = new Team("test3", "Netball", chchLocation);
        Team team4 = new Team("wrong1", "Netball", auckLocation);
        Team team5 = new Team("wrong2", "CheeseEating", chchLocation);
        teamRepository.save(team1);
        teamRepository.save(team2);
        teamRepository.save(team3);
        teamRepository.save(team4);
        teamRepository.save(team5);

        List<String> sports = List.of("Hockey");
        List<String> cities = List.of();
        var pageable = PageRequest.of(0, 10);
        var output = teamService.findPaginatedTeamsByCityAndSports(pageable, cities, sports, "").toSet();

        assertEquals(2, output.size());
        assertEquals(Set.of(team1, team2), output);
    }
    @Test
    void filteringByCityAndSport_filterByOneCity_correctListReturned() throws Exception {
        Location chchLocation = new Location(null, null, null, "Christchurch", null, "New Zealand");
        Location auckLocation = new Location(null, null, null, "Auckland", null, "New Zealand");

        Team team1 = new Team("test1", "Hockey", chchLocation);
        Team team2 = new Team("test2", "Hockey", auckLocation);
        Team team3 = new Team("test3", "Netball", chchLocation);
        Team team4 = new Team("team4", "Netball", auckLocation);
        Team team5 = new Team("team5", "CheeseEating", chchLocation);
        teamRepository.save(team1);
        teamRepository.save(team2);
        teamRepository.save(team3);
        teamRepository.save(team4);
        teamRepository.save(team5);

        var sports = List.<String>of();
        var cities = List.<String>of("Christchurch");
        var pageable = PageRequest.of(0, 10);
        var output = teamService.findPaginatedTeamsByCityAndSports(pageable, cities, sports, "").toSet();

        assertEquals(3, output.size());
        assertEquals(Set.of(team1, team3, team5), output);
    }
}
