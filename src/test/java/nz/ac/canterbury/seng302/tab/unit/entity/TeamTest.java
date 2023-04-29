package nz.ac.canterbury.seng302.tab.unit.entity;

import nz.ac.canterbury.seng302.tab.controller.ForgotPasswordController;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.LocationRepository;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TeamTest {
    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    UserRepository userRepository;
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

    @Test
    public void checkCreationDateIsToday() throws IOException {
        Team t = new Team("Test", "Sport");
        Assertions.assertEquals(new Date(), t.getCreationDate());
    }

    @Test
    public void testAddTeamsToUser() throws IOException {

        Team team1 = new Team("team1", "cricket");
       Team team2 = new Team("team2", "hockey");

        teamRepository.save(team1);
        teamRepository.save(team2);

        User user1 = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@example.com", "Password123!", new Location(null, null, null, "dunedin", null, "nz"));
        User user2 = new User("Alice", "Smith", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "alice@example.com", "Password123!", new Location(null, null, null, "auckland", null, "nz"));

        Set<Team> teamsToJoinUser1 = new HashSet<>(); //teams to join for user1
        teamsToJoinUser1.add(team1);
        teamsToJoinUser1.add(team2);

        Set<Team> teamsToJoinUser2 = new HashSet<>(); //teams to join for user2
        teamsToJoinUser2.add(team2);

        user1.setJoinedTeams(teamsToJoinUser1); //user is joining teams
        user2.setJoinedTeams(teamsToJoinUser2);

        userRepository.save(user1);
        userRepository.save(user2);

        assertEquals(teamsToJoinUser1, user1.getJoinedTeams());
        assertEquals(teamsToJoinUser2, user2.getJoinedTeams());

    }

}
