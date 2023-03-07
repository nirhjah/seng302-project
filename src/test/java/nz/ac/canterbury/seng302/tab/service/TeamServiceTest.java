package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class TeamServiceTest {

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamRepository teamRepository;

    @BeforeEach
    public void beforeEach() {
        teamRepository.deleteAll();
    }

    @Test
    public void testGettingTeamList() {
        List<Team> teamList = teamService.getTeamList();
        assertTrue(teamList.isEmpty());
        Team team = new Team("test", "Christchurch", "Hockey");
        Team team2 = new Team("test2", "Auckland", "Netball");
        Team team3 = new Team("test3", "Dunedin", "Basketball");
        List<Team> list = Arrays.asList(team, team2, team3);
        teamRepository.save(team);
        teamRepository.save(team2);
        teamRepository.save(team3);
        teamService.getTeamList();

        assertEquals(list.toString(), teamService.getTeamList().toString());
    }

    @Test
    public void testAddingTeam(){
        Team team = new Team("test", "Christchurch", "Hockey");
        teamService.addTeam(team);
        assertEquals(team.getName(), teamRepository.findById(team.getTeamId()).get().getName());
        assertEquals(team.getLocation(), teamRepository.findById(team.getTeamId()).get().getLocation());
        assertEquals(team.getSport(), teamRepository.findById(team.getTeamId()).get().getSport());
    }

    // TODO Researching how to convert to MultipartFile for updatePicture Params
    @Test
    public void testUpdatingPicture(){}


}


