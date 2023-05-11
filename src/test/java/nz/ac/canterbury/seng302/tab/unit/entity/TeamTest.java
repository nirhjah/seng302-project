package nz.ac.canterbury.seng302.tab.unit.entity;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.entity.TeamRole;
import nz.ac.canterbury.seng302.tab.enums.Role;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
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
    public void GivenATeamIsCreated_WhenIgetTheRoleList_thenTheListWillContainTheManger() throws Exception {
        User user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(),
                "johndoe@example.com", "Password123!", location);

        Team team = new Team("test", "Sport", location, user);
        Set<TeamRole> roles = team.getTeamRoles();
        TeamRole managerRole = roles.stream().findAny().get();
        assertEquals(user, managerRole.getUser());
        assertEquals(Role.MANAGER, managerRole.getRole());
    }

    @Test
    public void GivenIAddAMember_whenICallGetTeamRoleList_thenTheListWillContainTheMember() throws Exception {
        User user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(),
                "johndoe@example.com", "Password123!", location);

        Team team = new Team("test", "Sport", location, user);

        User member = new User("Jane", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(),
                "JaneDoe@example.com", "Password123!", location);
        team.setMember(user);
        Set<TeamRole> roles = team.getTeamRoles();
        assertEquals(2, roles.size());
        var hasOneMember = roles.stream().filter((teamRole) -> teamRole.getRole() == Role.MANAGER).count() == 1;
        assertTrue(hasOneMember, "didn't have one member");
    }

    @Test
    public void GivenIAddACoach_whenICallGetTeamRoleList_thenTheListWillContainTheCoach() throws Exception {
        User user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(),
                "johndoe@example.com", "Password123!", location);

        Team team = new Team("test", "Sport", location, user);

        User coach = new User("Jane", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(),
                "JaneDoe@example.com", "Password123!", location);
        team.setCoach(user);
        Set<TeamRole> roleList = team.getTeamRoles();
        var hasOneCoach = roleList.stream().filter((teamRole) -> teamRole.getRole() == Role.COACH).count() == 1;
        var hasOneManager = roleList.stream().filter((teamRole) -> teamRole.getRole() == Role.MANAGER).count() == 1;
        assertTrue(hasOneCoach, "doesn't have one coach");
        assertTrue(hasOneManager, "doesn't have one manager");
    }

    @Test
    public void testAddTeamsToUser() throws IOException {

        Team team1 = new Team("team1", "cricket");
       Team team2 = new Team("team2", "hockey");

        teamRepository.save(team1);
        teamRepository.save(team2);

        User user1 = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@example.com", "Password123!", new Location(null, null, null, "dunedin", null, "nz"));
        User user2 = new User("Alice", "Smith", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "alice@example.com", "Password123!", new Location(null, null, null, "auckland", null, "nz"));

        //expected
        Set<Team> user1ExpectedTeams = new HashSet<>();
        Set<Team> user2ExpectedTeams = new HashSet<>();
        user1ExpectedTeams.add(team1);
        user1ExpectedTeams.add(team2);
        user2ExpectedTeams.add(team2);

        //output
        user1.joinTeam(team1);
        user1.joinTeam(team2);
        user2.joinTeam(team2);

        assertEquals(user1ExpectedTeams, user1.getJoinedTeams());
        assertEquals(user2ExpectedTeams, user2.getJoinedTeams());

    }

    /**
     * U24/AC5 states that a token must be 12 characters long
     *
     * @throws IOException
     */
    @Test
    public void givenCreatingANewUser_WhenTokenIsGenerated_TokenIs12CharactersLong() throws IOException {
        Team t = new Team("abc", "soccer");
        t.generateToken(teamService);
        assertEquals(t.getToken().length(), 12);
    }

    /**
     * U24/AC5 states that a token must a combination of letters and numbers
     *
     * @throws IOException
     */
    @Test
    public void givenCreatingANewUser_WhenTokenIsGenerated_TokenIsMadeOfOnlyCharactersAndNumbers()
            throws IOException {
        Team t = new Team("abc", "soccer");
        t.generateToken(teamService);
        assertTrue(t.getToken().matches("^[a-zA-Z0-9]*$"));
    }
}
