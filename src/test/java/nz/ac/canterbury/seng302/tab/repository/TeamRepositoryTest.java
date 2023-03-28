package nz.ac.canterbury.seng302.tab.repository;

import jakarta.persistence.EntityManager;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TeamRepositoryTest {
    private EntityManager entityManager;
    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamService teamService;

    @Autowired
    private LocationRepository locationRepository;

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
        assertEquals(team.getLocation().getAddressLine2(), teamRepository.findById(team.getTeamId()).get().getLocation().getAddressLine1());
        assertEquals(team.getSport(), teamRepository.findById(team.getTeamId()).get().getSport());
        assertEquals(team.getName(), teamRepository.findById(team.getTeamId()).get().getName());

    }
    // TODO this test are failing
    @Test
    public void testGettingTeamList() throws IOException {
        assertTrue(teamService.getTeamList().isEmpty());
        Location testLocation = new Location(null, null, null, "Christchurch", null, "New Zealand");
        locationRepository.save(testLocation);
        Team team = new Team("test", "Hockey", locationRepository.findById(testLocation.getLocationId()).get());
        Team team2 = new Team("test2", "Netball", locationRepository.findById(testLocation.getLocationId()).get());
        Team team3 = new Team("test3", "Basketball", locationRepository.findById(testLocation.getLocationId()).get());
        List<Team> list = Arrays.asList(team, team2, team3);

        teamRepository.save(team);
        teamRepository.save(team2);
        teamRepository.save(team3);

        assertEquals(list.toString(), teamRepository.findAll().toString());
    }


    @Test void filteringSearchBySport_filteringAllTeamsByHockey_correctListReturned() throws IOException {
        assertTrue(teamService.getTeamList().isEmpty());
        Team team = new Team("test", "Christchurch", "Hockey");
        Team team2= new Team ("test2", "Auckland", "Netball");

        List<Team> expectedTeamList = List.of(team); // We will filter the search by hockey so only the first team is expected
        teamRepository.save(team);
        teamRepository.save(team2);
        List<String> searchedSports = new ArrayList<String>();
        searchedSports.add("Hockey");
        assertEquals(expectedTeamList.toString(), teamRepository.findTeamByNameAndSportIn(PageRequest.of(0,10), searchedSports, "").toList().toString());

    }

    @Test void filteringSearchBySport_filteringAllTeamsByHockeyAndNetball_correctListReturned() throws IOException {
        assertTrue(teamService.getTeamList().isEmpty());
        Team team = new Team("test", "Christchurch", "Hockey");
        Team team2= new Team ("test2", "Auckland", "Netball");

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
        Team team = new Team("test", "Christchurch", "Hockey");
        Team team2= new Team ("test2", "Auckland", "Netball");
        Team team3 = new Team("Team", "Nelson", "Hockey");

        List<Team> expectedTeamList = List.of(team); // We will filter the search by hockey so only the first team is expected
        teamRepository.save(team);
        teamRepository.save(team2);
        teamRepository.save(team3);
        List<String> searchedSports = new ArrayList<String>();
        searchedSports.add("Hockey");
        assertEquals(expectedTeamList.toString(), teamRepository.findTeamByNameAndSportIn(PageRequest.of(0,10), searchedSports, "test").toList().toString());
    }

    @Test void filteringSearchBySport_filteringAllTeamsAndNoSports_correctListReturned() throws IOException {
        assertTrue(teamService.getTeamList().isEmpty());
        Team team = new Team("test", "Christchurch", "Hockey");
        Team team2= new Team ("test2", "Auckland", "Netball");
        Team team3 = new Team("Team", "Nelson", "Hockey");

        List<Team> expectedTeamList = List.of(team3, team, team2); // We will filter the search by hockey so only the first team is expected
        teamRepository.save(team);
        teamRepository.save(team2);
        teamRepository.save(team3);
        List<String> searchedSports = new ArrayList<>();
        assertEquals(expectedTeamList.toString(), teamRepository.findTeamByNameAndSportIn(PageRequest.of(0,10), searchedSports, "").toList().toString());
    }

//    @Test
//    public void filteringTeamsBySports_noSportsSelected_allSportsDisplayed() throws IOException {
//        List<Team> teamList = teamService.getTeamList();
//        assertTrue(teamList.isEmpty());
//        Team team = new Team("test", "Christchurch", "Hockey");
//        Team team2 = new Team("test2", "Auckland", "Netball");
//        Team team3 = new Team("test3", "Dunedin", "Basketball");
//        List<Team> list = Arrays.asList(team, team2, team3);
//        teamRepository.save(team);
//        teamRepository.save(team2);
//        teamRepository.save(team3);
//
//        Page<Team> filteredTeams = teamRepository.findTeamByNameAndSportIn();
//        assertEquals(list.toString(), filteredTeams.toString());
//
//        teamService.getTeamList();
//    }

}
