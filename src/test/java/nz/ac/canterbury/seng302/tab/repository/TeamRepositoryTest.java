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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    // TODO this test are failing
    @Test
    public void testGettingTeamList() throws IOException {
        assertTrue(teamService.getTeamList().isEmpty());
        Location testLocation = new Location("123 Test1 road", "", "Suburb1", "Christchurch", "1111", "NZ");
        Team team = new Team("test", "Hockey", new Location("123 Test1 road", "", "Suburb1", "Christchurch", "1111", "NZ"));
        Team team2 = new Team("test2", "Netball", new Location("456 Test2 road", "", "Suburb2", "Auckland", "2222", "NZ"));
        Team team3 = new Team("test3", "Basketball", new Location("789 Test3 road", "", "Suburb3", "Wellington", "3333", "NZ"));
        List<Team> list = Arrays.asList(team, team2, team3);

        teamRepository.save(team);
        teamRepository.save(team2);
        teamRepository.save(team3);

        assertEquals(list.toString(), teamRepository.findAll().toString());
    }


    @Test void filteringSearchBySport_filteringAllTeamsByHockey_correctListReturned() throws IOException {
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

    @Test void filteringSearchBySport_filteringAllTeamsAndNoSports_correctListReturned() throws IOException {
        assertTrue(teamService.getTeamList().isEmpty());
        Location testLocation = new Location(null, null, null, "Christchurch", null, "New Zealand");

        Team team = new Team("test", "Hockey", testLocation);
        Team team2= new Team ("test2", "Netball", testLocation);
        Team team3 = new Team("Team", "Hockey", testLocation);

        List<Team> expectedTeamList = List.of(team3, team, team2); // We will filter the search by hockey so only the first team is expected
        teamRepository.save(team);
        teamRepository.save(team2);
        teamRepository.save(team3);
        List<String> searchedSports = new ArrayList<>();
        assertEquals(expectedTeamList.toString(), teamRepository.findTeamByNameAndSportIn(PageRequest.of(0,10), searchedSports, "").toList().toString());
    }
}
