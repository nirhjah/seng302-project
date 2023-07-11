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

import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
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
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.LocationService;
import nz.ac.canterbury.seng302.tab.service.SportService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;

/**
 * These step defs cover both U19 and U20, because they both cover
 * elements of the same system (querying teams)
 */
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
public class U19_U20_FilterTeams {

    @Autowired
    private ApplicationContext applicationContext;

    private UserService userService;
    private TeamService teamService;
    private TeamRepository teamRepository;
    private SportService sportService;
    private LocationService locationService;

    private MockMvc mockMvc;

    private Set<String> selectedSports = new HashSet<>();
    private Set<String> selectedCities = new HashSet<>();

    private static final String TEST_SPORT = "TEST_SPORT";

    private void setupMorganMocking() throws IOException {

        userService = Mockito.spy(applicationContext.getBean(UserService.class));
        teamService = applicationContext.getBean(TeamService.class);
        locationService = applicationContext.getBean(LocationService.class);
        sportService = applicationContext.getBean(SportService.class);
        teamRepository = applicationContext.getBean(TeamRepository.class);

        // Mock the authentication
        Mockito.doReturn(Optional.of(User.defaultDummyUser())).when(userService).getCurrentUser();

        this.mockMvc = MockMvcBuilders.standaloneSetup(new ViewAllTeamsController(
                teamService, userService, locationService, sportService
            )).build();
    }

    @Before("@view_teams_page_filtering")
    public void setup() throws IOException {
        setupMorganMocking();

        selectedSports.clear();
        selectedCities.clear();
    }

    /**
     * Performs the get request to the <code>/view-teams</code> page
     * @return The result of the request, so you can chain <code>.andExpect(...)</code>
     */
    private ResultActions performGet() {
        // Build the request
        MockHttpServletRequestBuilder request = get("/view-teams")
                .with(csrf())       // Required as the post is a form
                .with(anonymous())  // Pretend we're logged in
                .param("page", "1");
        // Populate the dropdowns
        for (String sportName : selectedSports)
            request = request.param("sports", sportName);
        for (String cityName : selectedCities)
            request = request.param("cities", cityName);
        try {
            return mockMvc.perform(request);
        } catch (Exception e) {
            fail(e);
            return null;
        }
    }

    /**
     * The initial step to ensure no other teams are in the database.
     * 
     * If you do this during setup(), it won't work, and teams
     * from other tests (ViewMyActivitiesIntegrationTests) stay.
     */
    @Given("there are no other teams")
    public void there_are_no_other_teams() {
        teamRepository.deleteAll();
    }

    /**
     * U19 - Populate the database, specifying the <strong>sport</strong>
     */
    @Given("there is a sports team called {string} who plays the sport {string}")
    public void there_is_a_sports_team_called_who_plays_the_sport(String teamName, String sportName) throws IOException {
        Team team = new Team(teamName, sportName);
        teamService.addTeam(team);
    }

    /**
     * U20 - Populate the database, specifying the <em>city</em>
     */
    @Given("there is a sports team called {string} located in {string}")
    public void there_is_a_sports_team_called_located_in(String teamName, String cityName) throws IOException {
        Location location = new Location("", "", "", cityName, "", "Test Country");
        Team team = new Team(teamName, TEST_SPORT, location);
        teamService.addTeam(team);
    }
    /**
     * U19 - Add a <em>sport</em> to the dropdown
     */
    @When("I select the sport {string}")
    public void i_select_the_sport(String sport) {
        selectedSports.add(sport);
    }

    /**
     * U20 - Add a <em>city</em> to the dropdown
     */
    @When("I select the city {string}")
    public void i_select_the_city(String city) {
        selectedCities.add(city);
    }

    /**
     * U19 - Explicitly state that the sportsdropdown is empty
     */
    @When("no sports are selected")
    public void no_sports_are_selected() {
        selectedSports.clear();
    }

    /**
     * U19 & U20 - Asserts that the provided list of teams matches.
     */
    @Then("only these teams are selected:")
    @SuppressWarnings("unchecked")
    public void only_these_teams_are_selected(List<String> expectedTeams) throws Exception {
        // We want the expectedTeams to exactly match the resulting teams
        // but we don't care about order
        Set<String> expectedTeamsSet = Set.copyOf(expectedTeams);
        performGet()
            .andExpect(status().isOk()) // Accepted 200
            .andExpect(view().name("viewAllTeams")) // Rendering the right page
            .andDo(result -> {
                // Check that all the teams are present
                List<Team> listOfTeams = (List<Team>) result.getModelAndView().getModel().get("listOfTeams");
                assertNotNull("The webpage's model does not contain a 'listOfTeams'", listOfTeams);
                Set<String> setOfTeamNames = listOfTeams.stream().map(Team::getName).collect(Collectors.toSet());
                assertEquals(expectedTeamsSet, setOfTeamNames);
            });
    }

}
