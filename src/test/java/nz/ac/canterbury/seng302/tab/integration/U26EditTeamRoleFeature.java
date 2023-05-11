package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
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
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class U26EditTeamRoleFeature {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService mockUserService = mock(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @MockBean
    TeamService teamService = mock(TeamService.class);

    @Autowired
    TeamRepository teamRepository;

    private HttpServletRequest request;

    Team team;

    User testUser;


    @BeforeEach
    void setupUser() throws IOException {
        Date userDOB;
        try {
            // Have to catch a constant parse exception annoyingly
            userDOB = new SimpleDateFormat("YYYY-mm-dd").parse("2000-01-01");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Location testLocation = new Location(null, null, null, "cc", null, "nz");
        testUser = new User("john", "smith", userDOB, "jonh@gmail.com", "test", testLocation);
        userRepository.save(testUser);

        when(mockUserService.getCurrentUser()).thenReturn(Optional.of(testUser));
        when(mockUserService.emailIsInUse(anyString())).thenReturn(false);
    }

    @Given("I have created a team")
    public void iHaveCreatedATeam() throws IOException {
        team = new Team("Test Team", "Hockey");
    }


    @And("I am a manager of the team")
    @WithMockUser()
    public void iAmAManagerOfTheTeam() {
        team.setManager(testUser);
        teamRepository.save(team);
    }

    @WithMockUser()
    @When("I click on the edit team role button")
    public void iClickOnTheEditTeamRoleButton() throws Exception {
        mockMvc.perform(get("/editTeamRole")
                .with(csrf())
                .param("edit", String.valueOf(team.getTeamId()))).andExpect(status().isFound());
    }

    @Then("I am taken to the edit team members role page")
    public void iAmTakenToTheEditTeamMembersRolePage() throws Exception {
        mockMvc.perform(get("/editTeamRole", 42L)
                .param("edit", String.valueOf(team.getTeamId()))).andDo(print()).andExpect(status().isFound())
                .andExpect(view().name("editTeamRoleForm"));
    }
}
