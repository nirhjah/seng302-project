package nz.ac.canterbury.seng302.tab.repository;

import jakarta.persistence.EntityManager;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@DataJpaTest
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
        team = teamRepository.save(team);
        assertEquals(team.getTeamId(), teamRepository.findById(team.getTeamId()).get().getTeamId());
        assertEquals(team.getLocation().getAddressLine2(), teamRepository.findById(team.getTeamId()).get().getLocation().getAddressLine1());
        assertEquals(team.getSport(), teamRepository.findById(team.getTeamId()).get().getSport());
        assertEquals(team.getName(), teamRepository.findById(team.getTeamId()).get().getName());
    }
    @Test
    public void testGettingTeamList() throws IOException {
        assertTrue(teamService.getTeamList().isEmpty());
        Location testLocation = new Location(null, null, null, "Christchurch", null, "New Zealand");
        Location testLocation2 = new Location(null, null, null, "Christchurch", null, "New Zealand");
        Location testLocation3 = new Location(null, null, null, "Christchurch", null, "New Zealand");
        Team team = new Team("test", "Hockey", testLocation);
        Team team2 = new Team("test2", "Netball", testLocation2);
        Team team3 = new Team("test3", "Basketball", testLocation3);
        List<Team> list = Arrays.asList(team, team2, team3);

        teamRepository.save(team);
        teamRepository.save(team2);
        teamRepository.save(team3);

        assertEquals(list.toString(), teamRepository.findAll().toString());
    }


}
