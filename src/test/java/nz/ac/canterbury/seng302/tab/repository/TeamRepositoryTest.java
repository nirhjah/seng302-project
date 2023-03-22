package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

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

    @Autowired
    private SportRepository sportRepository;

    @BeforeEach
    public void beforeEach(){
        teamRepository.deleteAll();
    }

    @Test
    public void testGettingTeamById() throws IOException {
        List<Team> teamList = teamService.getTeamList();
        assertTrue(teamList.isEmpty());
        Team team = new Team("test", "Christchurch", "Hockey");
        teamRepository.save(team);
        assertEquals(team.getTeamId(), teamRepository.findById(team.getTeamId()).get().getTeamId());
        assertEquals(team.getLocation(), teamRepository.findById(team.getTeamId()).get().getLocation());
        assertEquals(team.getSport(), teamRepository.findById(team.getTeamId()).get().getSport());
        assertEquals(team.getName(), teamRepository.findById(team.getTeamId()).get().getName());

    }

    @Test
    public void testGettingTeamList() throws IOException {
        assertTrue(teamService.getTeamList().isEmpty());
        Team team = new Team("test", "Christchurch", "Hockey");
        Team team2= new Team ("test2", "Auckland", "Netball");
        Team team3= new Team ("test3", "Dunedin", "Basketball");
        List<Team> list = Arrays.asList(team, team2, team3);

        teamRepository.save(team);
        teamRepository.save(team2);
        teamRepository.save(team3);
        assertEquals(list.toString(), teamRepository.findAll().toString());

    }

    @Test
    public void filteringTeamsBySports_noSportsSelected_allSportsDisplayed() throws IOException {
        List<Team> teamList = teamService.getTeamList();
        assertTrue(teamList.isEmpty());
        Team team = new Team("test", "Christchurch", "Hockey");
        Team team2 = new Team("test2", "Auckland", "Netball");
        Team team3 = new Team("test3", "Dunedin", "Basketball");
        List<Team> list = Arrays.asList(team, team2, team3);
        teamRepository.save(team);
        teamRepository.save(team2);
        teamRepository.save(team3);

        Page<Team> filteredTeams = teamRepository.filterTeamsBySports();
        assertEquals(list.toString(), filteredTeams.toString());

        teamService.getTeamList();
    }

    @Test
    public void searchTeams_searchWithSingleSport_resultsShown() throws IOException {
        Team team = new Team("test", "Christchurch", "Hockey");
        Team team2= new Team ("test2", "Auckland", "Netball");
        Team team3= new Team ("test3", "Dunedin", "Basketball");
        List<Team> expectedTeams = Arrays.asList(team);

        teamRepository.save(team);
        teamRepository.save(team2);
        teamRepository.save(team3);

        ArrayList<Sport> sports = new ArrayList<>();
        sport.add("Hockey");


        Page<Team> actualTeams = teamRepository.findTeamByNameAndSport("tes", sports, PageRequest.of(0, 10));
        assertEquals(actualTeams.toString(), expectedTeams.toString());
    }
}
