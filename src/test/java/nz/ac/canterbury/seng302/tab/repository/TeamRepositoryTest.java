package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TeamRepositoryTest {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamService teamService;

    @BeforeEach
    public void beforeEach(){
        teamRepository.deleteAll();
    }

    @Test
    public void testGettingTeamById() throws IOException {
        List<Team> teamList = teamService.getTeamList();
        assertTrue(teamList.isEmpty());
        Location testLocation = new Location(null, null, null, "Christchurch", null, "New Zealand");
        Team team = new Team("test", "Hockey", testLocation);
        teamRepository.save(team);
        assertEquals(team.getTeamId(), teamRepository.findById(team.getTeamId()).get().getTeamId());
        assertEquals(team.getLocation(), teamRepository.findById(team.getTeamId()).get().getLocation());
        assertEquals(team.getSport(), teamRepository.findById(team.getTeamId()).get().getSport());
        assertEquals(team.getName(), teamRepository.findById(team.getTeamId()).get().getName());

    }

    @Test
    public void testGettingTeamList() throws IOException {
        assertTrue(teamService.getTeamList().isEmpty());
        Location testLocation = new Location(null, null, null, "Christchurch", null, "New Zealand");
        Team team = new Team("test", "Hockey",  testLocation);
        Team team2= new Team ("test2", "Netball", testLocation);
        Team team3= new Team ("test3", "Basketball", testLocation);
        List<Team> list = Arrays.asList(team, team2, team3);

        teamRepository.save(team);
        teamRepository.save(team2);
        teamRepository.save(team3);
        assertEquals(list.toString(), teamRepository.findAll().toString());

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

        assertEquals(expectedTeams.toString(), teamRepository.findTeamByFilteredLocations(filteredLocations, PageRequest.of(0,10)).toList().toString());

    }

    @Test
    public void filteringTeamsByCities_oneCitySelected_allTeamsWithCityDisplayed() throws IOException {
        final String WANTED_CITY = "Christchurch";
        List<Team> teamList = teamService.getTeamList();
        assertTrue(teamList.isEmpty());
        Team team1 = new Team("team1", "Hockey", new Location("123 Test1 road", "", "Suburb1", WANTED_CITY, "1111", "NZ"));
        Team team2 = new Team("team2", "Hockey", new Location("213 Test2 road", "", "Suburb2", "Dunedin", "1111", "NZ"));
        Team team3 = new Team("team3", "Hockey", new Location("321 Test3 road", "", "Suburb3", "Auckland", "1111", "NZ"));

        List<Team> expectedTeams = Arrays.asList(team1);

        teamRepository.save(team1);
        teamRepository.save(team2);
        teamRepository.save(team3);

        ArrayList<String> filteredLocations = new ArrayList<>();
        filteredLocations.add(WANTED_CITY);

        assertEquals(expectedTeams.toString(), teamRepository.findTeamByFilteredLocations(filteredLocations, PageRequest.of(0,10)).toList().toString());

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

        assertEquals(expectedTeams.toString(), teamRepository.findTeamByFilteredLocations(filteredLocations, PageRequest.of(0,10)).toList().toString());

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

        assertEquals(expectedTeams.toString(), teamRepository.findTeamByFilteredLocations(filteredLocations, PageRequest.of(0,10)).toList().toString());

    }
}
