package nz.ac.canterbury.seng302.tab.unit.entity;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.repository.LocationRepository;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TeamTest {
    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamService teamService;

    @Autowired
    private LocationRepository locationRepository;

    private Location location;

    @BeforeEach
    public void beforeEach() {
        teamRepository.deleteAll();
        location = new Location("1 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand");
    }


    @Test
    public void testTeamConstructor() throws IOException {
        List<Team> teamList = teamService.getTeamList();
        assertTrue(teamList.isEmpty());
        Team team = new Team("test", "Hockey", location);
        teamRepository.save(team);
        assertEquals("test", team.getName());
        assertEquals("Christchurch", team.getLocation().getCity());
        assertEquals("Hockey", team.getSport());
    }

    @Test
    public void testGettingTeamId() throws IOException {
        List<Team> teamList = teamService.getTeamList();
        assertTrue(teamList.isEmpty());
        Team team = new Team("test", "Hockey", location);
        teamRepository.save(team);
        assertEquals(1, team.getTeamId());
    }

    @Test
    public void testGettingPictureString() throws IOException {
        List<Team> teamList = teamService.getTeamList();
        assertTrue(teamList.isEmpty());
        Resource resource = new ClassPathResource("/static/image/default-profile.png");
        File file = resource.getFile();
        String pictureString = Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
        Team team = new Team("test", "Hockey", location);
        teamService.addTeam(team);
        assertEquals(pictureString, team.getPictureString());
    }

    @Test
    public void testGettingTeamName() throws IOException {
        List<Team> teamList = teamService.getTeamList();
        assertTrue(teamList.isEmpty());
        Team team = new Team("test", "Hockey", location);
        teamService.addTeam(team);
        assertEquals("test", team.getName());
    }

    @Test
    public void testGettingTeamLocation() throws IOException {
        List<Team> teamList = teamService.getTeamList();
        assertTrue(teamList.isEmpty());
        Team team = new Team("test", "Hockey", location);
        teamService.addTeam(team);
        assertEquals("Christchurch", team.getLocation().getCity());
    }

}