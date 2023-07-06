package nz.ac.canterbury.seng302.tab.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.controller.ViewAllTeamsController;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.LocationService;
import nz.ac.canterbury.seng302.tab.service.SportService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
public class FilterTeamsBySportFeature {

    @Autowired
    private ApplicationContext applicationContext;

    private UserService userService;
    private TeamService teamService;
    private SportService sportService;
    private LocationService locationService;

    private MockMvc mockMvc;

    private Set<String> selectedSports = new HashSet<>();

    private void setupMorganMocking() throws IOException {

        userService = Mockito.spy(applicationContext.getBean(UserService.class));
        teamService = applicationContext.getBean(TeamService.class);
        locationService = applicationContext.getBean(LocationService.class);
        sportService = applicationContext.getBean(SportService.class);

        // Mock the authentication
        Mockito.doReturn(Optional.of(User.defaultDummyUser())).when(userService).getCurrentUser();

        this.mockMvc = MockMvcBuilders.standaloneSetup(new ViewAllTeamsController(
                teamService, userService, locationService, sportService)).build();
    }

    @Before("@U19_filter_teams_by_sport")
    public void setup() throws IOException {
        setupMorganMocking();

        selectedSports.clear();
    }

    private ResultActions performGet() {
        MockHttpServletRequestBuilder request = get("/view-teams")
                    .with(csrf())
                    .with(anonymous())
                    .param("page", "1");
        for (String sportName : selectedSports) {
            request = request.param("sports", sportName);
        }
        try {
            return mockMvc.perform(request);
        } catch (Exception e) {
            fail(e);
            return null;
        }
    }

    @Given("there are no other teams")
    public void there_are_no_other_teams() {
        teamService.deleteAllTeams();
    }

    @Given("there is a sports team called {string} who plays the sport {string}")
    public void there_is_a_sports_team_called_who_plays_the_sport(String teamName, String sportName) throws IOException {
        Team team = new Team(teamName, sportName);
        teamService.addTeam(team);
    }

    @When("I select the sport {string}")
    public void i_select_the_sport(String sport) {
        selectedSports.add(sport);
    }

    @When("no teams are selected")
    public void no_teams_are_selected() {
        selectedSports.clear();
    }

    @Then("only these teams are selected:")
    public void only_these_teams_are_selected(List<String> expectedTeams) throws Exception {
        Set<String> expectedTeamsSet = Set.copyOf(expectedTeams);
        performGet()
            .andExpect(status().isOk()) // Accepted 200
            .andExpect(view().name("viewAllTeams")) // Rendering the right page
            .andDo(result -> {
                // Check that all the 
                List<Team> listOfTeams = (List<Team>) result.getModelAndView().getModel().get("listOfTeams");
                assertNotNull("The webpage's model does not contain a 'listOfTeams'", listOfTeams);
                Set<String> setOfTeamNames = listOfTeams.stream().map(Team::getName).collect(Collectors.toSet());
                assertEquals(expectedTeamsSet, setOfTeamNames);
            });
    }
}
