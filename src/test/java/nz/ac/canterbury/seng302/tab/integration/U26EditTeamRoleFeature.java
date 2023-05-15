package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class U26EditTeamRoleFeature {

    @Autowired
    MockMvc mockMvc;

    @Mock
    UserService mockUserService = mock(UserService.class);

    @InjectMocks
    private UserRepository userRepository;

    @Mock
    TeamService teamService = mock(TeamService.class);

    @InjectMocks
    TeamRepository teamRepository;

    private HttpServletRequest request;

    Team team;

    User user;

    static long TEAM_ID = 1;

    @Before("@WithAdminUser")
    public void setupAdminUser() throws IOException {
        Location testLocation = new Location(null, null, null, "CHCH", null, "NZ");
        user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(),
                "johndoe@example.com", "Password123!", testLocation);

        Mockito.when(mockUserService.getCurrentUser()).thenReturn(Optional.of(user));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        "N/A",
                        user.getAuthorities()));
    }


    @Before
    void setupUser(final Scenario scenario) throws IOException {
        MockitoAnnotations.openMocks(this);
    }

    @Given("I have created a team")
    public void iHaveCreatedATeam() throws IOException {
        team = new Team("Test Team", "Hockey");
        team = Mockito.spy(team);
        Mockito.when(team.getTeamId()).thenReturn(TEAM_ID);
        Mockito.when(teamService.getTeam(TEAM_ID)).thenReturn(team);
    }


    @And("I am a manager of the team")
    public void iAmAManagerOfTheTeam() {
        team.setManager(user);
    }

    @When("I click on the edit team role button")
    public void iClickOnTheEditTeamRoleButton() throws Exception {
        Mockito.when(teamService.getTeam(TEAM_ID)).thenReturn(team);
        Mockito.doReturn(team).when(teamService).getTeam(TEAM_ID);

        Mockito.when(team.getTeamId()).thenReturn(TEAM_ID);
        Mockito.doReturn(TEAM_ID).when(team).getTeamId();
        Mockito.doReturn(Optional.of(user)).when(mockUserService).getCurrentUser();
        Mockito.when(mockUserService.getCurrentUser()).thenReturn(Optional.of(user));

        mockMvc.perform(get("/editTeamRole")
                .param("edit", String.valueOf(team.getTeamId()))).andExpect(status().isFound()).andExpect(view().name("editTeamRoleForm"));
    }

    @Then("I am taken to the edit team members role page")
    public void iAmTakenToTheEditTeamMembersRolePage() throws Exception {
        //Mockito.doReturn(Optional.of(user)).when(mockUserService).getCurrentUser();
        Mockito.when(mockUserService.getCurrentUser()).thenReturn(Optional.of(user));
        mockMvc.perform(get("/editTeamRole", 42L)
                .param("edit", String.valueOf(team.getTeamId()))).andDo(print()).andExpect(status().isFound())
                .andExpect(view().name("editTeamRoleForm"));
    }
}
