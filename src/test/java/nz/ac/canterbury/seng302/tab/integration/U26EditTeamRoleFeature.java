package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class U26EditTeamRoleFeature {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService mockUserService;

    @MockBean
    TeamService teamService;

    Team team;

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
        User testUser = new User("john", "smith", userDOB, "jonh@gmail.com", "test", testLocation);

        when(mockUserService.getCurrentUser()).thenReturn(Optional.of(testUser));
        when(mockUserService.emailIsInUse(anyString())).thenReturn(false);

    }

    @Given("I have created a team")
    public void iHaveCreatedATeam() throws IOException {
        team = new Team("Test Team", "Hockey");
    }

    @WithMockUser()
    @And("I am a manager of the team")
    public void iAmAManagerOfTheTeam() {
        Optional<User> manager = mockUserService.getCurrentUser();
        team.setManager(manager.get());
        team = teamService.addTeam(team);
    }

    @WithMockUser()
    @When("I click on the edit team role button")
    public void iClickOnTheEditTeamRoleButton() throws Exception {
        mockMvc.perform(get("/editTeamRole")
                .param("edit", String.valueOf(team.getTeamId()))).andExpect(status().isFound());
    }

    @Then("I am taken to the edit team members role page")
    public void iAmTakenToTheEditTeamMembersRolePage() throws Exception {
        mockMvc.perform(get("/editTeamRole")
                .param("edit", String.valueOf(team.getTeamId()))).andExpect(status().isFound());
    }
}
